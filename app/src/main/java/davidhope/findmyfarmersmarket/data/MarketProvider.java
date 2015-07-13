package davidhope.findmyfarmersmarket.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by David on 4/2/2015.
 */
public class MarketProvider extends ContentProvider {

    public static final String LOG_TAG = MarketProvider.class.getSimpleName();

    private MarketDbHelper mMarketDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {

        Log.i(LOG_TAG, "MarketProvider has been created");

        mMarketDbHelper = new MarketDbHelper(getContext());

        return true;
    }

    // Market Detail
    static final int MARKET_DETAIL = 100;
    static final int MARKETS_BY_ZIPCODE = 101;
    static final int MARKETS_BY_LAT_LNG = 102;

    private static final String DETAIL_QUERY_STRING = MarketContract.SearchResultsTable.MARKET_TABLE_NAME
            + "." + MarketContract.SearchResultsTable.COLUMN_MARKET_IDS + " = ? ";

    private static final String ZIPCODE_QUERY_STRING = MarketContract.SearchResultsTable.MARKET_TABLE_NAME
            + "." + MarketContract.SearchResultsTable.COLUMN_ZIPCODE + " = ? ";

    private static final String LAT_LNG_QUERY_STRING = MarketContract.SearchResultsTable.MARKET_TABLE_NAME
            + "." + MarketContract.SearchResultsTable.COLUMN_LAT_LNG + " = ? ";


