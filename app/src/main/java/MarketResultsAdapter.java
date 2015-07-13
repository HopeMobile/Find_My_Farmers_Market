import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

/**
 * Created by David on 4/9/2015.
 */
public class MarketResultsAdapter extends SimpleCursorAdapter {

    public MarketResultsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    //TODO: Must create a cursor and query provider here to get results for list


}
