package davidhope.findmyfarmersmarket;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Map;
import java.util.Set;

import davidhope.findmyfarmersmarket.service.FetchMarketService;

/**
 * Created by David on 3/27/2015.
 */
public class SearchFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String SEARCH_FRAG_LOG_TAG = SearchFragment.class.getSimpleName();

    public static final String SERVICE_TAG = FetchMarketService.class.getSimpleName();

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;

    Context mContext;

    EditText  mSearchField;
    Button mSearchButton;

    public GoogleApiClient mGoogleApiClient;
    public double currentLatitude;
    public double currentLongitude;

    String zipcode;

    public SharedPreferences preferences;

    public SharedPreferences.Editor sharedPreferencesEditor;

    public static final String LAT = "lat";
    public static final String LGN = "lgn";

    public SearchFragment() {
      setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(SEARCH_FRAG_LOG_TAG, "onCreate called");

    }

    protected boolean isZipcodeValid() {

        zipcode = mSearchField.getText().toString();

        if (mSearchField.getText().toString().length() != 0) {

              sharedPreferencesEditor = new SharedPreferences.Editor() {
                @Override
                public SharedPreferences.Editor putString(String key, String value) {
                    return sharedPreferencesEditor.putString("", zipcode);
                }

                @Override
                public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor putInt(String key, int value) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor putLong(String key, long value) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor putFloat(String key, float value) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor putBoolean(String key, boolean value) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor remove(String key) {
                    return null;
                }

                @Override
                public SharedPreferences.Editor clear() {
                    return null;
                }

                @Override
                public boolean commit() {
                    return false;
                }

                @Override
                public void apply() {
                    sharedPreferencesEditor.apply();
                }
            };

            preferences = new SharedPreferences() {

                @Override
                public Map<String, ?> getAll() {
                    return null;
                }

                @Override
                public String getString(String key, String defValue) {
                    return preferences.getString("", zipcode);
                }

                @Override
                public Set<String> getStringSet(String key, Set<String> defValues) {
                    return null;
                }

                @Override
                public int getInt(String key, int defValue) {
                    return 0;
                }

                @Override
                public long getLong(String key, long defValue) {
                    return 0;
                }

                @Override
                public float getFloat(String key, float defValue) {
                    return 0;
                }

                @Override
                public boolean getBoolean(String key, boolean defValue) {
                    return false;
                }

                @Override
                public boolean contains(String key) {
                    return true;
                }

                @Override
                public Editor edit() {
                    return null;
                }

                @Override
                public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

                }

                @Override
                public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

                }

            };

            preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    preferences.getString(sharedPreferencesEditor.toString(), zipcode);
                    isZipcodeValid();
                }
            });

           preferences = getActivity().getSharedPreferences(zipcode, 0);

            Log.v(SEARCH_FRAG_LOG_TAG, preferences.toString() + " " + zipcode);

            return true;
        }
        else {
            Log.e(SEARCH_FRAG_LOG_TAG, "No valid zipcode");

        }

        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

    View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        Log.i(SEARCH_FRAG_LOG_TAG, "onCreateView called");

      mSearchButton = (Button) rootView.findViewById(R.id.search_button);
      mSearchField = (EditText) rootView.findViewById(R.id.search_field);

        preferences = getActivity().getSharedPreferences(zipcode, 0);

        mSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                zipcode = mSearchField.getText().toString();

                Log.v(SEARCH_FRAG_LOG_TAG, "onClickListener called");

                Log.v(SEARCH_FRAG_LOG_TAG, zipcode);

                Intent intent = new Intent(getActivity().getApplicationContext(), FetchMarketService.class);

                if (mSearchField.getText().length() <= 4) {

                    intent.putExtra(LAT, currentLatitude);
                    intent.putExtra(LGN, currentLongitude);

                    Log.e(SEARCH_FRAG_LOG_TAG, "Lat Long used for Intent");
                } else {

                    // todo use valid
                    boolean valid = isZipcodeValid();

                    Log.e(SEARCH_FRAG_LOG_TAG, "Zipcode used for Intent");
                    intent.putExtra(Intent.EXTRA_TEXT, zipcode);
                }

                //TODO: Fix if/else to display Toast when GPS Data and EditText are null and stop

               /*  if ( currentLatitude == 0 && mSearchField.getText().length() < 4) {
                     Toast.makeText(getActivity().getBaseContext(), "No location found", Toast.LENGTH_LONG).show();
                     getActivity().stopService(intent);

                 } */
                //TODO: Uncomment intentional stop of service once Adapter use figured out.
                getActivity().startService(intent);
                Intent list = new Intent(getActivity().getApplicationContext(), MarketResultsActivity.class);
                getActivity().startActivity(list);


            }

        });


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest =  LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(15 * 1000) // 15 seconds;
                .setFastestInterval(3 * 1000); // 3 seconds

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
       super.onActivityCreated(savedInstanceState);

        Log.i(SEARCH_FRAG_LOG_TAG, "onActivityCreated called");
   }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(SEARCH_FRAG_LOG_TAG, "Location Connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            handleNewLocation(location);

        }
    }

    private void handleNewLocation(Location location) {

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Log.d(SEARCH_FRAG_LOG_TAG, location.toString());
    }


    @Override
    public void onConnectionSuspended(int i) {
       Log.i(SEARCH_FRAG_LOG_TAG, "Location Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
           try {
               connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
           } catch (IntentSender.SendIntentException e) {
               e.printStackTrace();
           }

        }
        else {
            Log.i(SEARCH_FRAG_LOG_TAG, "Location Failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

        Log.i(SEARCH_FRAG_LOG_TAG, "onResume called");

        if (!isZipcodeValid()) {

            Log.e(SEARCH_FRAG_LOG_TAG, "Failure has occurred with SharedPref");
        }
        else {

            isZipcodeValid();
            Log.i(SEARCH_FRAG_LOG_TAG, "Shared Preferences were successfully saved");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(SEARCH_FRAG_LOG_TAG, "onPause called");

        isZipcodeValid();


        if (mGoogleApiClient.isConnected()) {
           LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            mGoogleApiClient.disconnect();
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.i(SEARCH_FRAG_LOG_TAG, "onDetach called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.i(SEARCH_FRAG_LOG_TAG, "onDestroyView called");


    }
}
