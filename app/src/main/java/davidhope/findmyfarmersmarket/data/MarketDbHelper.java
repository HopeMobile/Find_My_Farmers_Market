package davidhope.findmyfarmersmarket.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by David on 4/2/2015.
 */
public class MarketDbHelper extends SQLiteOpenHelper {

     public static final String LOG_TAG = MarketDbHelper.class.getSimpleName();

    Context mContext;


    public static final int DATABASE_VERSION = 4;

    public static final String MARKETS_DB = "marketDatabase";


    public MarketDbHelper(Context context) {
        super(context, MARKETS_DB, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(LOG_TAG, "MarketDbHelper has been created");

        // todo: Possibly change COLUMN_ZIPCODE to use INTEGER and COLUMN_LAT_LNG to use REAL.

        String SQL_DB_MARKET_RESULTS =  " CREATE TABLE " + MarketContract.SearchResultsTable.MARKET_TABLE_NAME +
                "( "
                + MarketContract.SearchResultsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MarketContract.SearchResultsTable.COLUMN_MARKET_IDS + " INTEGER NOT NULL, "
                + MarketContract.SearchResultsTable.COLUMN_ZIPCODE + " TEXT NOT NULL, "
                + MarketContract.SearchResultsTable.COLUMN_LAT_LNG + " TEXT NOT NULL, "
                + MarketContract.SearchResultsTable.COLUMN_MILES_TO_MARKET + " TEXT NOT NULL, "
                + MarketContract.SearchResultsTable.COLUMN_MARKET_NAME + " TEXT NOT NULL "
                + " ); ";

       db.execSQL(SQL_DB_MARKET_RESULTS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(LOG_TAG, "MarketDbHelper has been upgraded");
         // TODO: Possibly modify if need to hold onto user-generated data using ALTER_TABLE

         db.execSQL("DROP TABLE IF EXISTS " + MarketContract.SearchResultsTable.MARKET_TABLE_NAME);
         onCreate(db);

    }

}
