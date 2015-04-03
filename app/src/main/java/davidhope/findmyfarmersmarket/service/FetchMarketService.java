package davidhope.findmyfarmersmarket.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
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
    protected static final String LOG_TAG = FetchMarketService.class.getSimpleName();

    //protected static final String marketDetailQuery =

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param //name Used to name the worker thread, important only for debugging.
     */


    public FetchMarketService() {
        super("FetchMarketService");

    }

    final  String[] MarketNames = null;
    final  String[] Ids = null ;
    String marketStream;

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

        //TODO: Use lat/lng API endpoint example below to construct specific queries.
        //   http://search.ams.usda.gov/farmersmarkets/v1/data.svc/locSearch?lat=45.5424364&lng=-122.654422

        URL zipCodeUrl;

        final String BASE_URL = "http://search.ams.usda.gov/farmersmarkets/v1/data.svc/";
        final String LAT_LNG_URL = "http://search.ams.usda.gov/farmersmarkets/v1/data.svc/locSearch";
        final String ZIP_SEARCH = "zipSearch";
        final String ZIP_PARAM = "zip";
        final String locationQueryParameter = "locSearch";
        final String latitude = "?lat=";
        final String longitude = "&lng=";

        final String LOCATION_QUERY_STRING = SearchFragment.LOCATION_QUERY_STRING;
        String locationQuery;


        // Use this API endpoint to get the details for any specific selected farmers market
        final String marketDetailResults = "http://search.ams.usda.gov/farmersmarkets/v1/data.svc/mktDetail?id=";


        try {

            locationQuery = intent.getStringExtra(Intent.EXTRA_TEXT);

            // Builds URI, creates the properly formatted path and query parameters.
            Uri.Builder zipCodeUriBuilder = new Uri.Builder()
                    .encodedPath(BASE_URL)
                    .appendEncodedPath(ZIP_SEARCH)
                    .appendQueryParameter(ZIP_PARAM, locationQuery);


            zipCodeUrl = new URL(zipCodeUriBuilder.build().toString());

            httpURLConnection = (HttpURLConnection) zipCodeUrl.openConnection();
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
        market = Parse_Market_Json(market);

      return market;

    }

    private String Parse_Market_Json(String market) throws JSONException {
        JSONObject jsonObject = new JSONObject(marketStream);

        JSONArray jsonArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject json2 = jsonArray.getJSONObject(i);
            String milesTo = miles_to_market(json2);
            market = json2.getString("marketname").substring(4);
            Log.v(LOG_TAG, market);
            Log.v(LOG_TAG, milesTo);
       }
        return market;
    }

    private String miles_to_market(JSONObject json) throws JSONException {
        return json.getString("marketname").substring(0, 4);
    }

}