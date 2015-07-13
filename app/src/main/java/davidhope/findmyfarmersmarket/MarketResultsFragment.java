package davidhope.findmyfarmersmarket;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by David on 3/27/2015.
 */
public class MarketResultsFragment extends Fragment {


  public  Context mContext = getActivity();
    //MarketAdapter mMarketAdapter;
    ListView listView;
    TextView mTextView;
    ArrayAdapter<String> mArrayAdapter;


    public MarketResultsFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //TODO: Switch back to layout for Fragment once SimpleCursorLoader used.
        View rootView = inflater.inflate(R.layout.list_search_results, container, false);

        //TODO: Initialize Detail-URI on via Intent and FetchMarketService upon user item tap.

        String[] strings = new String[] {"aaa", "ggg", "ppp", "ttt", "llll", "yyy", "eee", "ooo", "nnn", "zzzz", "ddd", "uuuu"};

       // mTextView = (TextView) rootView.findViewById(R.id.item_market_names);
        listView = (ListView) rootView.findViewById(R.id.results_list);

       // ArrayList<String> arrayList = new ArrayList<String>(strings);
      //  arrayList.add();

        //TODO: Must create a cursor and query provider here to get results for list, need instance of adapter from its class.

        SimpleCursorAdapter adapter;

        //TODO: Create viewLoader to add, format, data from adapter for use in ListView.

        //TODO: Implement and create loader for data from ContentProvider.


 mArrayAdapter  = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                strings);

        listView.setAdapter(mArrayAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();


    }

}
