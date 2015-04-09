package davidhope.findmyfarmersmarket.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by David on 4/2/2015.
 */
public class MarketProvider extends ContentProvider {

   private static final UriMatcher sUriMatcher = buildUriMatcher();
   private MarketDbHelper mMarketDbHelper;

    //TODO: Add ints for MarketDetail and likely milesTo as well.
   static final int MARKET_IDS = 100;
   static final int MARKET_NAMES = 101;
    // Need to use SQLiteQueryBuilder once MarketDetailTable and Fragment created.
   //private static final SQLiteQueryBuilder MarketQueryBuilder;

    static UriMatcher buildUriMatcher() {

    //TODO: Need uriMatcher.addUri() for MarketDetail and likely milesTo as well.
     final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

      String authority = MarketContract.CONTENT_AUTHORITY;

      uriMatcher.addURI(authority, MarketContract.PATH_MARKET_NAMES, MARKET_NAMES);

      uriMatcher.addURI(authority, MarketContract.PATH_MARKET_NAMES + "/*", MARKET_IDS);

       //TODO: Must add appropriate data type to create Uri addition.
       uriMatcher.addURI(authority, MarketContract.PATH_MARKET_NAMES + "/*", MARKET_NAMES);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        mMarketDbHelper = new MarketDbHelper(getContext(), null, null, 0);

        return true;
    }


    @Override
    public String getType(Uri uri) {

      //TODO: Add type for MarketDetail and likely Market Id, milesTo as well.

      final int match = sUriMatcher.match(uri);
        switch (match) {
            case MARKET_NAMES:
                return MarketContract.SearchResultsTable.CONTENT_TYPE;

            default: throw new
                    UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case MARKET_NAMES: {
                retCursor = mMarketDbHelper.getReadableDatabase().query(
                        MarketContract.SearchResultsTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }

            break;

              default:
                  throw new UnsupportedOperationException("Unsupported uri :" + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

     final SQLiteDatabase database = mMarketDbHelper.getWritableDatabase();
      Uri returnUri;

       Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case MARKET_NAMES: {

                long _id = database.insert(MarketContract.SearchResultsTable.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MarketContract.BASE_CONTENT_URI;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
             default:
                 throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
      }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = mMarketDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        //TODO: Add case statement for MarketDetail Table once created.

        if (null == selection) selection = "1";

         switch (match) {
             case MARKET_NAMES:
                 rowsDeleted = database.delete(MarketContract.SearchResultsTable.TABLE_NAME, selection,
                         selectionArgs
                 );
                 break;

          default:
           throw  new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = mMarketDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsUpdated;

        //TODO: Add case statement for MarketDetail Table once created.

        if (null == selection) selection = "1";

        switch (match) {
            case MARKET_NAMES:
                rowsUpdated = database.update(MarketContract.SearchResultsTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;

            default:
                throw  new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMarketDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MARKET_NAMES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MarketContract.SearchResultsTable.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
