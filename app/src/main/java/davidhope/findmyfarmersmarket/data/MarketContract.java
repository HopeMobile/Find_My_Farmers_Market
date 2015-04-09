package davidhope.findmyfarmersmarket.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by David on 4/2/2015.
 */
public class MarketContract extends ContentProvider {

    //The Unique identifier for your ContentProvider.
    public static final String CONTENT_AUTHORITY = "davidhope.findmyfarmersmarket";

    // Base Uri used by ContentProvider and accessed by Outside Applications for their needs.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Potential paths to be used for retrieving data from the SQLite Tables.
    public static final String PATH_MARKET_NAMES = "marketnames";


    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    //Table for search results.
    public static abstract class SearchResultsTable implements BaseColumns  {

      public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MARKET_NAMES).build();

      public static final String CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
              + " / " + CONTENT_AUTHORITY + " / " + PATH_MARKET_NAMES;


      public static final String TABLE_NAME = "searchResults";

      // ID returned by results to identify each individual market and required for inner-join.
      public static final String COLUMN_MARKET_IDS = "id";

      // Used to display market distance from users.
      public static final String COLUMN_MILES_TO_MARKET = "milesTo";

      // Names of each market returned by search and used for more detail in MarketDetailsTable
      public static final String COLUMN_MARKET_NAME = "marketname";

    }

    //TODO: Create table for detailed market information in version 2.0
    public static abstract class MarketDetailsTable implements BaseColumns {

    }
}
