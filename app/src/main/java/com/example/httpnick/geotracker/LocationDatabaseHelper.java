package com.example.httpnick.geotracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.sql.ResultSet;

/**
 * @class LocationDatabaseHelper
 * @author Nick Duncan
 * The class used for helping the storage of user info such as trac data on the local database.
 */
public class LocationDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "locations.sqlite";

    private static final int VERSION = 1;

    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_LOCATION_id = "userid";
    private static final String COLUMN_LOCATION_lat = "latitude";
    private static final String COLUMN_LOCATION_long = "longitude";
    private static final String COLUMN_LOCATION_SPEED = "speed";
    private static final String COLUMN_LOCATION_HEADING = "heading";
    private static final String COLUMN_LOCATION_TIMESTAMP = "timestamp";
    SharedPreferences sp;

    public LocationDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * Creates the table that will be used to store user data
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table location (" +
            "_id integer primary key autoincrement, userid varchar(100), heading real," +
                " longitude real, " +
                "latitude real, " +
                "speed real, " +
                "timestamp varchar(100))");
    }

    /**
     * Test method not currently used
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * used to insert location data to database
     * @param locpack Takes the location package of user locaiton data
     * @return returns the database to be written
     */
    public long insertLocation(LocationPackage locpack) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOCATION_id, locpack.id);
        cv.put(COLUMN_LOCATION_HEADING, locpack.heading);
        cv.put(COLUMN_LOCATION_lat, locpack.latitude);
        cv.put(COLUMN_LOCATION_SPEED, locpack.speed);
        cv.put(COLUMN_LOCATION_long, locpack.longitude);
        cv.put(COLUMN_LOCATION_TIMESTAMP, locpack.time);
        return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
    }

    /**
     * Method used for getting a cursor on the database that is being inserted to
     * @return Returns a cursor
     */
    public Cursor queryLocation() {
        Cursor c = getReadableDatabase().query(TABLE_LOCATION, null,
                null, null, null, null, null);

        return c;
    }

    /**
     * Returns the cursor assoicated with a signle user
     * @param uid The user id number
     * @return Database cursor
     */
    public Cursor querySingleUser(String uid) {

        return getReadableDatabase().rawQuery("select * from location where userid = \"" +uid + "\";",
                null);

    }

    /**
     * Clears a table in the database used for loc storage
     */
    public void clearDatabase() {
        getWritableDatabase().delete(TABLE_LOCATION, null, null);
    }

    /**
     * Class used for local database helpijg
     */
    public static class LocationCursor extends CursorWrapper {

        public LocationCursor(Cursor c) {
            super(c);
        }

        /**
         * Method used to get the location package object of a user
         * @return The location package of the user
         */
        public LocationPackage getLocationPackage() {
            if (isBeforeFirst() || isAfterLast()) {
                return null;
            } else {
                LocationPackage lp = new LocationPackage(getString(getColumnIndex(COLUMN_LOCATION_id)),
                        getFloat(getColumnIndex(COLUMN_LOCATION_HEADING)),
                        getDouble(getColumnIndex(COLUMN_LOCATION_long)),
                        getDouble(getColumnIndex(COLUMN_LOCATION_lat)),
                        getFloat(getColumnIndex(COLUMN_LOCATION_SPEED)),
                        getLong(getColumnIndex(COLUMN_LOCATION_TIMESTAMP)));

                return lp;
            }
        }
    }

}
