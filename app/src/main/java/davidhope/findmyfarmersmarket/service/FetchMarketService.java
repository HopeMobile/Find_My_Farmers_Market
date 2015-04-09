package davidhope.findmyfarmersmarket.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;

import davidhope.findmyfarmersmarket.SearchFragment;

/**
 * Created by David on 3/27/2015.
 */
public class FetchMarketService extends IntentService {

    protected ArrayList<String> searchResultData = new ArrayList();
    private static final String  LOG_TAG = FetchMarketService.class.getSimpleName();
    protected static final String  SEARCH_TAG = SearchFragment.class.getSimpleName();

    private static final String LAT = "lat";
    private static final String LGN = "lgn";

    protected static final String LOCATION_QUERY_STRING = "lqe";

    String mile;
    String[] miles;

    //protected static final String marketDetailQuery =

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param //name Used to name the worker thread, important only for debugging.
     */


    public FetchMarketService() {
        super("FetchMarketService");

    }

    //TODO: Create boolean to check if user has selected individual market from list and start URI for it.

      String[] MarketNames;
      String[] Ids;
      private String marketStream;
      Context mContext = getBaseContext();

    @Override
    protected void onHandleIntent(Intent intent) {


        //TODO: 1. Establish connection to Internet, build Uri with Zipcode or Lat-Lng query and create InputStream.
        //TODO: 2. Read from InputStream and  loop through data,
        //TODO: 3. Parse Json in helper functions and return as Strings.

        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        BufferedInputStream bufferedInputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;

        URL zipCodeUrl;

        final String BASE_URL = "http://search.ams.usda.gov/farmersmarkets/v1/data.svc/";
        final String ZIP_SEARCH = "zipSearch";
        final String ZIP_PARAM = "zip";
        final String LOCATION_QUERY_PARAM = "locSearch";
        final String LATITUDE = "?lat=";
        final String LONGITUDE = "&lng=";
        final String MARKET_DETAIL = "mktDetail";
        final String MARKET_ID = "?id=";

        // Use this API endpoint to get the details for any specific selected farmers market
        final String marketDetailResults = "http://search.ams.usda.gov/farmersmarkets/v1/data.svc/mktDetail?id=";


        try {

            String  locationQuery = intent.getStringExtra(Intent.EXTRA_TEXT);

            if (locationQuery == null) {

                double userLat = 0.0;
                double userLng = 0.0;

                Log.v(LOG_TAG, userLat + "");
                Log.v(LOG_TAG, userLng + "");

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
                    //Stores the result of a successful inputStream connection.
                    marketStream = bufferedReader.readLine();

                   // StringBuilder stringBuilder = new StringBuilder(marketStream);
                    //String m = null;

                /*    while (bufferedReader.readLine() != null) {
                        stringBuilder.append(m);
                        Log.v(LOG_TAG, m);
                    } */

                    addMarkets(marketStream);

                }

                // Closes the input stream once every character has been read from it.
                if (bufferedReader.read() <= -1) {
                    inputStream.close();
                }

            } else {
                Log.e(LOG_TAG, "Bummer, the connection failed");

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //TODO: Fix method to return both market and milesTo Strings.
    public String addMarkets(String market) throws JSONException {
       market = parseMarketJson(market);

      return market;

    }

    public String parseMarketJson(String market) throws JSONException {
        JSONObject jsonObject = new JSONObject(marketStream);

        JSONArray jsonArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject json2 = jsonArray.getJSONObject(i);

            String ids = json2.getString("id");

            //TODO: Reminder, just need to put Strings into ContentValues without additonal String[] to index.
            String milesTo = json2.getString("marketname").substring(0, 4);
            market = json2.getString("marketname").substring(4);
            Log.v(LOG_TAG, ids);
            Log.v(LOG_TAG, milesTo);
            Log.v(LOG_TAG, market);

       }
        return market;
    }

}