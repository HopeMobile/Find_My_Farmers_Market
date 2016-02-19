package davidhope.findmyfarmersmarket.service;


import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import davidhope.findmyfarmersmarket.Model.ListMarketPOJOs;
import davidhope.findmyfarmersmarket.Views.SearchFragment;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

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

    /* 1/11/2016 Update
    Removing all JSON methods, Arrays, ect... creating POJOs out of them.
    Using OkHttp or Retrofit

    * */

    public FetchMarketService() {
        super("FetchMarketService");

    }


    //TODO: Create boolean to check if user has selected individual market from list and start URI for it.

      String zip;

      String results = "results";
      Context mContext = getBaseContext();


    Moshi mMoshi = new Moshi.Builder().build();


   /* private List<MarketPOJOs> mPOJOsList;
    private ListMarketPOJOs mMarketsPOJOs;*/

    /*
    1. Read Buffered Source instance to get stream of JSON
    2. Read JSON Stream, looking for JSONObject "results""
    3. When JSONObject "results" exists, read its value, which is a JSONArray of JSONObjects.
    4. Inside JSONArray, read each JSONObject, transforming every value into a POJO from MarketPOJOs
    5.
    * */


  /*  public List<MarketPOJOs> readJsonStream(BufferedSource source) throws IOException {
        JsonReader jsonReader = JsonReader.of(source);
        try {
            return readResults(jsonReader);
        }
        finally {
            jsonReader.close();
        }

    }

    public List<MarketPOJOs> readResults(JsonReader jsonReader) throws IOException {

        List<MarketPOJOs> results = new ArrayList<>();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals("results")) {
                results = readMarketsArray(jsonReader);
            }
            else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();

        return results;
    }

    public List<MarketPOJOs> readMarketsArray(JsonReader jsonReader) throws IOException{

        List<MarketPOJOs> marketPOJOs = new ArrayList<>();

        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            marketPOJOs.add(readMarkets(jsonReader));
        }
        jsonReader.endArray();
        return marketPOJOs;
    }

    // Add Data structure(ArrayList?) to collect ALL of the JSONObject's inside the ONE JSONArray
    public MarketPOJOs readMarkets(JsonReader jsonReader) throws IOException{

        String id = null;
        String marketnames = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "id":
                    id = jsonReader.nextString();
                    break;
                case "marketnames":
                    marketnames = jsonReader.nextString();
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();
        return new MarketPOJOs(id, marketnames);
    }*/



    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(LOG_TAG, "onHandleIntent has been called");


      //  final String MARKET_DETAIL = "mktDetail";
      //  final String MARKET_ID = "?id=";

        // Use this API endpoint to get the details for any specific selected farmers market
       // final String marketDetailResults = "http://search.ams.usda.gov/farmersmarkets/v1/data.svc/mktDetail?ids=";


       /* public void getMarkets() {
        Call<ListMarketPOJOs>
        @GET("/zipSearch?{ZIP_SEARCH}")
        List<ListMarketPOJOs> marketsPOJOsList( @Path("zipSearch"),String zipSearch);
        }*/
//        try {

            zip = intent.getStringExtra(Intent.EXTRA_TEXT);


            // Retrofit Adapter for use in all 3 Call() types
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://search.ams.usda.gov/farmersmarkets/v1/data.svc/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();



           // G.P.S Enabled, LatLng used for location
            if (zip == null) {

                double userLat = 0.0;
                double userLng = 0.0;
                userLat = intent.getDoubleExtra(LAT, 0);
                userLng = intent.getDoubleExtra(LGN, 0);


                Log.v(LOG_TAG, "" + userLat);
                Log.v(LOG_TAG, "" + userLng);

                String lat = userLat + "";
                String lng = userLng + "";


               RequestMarkets requestMarkets = retrofit.create(RequestMarkets.class);
               Call<ResponseBody> latLngCall = requestMarkets.LatLngCall(lat, lng);

                try {
                    Response<ResponseBody> response = latLngCall.clone().execute();

                    Log.d(LOG_TAG, response.message());

                    extractJSON(response);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }

            // Zipcode field used for location
            else {

                RequestMarkets requestMarkets = retrofit.create(RequestMarkets.class);
                Call<ResponseBody> marketCall = requestMarkets.zipCall(zip).clone();


                //TODO: Create boolean which checks if zip equals previous call, and send to database layer if true
                try {

                    Response<ResponseBody> response = marketCall.clone().execute();

                    Log.d(LOG_TAG, response.message());

                    if (response.isSuccess()) {

                        extractJSON(response);

                    }
                    else {
                      String errorResponse = response.errorBody().string();
                        Log.e(LOG_TAG, "Response failed: " + errorResponse);

                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


            }

    }


    private void extractJSON(Response<ResponseBody> response) throws IOException {
        if (response.isSuccess()) {

            BufferedSource source = response.body().source();
            String json = source.readUtf8();

            Log.v(LOG_TAG, json);

            JsonAdapter<ListMarketPOJOs> jsonAdapter = mMoshi.adapter(ListMarketPOJOs.class);
            ListMarketPOJOs listMarketPOJOs = jsonAdapter.fromJson(json);

            // Tests to ensure a random MarketPOJO marketname object exists
            Log.v(LOG_TAG, "POJO Test: " + listMarketPOJOs.results.get(4).getMarketName());

            if (source.exhausted()) {
                source.close();
            }
        }

        else {
            String errorResponse = response.errorBody().string();
            Log.e(LOG_TAG, "Response failed: " + errorResponse);

        }
    }

}

