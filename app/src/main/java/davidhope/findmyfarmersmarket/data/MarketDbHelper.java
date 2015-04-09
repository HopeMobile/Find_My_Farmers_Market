package davidhope.findmyfarmersmarket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by David on 4/2/2015.
 */
public class MarketDbHelper extends SQLiteOpenHelper {

    Context mContext;

    public static final int DATABASE_VERSION = 1;

    public static final String MARKET_RESULTS_DB = "marketresults.db";

    public Cursor cursor;



    //TODO: Guide for Database requirements:
    // 1. Create Database using Table defined in Contract.
    // 2. Prepare Table/Database for C.R.U.D operations.
    // 3. Use ContentValues to make insert operation for Database and instantiate.
    // 4. Open and close Database using Cursor and its static methods appropriately.

    protected static String MARKET_IDS = "id";
    protected static String MILES_TO_MARKET = "milesTo";
    protected static String MARKET_NAMES = "marketnames";


    ContentValues cv = new ContentValues(); {
        cv.put(MarketContract.SearchResultsTable.COLUMN_MARKET_IDS, "id");
        cv.put(MarketContract.SearchResultsTable.COLUMN_MILES_TO_MARKET, "milesTo");
        cv.put(MarketContract.SearchResultsTable.COLUMN_MARKET_NAME, "marketNames");
    }

    private static final String SQL_MARKET_RESULTS =
            " ( " + MarketContract.SearchResultsTable.TABLE_NAME + " CREATE TABLE "
            + "," + MarketContract.SearchResultsTable._ID + " INTEGER NOT NULL " + ","
            + MarketContract.SearchResultsTable.COLUMN_MARKET_IDS + " INTEGER NOT NULL "
            + "," + MarketContract.SearchResultsTable.COLUMN_MILES_TO_MARKET + " TEXT NOT NULL " + ","
            + MarketContract.SearchResultsTable.COLUMN_MARKET_NAME + " TEXT NOT NULL "
            + " ); ";

    public MarketDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

       db.setVersion(DATABASE_VERSION);
       db.execSQL(SQL_MARKET_RESULTS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

      if (newVersion >= oldVersion) {
         db.needUpgrade(db.CONFLICT_REPLACE);
         db.execSQL(SQL_MARKET_RESULTS);

      }

      onCreate(db);
    }
}
