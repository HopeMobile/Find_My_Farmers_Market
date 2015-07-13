package davidhope.findmyfarmersmarket.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import davidhope.findmyfarmersmarket.SearchFragment;
import davidhope.findmyfarmersmarket.data.MarketContract;


/**
 * Created by David on 3/27/2015.
 */
public class FetchMarketService extends IntentService  {

    //protected ArrayList<String> searchResultData = new ArrayList();
    private static final String  LOG_TAG = FetchMarketService.class.getSimpleName();
    protected static final String  SEARCH_TAG = SearchFragment.class.getSimpleName();

    private static final String LAT = "lat";
    private static final String LGN = "lgn";

    protected static final String LOCATION_QUERY_STRING = "lqe";

    protected static final String[] marketDetailQuery = {"COLUMN_MARKET_IDS", "COLUMN_MILES_TO", "COLUMN_MARKET_NAMES"};

    protected static final int[] INDEX_OF_COLUMNS = {0, 1, 2};

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param //name Used to name the worker thread, important only for debugging.
     */

    public FetchMarketService() {
        super("FetchMarketService");

    }

    //TODO: Create boolean to check if user has selected individual market from list and start URI for it.

      private String marketStreamString;

      String  locationQuery;
      Context mContext = getBaseContext();

      String mIds = "mIds";
      String milesTo = "milesTo";
      String marketName = "marketName";

     private JSONObject json2 = new JSONObject();
     private JSONArray jsonResultsArray;

      private String tempStringIds;
      private String tempStringMiles;
      private String tempStringMarkets;

      private String id;
      private String miles;
      private String markets;


    final StringBuilder  sbIds = new StringBuilder();
    final StringBuilder sbMilesTo = new StringBuilder();
    final StringBuilder sbMarketNames = new StringBuilder();

    // Double String[]s for holding json values from Input Stream.
    String[][] mIdArray = new String[19][19];
    String[][] mMilesToArray = new String[19][19];
    String[][] mMarketNamesArray = new String[19][19];

    // Bank of columns to add values for checking inside Content Values.
    protected String[] cvIds = new String[19];
    protected String[] cvMilesTo = new String[19];
    protected String[] cvMarkets = new String[19];

//    protected Cursor cursor;
//    protected String selection;
//    protected String[] projection;

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(LOG_TAG, "onHandleIntent has been called");

        //TODO: 1. Add data from Strings to Database via ContentResolver and/or possibly Loaders.

        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        BufferedInputStream bufferedInputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;

        URL zipCodeUrl;

        // Example of Lat-Lng call with A.P.I
        //  http://search.ams.usda.gov/farmersmarkets/v1/data.svc/locSearch?lat=33.8611556&lng=-84.1408

        final String BASE_URL = "http://search.ams.usda.gov/farmersmarkets/v1/data.svc/";
        final String ZIP_SEARCH = "zipSearch";
        final String ZIP_PARAM = "zip";
        final String LOCATION_QUERY_PARAM = "locSearch";
        final String LATITUDE = "?lat=";
        final String LONGITUDE = "&lng=";
      //  final String MARKET_DETAIL = "mktDetail";
      //  final String MARKET_ID = "?id=";

        // Use this API endpoint to get the details for any specific selected farmers market
       // final String marketDetailResults = "http://search.ams.usda.gov/farmersmarkets/v1/data.svc/mktDetail?ids=";

        try {

            locationQuery = intent.getStringExtra(Intent.EXTRA_TEXT);



            if (locationQuery == null) {

                double userLat = 0.0;
                double userLng = 0.0;

                userLat = intent.getDoubleExtra(LAT, 0);
                userLng = intent.getDoubleExtra(LGN, 0);

                zipCodeUrl = new URL(BASE_URL + LOCATION_QUERY_PARAM + LATITUDE + userLat + LONGITUDE + userLng);

                Log.v(LOG_TAG, "" + userLat);
                Log.v(LOG_TAG, "" + userLng);
                httpURLConnection = (HttpURLConnection) zipCodeUrl.openConnection();
            }
            else {

                // Builds URI, creates the properly formatted path and query parameters.
               /* Uri.Builder zipCodeUriBuilder = new Uri.Builder()
                        .encodedPath(BASE_URL)
                        .appendEncodedPath(ZIP_SEARCH)
                        .appendQueryParameter(ZIP_PARAM, locationQuery); */

               // zipCodeUrl = new URL(zipCodeUriBuilder.build().toString());

                zipCodeUrl = new URL(BASE_URL + ZIP_SEARCH + "?zip=" + locationQuery);
                httpURLConnection = (HttpURLConnection) zipCodeUrl.openConnection();
            }


            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                Log.v(LOG_TAG, "Connection was successful!!!");

                inputStream = httpURLConnection.getInputStream();
                if (inputStream == null) {
                    return;
                }
                else {

                    bufferedInputStream = new BufferedInputStream(inputStream);
                    inputStreamReader = new InputStreamReader(bufferedInputStream);
                    bufferedReader = new BufferedReader(inputStreamReader);


                    StringBuilder sbStream = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sbStream.append(line);
                        Log.v(LOG_TAG, line);
                    }


                    marketStreamString = new String(sbStream);

                    parseJson();

                        //TODO: Reminder, just need to put Strings into ContentValues without additional String[] to index.

                }

