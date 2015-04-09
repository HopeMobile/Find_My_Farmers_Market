package davidhope.findmyfarmersmarket;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import davidhope.findmyfarmersmarket.service.FetchMarketService;

/**
 * Created by David on 3/27/2015.
 */
public class SearchFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String LOG_TAG = SearchFragment.class.getSimpleName();

    public static final String LAT = "lat";
    public static final String LGN = "lgn";

    public static final String SERVICE_TAG = FetchMarketService.class.getSimpleName();

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;

    Context mContext;

    public static final String LOCATION_QUERY_STRING = "lqe";

    public GoogleApiClient mGoogleApiClient;
    public double currentLatitude;
    public double currentLongitude;

    public double atlLat = 33.7677129;
    public double atlLang = -84.420604;
    public String lat = "lat";
    public String lgn = "lon";

    EditText  mSearchField;
    Button mSearchButton;

    public SearchFragment() {
      setHasOptionsMenu(true);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       // Instantiate views for user interaction.

       View rootView = inflater.inflate(R.layout.fragment_search, container, false);

       mSearchButton = (Button) rootView.findViewById(R.id.search_button);
       mSearchField = (EditText) rootView.findViewById(R.id.search_field);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest =  LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000) // 10 seconds;
                .setFastestInterval(3 * 1000); // 3 seconds

        return rootView;
    }



   @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


       mSearchButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent intent = new Intent(getActivity().getApplicationContext(), FetchMarketService.class);

                 if (mSearchField.getText().length() <= 0) {

                     intent.putExtra(LAT, currentLatitude);
                     intent.putExtra(LGN, currentLongitude);
                 }
                 else {

                     String zipcode = mSearchField.getText().toString();
                     intent.putExtra(Intent.EXTRA_TEXT, zipcode);

                 }

                 //TODO: Fix if/else to display Toast when GPS Data and EditText are null and stop
               /*  if ( currentLatitude == 0 && mSearchField.getText().length() < 4) {
                     Toast.makeText(getActivity().getBaseContext(), "No location found", Toast.LENGTH_LONG).show();
                     getActivity().stopService(intent);

                 } */
                 //TODO: Uncomment intentional stop of service once Adapter use figured out.
                   //  getActivity().startService(intent);
                     Intent list = new Intent(getActivity().getApplicationContext(), MarketResultsActivity.class);
                     getActivity().startActivity(list);

             }
         });

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG, "Location Connected");
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
        Log.d(LOG_TAG, location.toString());
    }


    @Override
    public void onConnectionSuspended(int i) {
       Log.i(LOG_TAG, "Location Suspended");
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
            Log.i(LOG_TAG, "Location Failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
           LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }



}
