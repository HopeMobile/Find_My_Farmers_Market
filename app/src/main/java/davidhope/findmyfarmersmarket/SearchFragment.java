package davidhope.findmyfarmersmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import davidhope.findmyfarmersmarket.service.FetchMarketService;

/**
 * Created by David on 3/27/2015.
 */
public class SearchFragment extends Fragment {

    public static final String LOG_TAG = SearchFragment.class.getSimpleName();

    public static final String SERVICE_TAG = FetchMarketService.class.getSimpleName();
    public static final String LOCATION_QUERY_STRING = "lqe";
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

        // Set button to start
       /*   mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceStarter();

            } */
            /*    if (mSearchField != null) {
                    Intent intent = new Intent(, FetchMarketService.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(mContext, "No location found", Toast.LENGTH_LONG).show();
                }
            } */

        // });

        return rootView;
    }


   @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

         mSearchButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 if (mSearchField != null) {
                     String zipcode = mSearchField.getText().toString();
                     Intent intent = new Intent(getActivity().getApplicationContext(), FetchMarketService.class);
                     intent.putExtra(Intent.EXTRA_TEXT, zipcode);
                     getActivity().startService(intent);

                     Intent list = new Intent(getActivity().getApplicationContext(), MarketResultsActivity.class);
                     getActivity().startActivity(list);
                 }

                 else {
                     //Toast.makeText(SearchFragment.this, "No location found", Toast.LENGTH_LONG).show();
                     Log.e(LOG_TAG, "No data was sent");
                 }
             }
         });

    }

   /* private void serviceStarter() {

    } */

}
