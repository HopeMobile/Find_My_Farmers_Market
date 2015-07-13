package davidhope.findmyfarmersmarket.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by David on 4/2/2015.
 */
public class MarketContract  {

    // Base Uri used by ContentProvider and accessed by Outside Applications for their needs.
//    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Table for search results.


    //The Unique identifier for your ContentProvider.
    public static final String CONTENT_AUTHORITY = "davidhope.findmyfarmersmarket";

    // Potential paths to be used for retrieving data from the SQLite Tables.
    public static final String PATH_MARKET_SEARCH = "marketDatabase";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        /* public static  Uri buildDetailPath(long id) {
         return CONTENT_URI.buildUpon().appendPath("Detail").appendPath(String.valueOf(id)).build();
      }

      //TODO: Possibly use to get marketNames instead of buildDetailPath.
      public static Uri buildZipcodePath(Uri zipcode) {
          return CONTENT_URI.buildUpon().appendPath("Zipcode").appendPath(String.valueOf(zipcode)).build();
      }

        public static  Uri buildLatLngPath(String latLng) {
            return CONTENT_URI.buildUpon().appendPath("LatLng").appendPath(latLng).build();
        }*/


    // Copies of original buildUris(), to test Uri Exception fixes.

    public static abstract class SearchResultsTable implements BaseColumns  {

        // Uri for experimenting on to find solution.
//        public static final Uri CONTENT_URI  = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MARKET_SEARCH).buildUpon().build();

        // todo Will only need for detail layout.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MARKET_SEARCH;

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MARKET_SEARCH;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MARKET_SEARCH).build();

//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
//                .appendPath(PATH_MARKET_SEARCH).build();


        //TODO Use substring on MILES_TO_MARKET and MARKET_NAME column values

        //TODO: Possibly change from "id" to "ids"
        // ID returned by results to identify each individual market and required for inner-join.

        public static final String MARKET_TABLE_NAME = "marketTableName";

        public static final String COLUMN_ZIPCODE = "zipcode";

        public static final String COLUMN_LAT_LNG = "latLng";

        // todo: Might not need COLUMN_MARKET_IDS, but just use _ID instead for same data.
        public static final String COLUMN_MARKET_IDS = "id";

        // Used to display market distance from users.
        public static final String COLUMN_MILES_TO_MARKET = "milesTo";

        // Names of each market returned by search and used for more detail in MarketDetailsTable
        public static final String COLUMN_MARKET_NAME = "marketName";


//        public static String getUriData(Uri uri) {
//            return uri.getPathSegments().get(2);
//        }

    }

    //TODO: Create table for detailed market information in version 2.0
  /*  public static abstract class MarketDetailsTable implements BaseColumns {

    } */
}