                // Closes the input stream once every character has been read from it.
                if (bufferedReader.read() <= -1) {
                    inputStream.close();
                }

            } else {
                Log.e(LOG_TAG, "Bummer, the connection failed");

            }

        } catch (MalformedURLException e) {
           Log.e(LOG_TAG, "MalformedURLException " + e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException " + e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException " + e);
        }

    }

    private void parseJson() throws JSONException {
        JSONObject jsonObject = new JSONObject(marketStreamString);

        jsonResultsArray = jsonObject.getJSONArray("results");

        for (int i = 0; i < jsonResultsArray.length(); i++ ) {

            json2 = jsonResultsArray.getJSONObject(i);

            sbIds.append(json2.getString("id")).append(" , ");
            tempStringIds = new String(sbIds);
            mIdArray = new String[][]{tempStringIds.split(" , ")};

            parseIds();

            sbMilesTo.append(json2.getString("marketname").substring(0, 4)).append(" , ");
            tempStringMiles = new String(sbMilesTo);
            mMilesToArray = new String[][] {tempStringMiles.split(" , ")};

            parseMiles();

            sbMarketNames.append(json2.getString("marketname").substring(4)).append(" , ");
            tempStringMarkets = new String(sbMarketNames);

            mMarketNamesArray = new String[][]{tempStringMarkets.split(" , ")};

            parseMarkets();

        }

        // todo Fix LatLng error, which is being stored in Json variables, methods, and here
        if ( cvIds != null  && cvMilesTo != null && cvMarkets != null ) {
            checkContentValues(cvIds, cvMilesTo, cvMarkets);
        }
    }

    private void parseIds() {
        for ( String[] string : mIdArray ) {
            cvIds  = string;
        }
    }


    private void parseMiles() {
        for ( String[] string : mMilesToArray ) {
            cvMilesTo  = string;

        }
    }

    private void parseMarkets() {
        for ( String[] string : mMarketNamesArray ) {
            cvMarkets  = string;
        }
    }

    // TODO: Three possibly options for adding and holding data needed for ContentValues()

        //TODO: Fix method to only insert if data has not previously been saved.
        //TODO: Must recognize when location is null and log the resulting exception.


    // boolean for checking if cv[] values exist in provider.
   /*private  boolean  checkCursorValues(Cursor c, String[] stringArray ) {

        cvIds = stringArray ;
        Cursor cursor = getContentResolver()
                .query(MarketContract.SearchResultsTable.CONTENT_URI,
                        cvIds,
                        null,
                        null,
                        null);

        return true;
    }
*/

    // Method checks if values exist in content provider and inserts if they don't.
    private void checkContentValues(String[] arrayIds, String[] arrayMilesTo, String[] arrayMarketNames)
            throws JSONException {

        // 1. Determine if values from cvIds, cvMilesTo, and cvMarkets exist in provider, Database.
        // 2. If step 1 false, then bulkInsert ContentValues.
        // 3. Query once it exists and place in Adapter or Loader for display in List.

        cvIds = arrayIds;
        cvMilesTo = arrayMilesTo;
        cvMarkets = arrayMarketNames;

        // TODO: Possibly use to query and verify existence of values in provider.
        long initialCursorPosition = -1;


//        long columnMarketsPosition = MarketContract.SearchResultsTable._ID;

        Log.i(LOG_TAG, "checkContentValues has been called");

        //TODO: Try using some combo of cursor.moveToFirst and cursor.moveToLast to check if values present.

        //TODO: Try using _ID column to check if values exist in provider.

        // TODO: Create Strings and values for projection( String[] ), selection, selectionArgs( String[] ) & sortOrder.

        // Try using this do-while loop as a method to iterate through columns and use for bulkInsert, insert, boolean condition.

      /*do {
//            Log.v(LOG_TAG,  cursor.getString(cursor.getColumnIndex(selection)) );
             cursor.moveToNext();
        }while ( !cursor.isLast() );*/


        // Possibly use to index and compare values in provider for insert boolean.
//        List<String> listMarkets = Arrays.asList(cvMarkets);

        //TODO: Modify to bulkInsert all values from cvIds, cvMilesTo, and cvMarkets if Column is completely empty...
        // TODO: ...or if there are 19 new searchResults not previously in MarketProvider/Database.

//        String columnMarkets = cursor.getString(cursor.getColumnIndex("marketName"));


       String[]  projection = new String[]
               {MarketContract.SearchResultsTable.COLUMN_MILES_TO_MARKET, MarketContract.SearchResultsTable.COLUMN_MARKET_NAME};

       String selection = MarketContract.SearchResultsTable.COLUMN_MARKET_NAME;
//        String[] selectionArgs = new String[] { MarketContract.SearchResultsTable.COLUMN_MARKET_NAME};

        // Change Uri to Detail, Zipcode, or LatLng.

        Log.v(LOG_TAG, "First Service Cursor Uri: " + MarketContract.SearchResultsTable.CONTENT_URI );
//        Log.v(LOG_TAG, "Second Service Cursor Uri: " + MarketContract.SearchResultsTable.SECOND_CONTENT_URI );

        Log.d(LOG_TAG, Log.getStackTraceString(new Throwable()));

   Cursor  cursor = getContentResolver().query(
           MarketContract.SearchResultsTable.CONTENT_URI,
           projection,
           selection,
           null,
           null);

      /*if ( cursor.isBeforeFirst() ) {
            cursor.moveToFirst();
        }*/

        // cursor.moveToLast();

        // TODO: Possibly need to use strings within while-loop for placement, query, of provider.
        while( cursor.moveToNext() ) {
            String savedLatLngColumn = cursor.getString(cursor.getColumnIndex(MarketContract.SearchResultsTable.COLUMN_LAT_LNG));
            String savedZipcodeColumn = cursor.getString(cursor.getColumnIndex(MarketContract.SearchResultsTable.COLUMN_ZIPCODE));
            String savedDetailColumn = cursor.getString(cursor.getColumnIndex(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME));

            Log.v(LOG_TAG, "while-loop results:  " + savedDetailColumn);

            Log.d(LOG_TAG, Log.getStackTraceString(new Throwable()));
        }


          if  ( cursor.getColumnIndex(MarketContract.SearchResultsTable.COLUMN_MARKET_IDS) <= -1 ) {

              Log.d(LOG_TAG, Log.getStackTraceString(new Throwable()));

              Log.v(LOG_TAG, "bulkInsert method called");

              ContentValues[] valuesArray = new ContentValues[cvMarkets.length];

              //TODO: Possibly create new ContentValues[] with values from cvIds, milesTo, and Markets, then use for-loop.

              for ( int i = 0; i < cvMilesTo.length; i++ ) {

                  // TODO: Possibly create if/else inside here to determine if values are in provider or not.

                  ContentValues contentValues = new ContentValues();

                  // TODO: Possibly use cvMarkets[i] for value of contentValues.put();
                  // Put empty String in LatLng Column when Zipcode used.
                  contentValues.put(MarketContract.SearchResultsTable.COLUMN_ZIPCODE, locationQuery);
                  contentValues.put(MarketContract.SearchResultsTable.COLUMN_LAT_LNG, "");
                  contentValues.put(MarketContract.SearchResultsTable.COLUMN_MARKET_IDS, cvIds[i]);
                  contentValues.put(MarketContract.SearchResultsTable.COLUMN_MILES_TO_MARKET, cvMilesTo[i]);
                  contentValues.put(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME, cvMarkets[i]);

                  // Try checking cursor for cvIds[] value and insert if missing here.
                  //TODO: Wrap notifyChange() with condition ?
//                  getContentResolver().notifyChange(MarketContract.SearchResultsTable.CONTENT_URI, null);

//                  Log.v(LOG_TAG, cursor.getString(cursor.getColumnIndex(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME)));

                  valuesArray[i] = contentValues;

          /*  if ( cursor.isNull(i) ) {
                Log.e(LOG_TAG, " values are null ");
                }
            */

              }

              if ( valuesArray[cvMarkets.length - 1] != null ) {

                  getContentResolver().bulkInsert(MarketContract.SearchResultsTable.CONTENT_URI, valuesArray);
                  getContentResolver().notifyChange(MarketContract.SearchResultsTable.CONTENT_URI, null);

//            cursor.close();
              }



              if ( cursor.isAfterLast() ) {
                  cursor.close();
              }

          }
        else {
             Log.i(LOG_TAG, "No bulkInsert needed");
          }
/*
* if ( cursor.isNull() )  {

            Log.v(LOG_TAG, "Insert boolean is true");

//            Log.v(LOG_TAG, valuesArray[i].toString());

            if ( valuesArray[cvIds.length - 1] != null ) {

                Log.v(LOG_TAG, "contentValues are not null");

            }

        } else {
            Log.v(LOG_TAG, "No bulk insert was needed");
        }
*/

              //TODO: Add second cursor to query for individual missing values and insert each into provider;

              // TODO: Cursor must query all values in provider and use ContentValues to insert individual missing values.
//              Cursor cursorSingleValue = getContentResolver()
//                      .query(MarketContract.SearchResultsTable.CONTENT_URI,
//                              new String[] {MarketContract.SearchResultsTable.COLUMN_MARKET_NAME},
//                              "marketName=?",
//                              null,
//                              null
//                      );

//              int individualRow = cursorSingleValue.getColumnIndex(MarketContract.SearchResultsTable._ID);
              // Possibly need long for row id or something else related to getting row value.

//              if ( individualRow <= -1 ) {
//                  cursorSingleValue.moveToFirst();
//                  Log.i(LOG_TAG, "cursorSingleValue moved to first");
//                  Log.i(LOG_TAG, cursorSingleValue.getColumnName(0));
//              }


              //TODO: Possibly need to add Loader and/or adapter instance here to get data to listViewFragment.


//              getContentResolver().insert(MarketContract.SearchResultsTable.CONTENT_URI, singleContentValues);
//              getContentResolver().notifyChange(MarketContract.SearchResultsTable.CONTENT_URI, null);

//              Log.v(LOG_TAG, " singleContentValues called " +
//                      singleContentValues.get(MarketContract.SearchResultsTable.COLUMN_MARKET_IDS) + " ");
//          }

//        else {
//              Log.i(LOG_TAG, "No bulkInsert needed, values exist");
//          }

//        cursor.close();


        //TODO: Set conditions for cursor traversal in order to find if column values exist.
        //TODO: Only need to do for one column, as all are dependent on each other for C.R.U.D operations.

//        if ( cursor.getColumnIndex(MarketContract.SearchResultsTable.COLUMN_MARKET_IDS ) )
//        cursor.moveToFirst();
//        cursor.moveToNext();
//        cursor.moveToLast();

//        ContentValues cvCheck = new ContentValues();

//        cvCheck.put(MarketContract.SearchResultsTable.COLUMN_MARKET_IDS, id);
//        cvCheck.put(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME, markets);
//        cvCheck.put(MarketContract.SearchResultsTable.COLUMN_MILES_TO_MARKET, miles);


        //if (cursor.getColumnIndex(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME) == -1)

        // cursor.isNull(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME.indexOf(marketName));
        //  if (cvVector.get(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME) == null)

        // cursor.getColumnIndex(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME


//        if ( cursor.moveToLast() )

//        cvVector.get(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME) !=
//        cursor.getColumnName(INDEX_OF_COLUMNS[1])


        //TODO: Must condition to check for value in database and use insert if not there.
        //TODO: Likely need cursor or contentResolver to do so.

        //TODO: NOT checking properly on run at 8:03 pm, April 26th, if condition / check must be changed.
//        if ( cursorMarkets.getColumnIndex(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME) == -1) {


//            Log.v(LOG_TAG, "cursor.getColumnIndex() called");


//        sortContentValues();
//        placeContentValues();

//                if ( cvIds != null && cvMarkets != null && cvMilesTo != null) {

        // Reads and stores data from "id" json values array, parsing it.

        // Loops through all id values collected in ids and parsed, for insert,
        // query, in Content Provider.


//                        getContentResolver().insert(MarketContract.SearchResultsTable.CONTENT_URI, contentValues);
//                        cursor.setNotificationUri(getContentResolver(), MarketContract.SearchResultsTable.CONTENT_URI);

        // TODO: Try adding all 3 for-each loops inside one. May help with C.R.U.D issues.

//             for ( String s : cvIds ) {
//                 id = s;
//              Log.v(LOG_TAG, id);

        //TODO: Possibly add check for values with query here instead of outside loop.
        //TODO: Same for insert with conditional for if value equals(null) in provider.

//                                getContentResolver().insert(MarketContract.SearchResultsTable.CONTENT_URI, contentValues);
//                                cursorMarkets.setNotificationUri(getContentResolver(), MarketContract.SearchResultsTable.CONTENT_URI);
//             }

//        for ( String parseMile : cvMilesTo ) {
//            miles = parseMile;
//
//            Log.v(LOG_TAG, " Content Value check miles " + miles);
//
//        }


        // Reads and stores data from "milesTo" json values array, parsing it.
//                        Log.v(LOG_TAG, "milesArray parser from contentValues attempt 12 " + t);

        // Loops through all milesTo values collected in milesTo and parsed,
        // for insert, query, in Content Provider.

        //                        getContentResolver().insert(MarketContract.SearchResultsTable.CONTENT_URI, contentValues);
//                        cursorMiles.setNotificationUri(getContentResolver(), MarketContract.SearchResultsTable.CONTENT_URI);


        // Reads and stores data from "marketName" json values array, parsing it.

        // Loops through all marketNames values collected in marketNames and parsed,
        // for insert, query, in Content Provider.
//                    for ( String v : cvMarkets ) {
//                        marketName = v;
//                        Log.v(LOG_TAG, " Content Value check markets " + marketName);
//
//                    }

    /*
    *  if ( id != null && miles != null && marketName != null  ) {

            Log.i(LOG_TAG, "placeContentValues used");

            placeContentValues();

        }*/

        // Insert does work for markets, inefficiently though, on 5/6/15 1:47 p.m

//                            cursorMarkets.setNotificationUri(getContentResolver(), MarketContract.SearchResultsTable.CONTENT_URI);
//                    }

//                }

        //TODO: Likely move contentValues out of here to properly check.
        //TODO: Try using insert, not bulk, to only insert missing values from d.b, provider.

//                    ContentValues[] values = new ContentValues[]{contentValues};

//                    getContentResolver().insert(MarketContract.SearchResultsTable.CONTENT_URI, contentValues);
//                    getContentResolver().bulkInsert(MarketContract.SearchResultsTable.CONTENT_URI, values);

//                    cursorIds.setNotificationUri(getContentResolver(), MarketContract.SearchResultsTable.CONTENT_URI);
//                    cursorMiles.setNotificationUri(getContentResolver(), MarketContract.SearchResultsTable.CONTENT_URI);
//                    cursorMarkets.setNotificationUri(getContentResolver(), MarketContract.SearchResultsTable.CONTENT_URI);

//        }


        //TODO: Try using variations of key and values for put instances

        //TODO: Fix so all values are inserted and checked, not just one at a time.


//            Log.v(LOG_TAG, "Data has been inserted " + " " + miles + "" + markets);

        // Log.v(LOG_TAG, one);

        //getContentResolver().notifyChange(MarketContract.SearchResultsTable.CONTENT_URI, null);

//        if ( cursorMarkets.moveToLast() ){

        //cvVector.get(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME);

    }

    /* private boolean doesJsonExistInProvider() {

        if ( cursor.getString(cursor.getColumnIndex(selection)) != cvMarkets[1] ) {
            return true;
        }
        else {
            return false;
        }

    }*/


    //TODO: Use methods below for ContentValue parsing and placement.

   /*private void sortContentValues() {

        for ( String s : cvIds ) {
          id = s;

            Log.v(LOG_TAG, id);

        }

        for ( String parseMile : cvMilesTo ) {
            miles = parseMile;

            Log.v(LOG_TAG, " Content Value check miles " + miles);

        }

        for ( String v : cvMarkets ) {
         markets = v;
        Log.v(LOG_TAG, " Content Value check markets " + markets);

        }

//        placeContentValues();
        getContentResolver().insert(MarketContract.SearchResultsTable.CONTENT_URI, placeContentValues());
    }
*/

    //TODO: bulkInsert() for ContentValues.
  /*
  *
  *  private ContentValues placeContentValues() {

        Log.i(LOG_TAG, "placeContentValues called");

        ContentValues contentValues = new ContentValues();

//        HashMap hashMap = new HashMap();
//        hashMap.put(MarketContract.SearchResultsTable.COLUMN_MARKET_IDS, cvIds);


        contentValues.put(MarketContract.SearchResultsTable.COLUMN_MARKET_IDS, cvIds[1]);
        contentValues.put(MarketContract.SearchResultsTable.COLUMN_MILES_TO_MARKET, cvMilesTo[1]);
        contentValues.put(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME, cvMarkets[1]);

        return contentValues;
    }*/

}