    // todo Possibly need later.
//    @Override
    public String getType(Uri uri) {

        Log.i(LOG_TAG, "getType() has been created");

        //TODO: Add type for MarketDetail as well.

        // Possibly instantiate outside of method and at top of class instead?
        final int match = sUriMatcher.match(uri);
        switch (match) {

            // todo: Change to use CONTENT_ITEM_TYPE for to-be-created detail table, possibly for regular table too.

            case MARKET_DETAIL:
                return MarketContract.SearchResultsTable.CONTENT_ITEM_TYPE;


            case MARKETS_BY_ZIPCODE:
                return MarketContract.SearchResultsTable.CONTENT_TYPE;


            case MARKETS_BY_LAT_LNG:
                return MarketContract.SearchResultsTable.CONTENT_TYPE;



            default:
                Log.d(LOG_TAG, Log.getStackTraceString(new Throwable()));
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }


    public  static UriMatcher buildUriMatcher() {

        Log.i(LOG_TAG, "UrIMatcher has been created");

        //TODO: Need uriMatcher.addUri() for MarketDetail as well.
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Try using .getAuthority() from Uri class.
        String authority = MarketContract.CONTENT_AUTHORITY;

        // Base Uris for creating the specific paths.

        uriMatcher.addURI(authority, MarketContract.PATH_MARKET_SEARCH, MARKET_DETAIL);
        uriMatcher.addURI(authority, MarketContract.PATH_MARKET_SEARCH, MARKETS_BY_ZIPCODE);
        uriMatcher.addURI(authority, MarketContract.PATH_MARKET_SEARCH, MARKETS_BY_LAT_LNG);


        // Change to detail path later for detailTable
        uriMatcher.addURI(authority, MarketContract.PATH_MARKET_SEARCH + "/Detail/#", MARKET_DETAIL);

        uriMatcher.addURI(authority, MarketContract.PATH_MARKET_SEARCH + "/Zipcode/*", MARKETS_BY_ZIPCODE);

        //TODO: Must add appropriate data type to create Uri addition.
        uriMatcher.addURI(authority, MarketContract.PATH_MARKET_SEARCH +  "/LatLng/*" , MARKETS_BY_LAT_LNG);

        return uriMatcher;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.i(LOG_TAG, "Cursor query from MarketProvider has been created");


        // Use in EVERY method/function to see how they're created & implemented.
        Log.d(LOG_TAG, Log.getStackTraceString(new Throwable()));

        Cursor retCursor;

        // todo: possibly need int and sUriMatcher combo to create, get, Uris for provider to access C.R.U.D

//        uri = MarketContract.SearchResultsTable.CONTENT_URI;
//        int UriMatcher = sUriMatcher.match(uri);


//         MarketContract.SearchResultsTable.getUriData(MarketContract.SearchResultsTable.CONTENT_URI);

//        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();


        switch (sUriMatcher.match(uri)) {

        /*case MILES_TO: {
                retCursor= mMarketDbHelper.getWritableDatabase().query(
                        MarketContract.SearchResultsTable.SEARCH_RESULTS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            } break;*/

      case MARKET_DETAIL: {
                retCursor = mMarketDbHelper.getReadableDatabase().query(
                        MarketContract.SearchResultsTable.MARKET_TABLE_NAME,
                        projection,
                        DETAIL_QUERY_STRING,
//                        selection,
//                        new String[] {MarketContract.SearchResultsTable.getUriData(uri)},
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            } break;

       case MARKETS_BY_ZIPCODE: {
                retCursor = mMarketDbHelper.getReadableDatabase().query(
                        MarketContract.SearchResultsTable.MARKET_TABLE_NAME,
                        projection,
//                        selection,
                        ZIPCODE_QUERY_STRING,
//                        new String[]{MarketContract.SearchResultsTable.getUriData(uri)},
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

            } break;

            // TODO: Possibly go back to use "projection" instead of new String[]s
           case MARKETS_BY_LAT_LNG: {
                retCursor = mMarketDbHelper.getReadableDatabase().query(
                        MarketContract.SearchResultsTable.MARKET_TABLE_NAME,
                        projection,
//                        selection,
                        LAT_LNG_QUERY_STRING,
//                        new String[] {MarketContract.SearchResultsTable.getUriData(uri)},
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }
            break;

            default:
                Log.d(LOG_TAG, Log.getStackTraceString(new Throwable()));
                throw new UnsupportedOperationException("Unsupported uri : " + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }




     @Override
    public Uri insert(Uri uri, ContentValues values) {

//        final SQLiteDatabase database = mMarketDbHelper.getWritableDatabase();
//        final int match = sUriMatcher.match(uri);

         Log.i(LOG_TAG, "Uri ContentValues insert from MarketProvider has been created");
//         Uri returnUri;

    /*    switch (match) {

            case MARKET_DETAIL: {

                long _id = database.insert(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MarketContract.SearchResultsTable.buildDetailPath(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }*/

//            case MILES_TO: {

//                long _id = database.insert(MarketContract.SearchResultsTable.SEARCH_RESULTS, null, values);
//                if ( _id > 0 )
//                    returnUri = MarketContract.SearchResultsTable.buildDetailPath(_id);
//                else
//                    throw new android.database.SQLException("Failed to insert row into " + uri);
//                break;
//            }

           /* case MARKETS_BY_LAT_LNG: {

                long _id = database.insert(MarketContract.SearchResultsTable.SEARCH_RESULTS, null, values);
                if ( _id > 0 )
                    returnUri = MarketContract.SearchResultsTable.buildLatLngPath(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }*/

//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//         returnUri = uri;
//        }

        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = mMarketDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        Log.i(LOG_TAG, "Uri delete from MarketProvider has been created");

        //TODO: Add case statement for MarketDetail Table once created.

        if ( null == selection ) selection = "1";

        switch (match) {
            case MARKET_DETAIL:
                rowsDeleted = database.delete(MarketContract.SearchResultsTable.MARKET_TABLE_NAME, selection,
                        selectionArgs
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if ( rowsDeleted != 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    // todo Must fix update()

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = mMarketDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsUpdated;

        Log.i(LOG_TAG, "Uri update from MarketProvider has been created");

        //TODO: Add case statement for MarketDetail Table once created.

        if ( null == selection ) selection = "1";

        switch (match) {

//            case MARKET_DETAIL:
//                rowsUpdated = database.update(MarketContract.SearchResultsTable.SEARCH_RESULTS,
//                        values,
//                        selection,
//                        selectionArgs
//                );
//                break;

//            case MILES_TO:
//                rowsUpdated = database.update(MarketContract.SearchResultsTable.SEARCH_RESULTS,
//                        values,
//                        selection,
//                        selectionArgs);
//                break;

          case MARKETS_BY_LAT_LNG:
                rowsUpdated = database.update(MarketContract.SearchResultsTable.MARKET_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if ( rowsUpdated != 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mMarketDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        Log.i(LOG_TAG, "bulkInsert from MarketProvider has been created");

//      switch (match) {
//            case MARKETS_BY_LAT_LNG:
                db.beginTransaction();
                int returnCount = 1;
                // int returnCount = 0;
                try {
                    for ( ContentValues value : values ) {
                        long _id = db.insert(MarketContract.SearchResultsTable.MARKET_TABLE_NAME, null, value);
                        if ( _id != -1 ) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
//            default:
//                return super.bulkInsert(uri, values);
//        }
    }

}
