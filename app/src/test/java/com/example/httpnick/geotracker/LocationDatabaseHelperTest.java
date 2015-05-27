package com.example.httpnick.geotracker;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;
import android.test.RenamingDelegatingContext;

/**
 * Created by mmills on 5/22/2015.
 */
public class LocationDatabaseHelperTest extends AndroidTestCase {

    private SQLiteDatabase testDB;
    private LocationDatabaseHelper dbh;
    private IsolatedContext mockContext;


    /**
     *Sets up a mock context to initialize the
     * LocationDatabaseHelper object.
     * @throws Exception
     */
    public void setUp() throws Exception {
        super.setUp();

        mockContext = new IsolatedContext(new MockContentResolver(), getContext());
        dbh = new LocationDatabaseHelper(mockContext);

    }

    /**
     *Tests that asserts LocationDatabaseHelper object instantiates.
     */
    public void testLocationDatabaseHelper() {
        assertNotNull(dbh);
    }
}