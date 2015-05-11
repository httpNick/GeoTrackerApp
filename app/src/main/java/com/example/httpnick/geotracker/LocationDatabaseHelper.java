package com.example.httpnick.geotracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

/**
 * Created by httpnick on 5/10/15.
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

    public LocationDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table location (" +
            "_id integer primary key autoincrement, userid varchar(100), heading real," +
                " longitude real, " +
                "latitude real, " +
                "speed real, " +
                "timestamp varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

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

    public Cursor queryLocation() {
        Cursor c = getReadableDatabase().query(TABLE_LOCATION, null,
                null, null, null, null, null);
        return c;
    }

    public static class LocationCursor extends CursorWrapper {

        public LocationCursor(Cursor c) {
            super(c);
        }

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
