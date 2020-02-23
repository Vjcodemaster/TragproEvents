package scanning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static android.app.DownloadManager.COLUMN_ID;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "recyclerManager";

    //DataBaseHelper table name
    private static final String TABLE_RECENT = "DataBaseHelper";

    //DataBaseHelper table name (2nd table for all tab)
    private static final String TABLE_ALL = "allTab";

    // recyclerManager Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_TAGID = "tag_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_NUMBER = "phone_number";
    private static final String KEY_EMAILID = "email_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_LAST_SEEN_TIME = "last_seen";
    private static final String KEY_MAJOR = "major";
    private static final String KEY_MINOR = "minor";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_RSSI = "rssi";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    /*
    leaving gap between "CREATE TABLE" & TABLE_RECENT gives error watch out!
    Follow the below format
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String CREATE_RECENT_TABLE = "CREATE TABLE " + TABLE_RECENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TAGID + " TEXT, "
                + KEY_NAME + " TEXT, "
                + KEY_DESIGNATION + " TEXT, "
                + KEY_NUMBER + " TEXT, "
                + KEY_EMAILID + " TEXT, "
                + KEY_DATE + " TEXT, "
                + KEY_TIME + " TEXT, "
                + KEY_LAST_SEEN_TIME + " TEXT, "
                + KEY_MAJOR + " TEXT, "
                + KEY_MINOR + " TEXT, "
                + KEY_UUID + " TEXT, "
                + KEY_RSSI + " TEXT)";*/

        String CREATE_ALL_TABLE = "CREATE TABLE " + TABLE_ALL + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TAGID + " TEXT, "
                + KEY_NAME + " TEXT, "
                + KEY_DESIGNATION + " TEXT, "
                + KEY_NUMBER + " TEXT, "
                + KEY_EMAILID + " TEXT, "
                + KEY_DATE + " TEXT, "
                + KEY_TIME + " TEXT, "
                + KEY_LAST_SEEN_TIME + " TEXT, "
                + KEY_MAJOR + " TEXT, "
                + KEY_MINOR + " TEXT, "
                + KEY_UUID + " TEXT, "
                + KEY_RSSI + " TEXT)";

       // db.execSQL(CREATE_RECENT_TABLE);
        db.execSQL(CREATE_ALL_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL);

        // Create tables again
        onCreate(db);
    }

    // Adding new data
    public void addData(DataBaseHelper dataBaseHelper) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID, dataBaseHelper.getID()); // Contact Name
        values.put(KEY_TAGID, dataBaseHelper.getTagID());
        values.put(KEY_NAME, dataBaseHelper.getName()); // Contact Phone
        values.put(KEY_DESIGNATION, dataBaseHelper.getDesignation());
        values.put(KEY_NUMBER, dataBaseHelper.getPhoneNumber());
        values.put(KEY_EMAILID, dataBaseHelper.getEmailID());
        values.put(KEY_DATE, dataBaseHelper.getDate());
        values.put(KEY_TIME, dataBaseHelper.getTime());
        values.put(KEY_LAST_SEEN_TIME, dataBaseHelper.getLastSeen());

        // Inserting Row
        //db.insert(TABLE_RECENT, null, values);
        db.insert(TABLE_ALL, null, values);

        db.close(); // Closing database connection
    }

    public int getIdForStringTabAll(String str) {
        int res;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALL, new String[]{COLUMN_ID,
                }, KEY_TAGID + "=?",
                new String[]{str}, null, null, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            res = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        } else {
            res = -1;
        }
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }

    public int getIdForStringRecentTab(String str) {
        int res;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECENT, new String[]{COLUMN_ID,
                }, KEY_TAGID + "=?",
                new String[]{str}, null, null, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            res = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        } else {
            res = -1;
        }
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }

    //gets Name of index to check whether to perform update task in recyclerview or not
    public String getNameFromAllTab(int ID) {
        Cursor cursor = null;
        String sName = "";
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.query(TABLE_ALL, new String[]{KEY_NAME,
                }, KEY_ID + "=?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        //cursor = db.rawQuery("SELECT TABLEALL FROM last_seen WHERE _id" +" = "+ID +" ", new String[] {KEY_ID + ""});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            sName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        } else {
            sName = "";
        }
        /*if(sName==null){
            return "";
        }*/
        cursor.close();
        return sName;

    }

    //gets Name of index to check whether to perform update task in recyclerview or not
    public String getNameFromRecentTab(int ID) {
        Cursor cursor = null;
        String sName = "";
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.query(TABLE_RECENT, new String[]{KEY_NAME,
                }, KEY_ID + "=?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        //cursor = db.rawQuery("SELECT TABLEALL FROM last_seen WHERE _id" +" = "+ID +" ", new String[] {KEY_ID + ""});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            sName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        } else {
            sName = "";
        }
        /*if(sName==null){
            return "";
        }*/
        cursor.close();
        return sName;
    }


    // Getting data
    public List<DataBaseHelper> getRowData(int ID) {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALL + " WHERE " + " _id " + " = " + ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            //do {
            DataBaseHelper dataBaseHelper = new DataBaseHelper();

            dataBaseHelper.setID(Integer.parseInt(cursor.getString(0)));
            dataBaseHelper.setTagID(cursor.getString(1));
            dataBaseHelper.setName(cursor.getString(2));
            dataBaseHelper.setDesignation(cursor.getString(3));
            dataBaseHelper.setPhoneNumber(cursor.getString(4));
            dataBaseHelper.setEmailID(cursor.getString(5));
            dataBaseHelper.setDate(cursor.getString(6));
            dataBaseHelper.setTime(cursor.getString(7));
            dataBaseHelper.setLastSeen(cursor.getString(8));
            dataBaseHelper.setMajor(cursor.getString(9));
            dataBaseHelper.setMinor(cursor.getString(10));
            dataBaseHelper.setUUID(cursor.getString(11));
            dataBaseHelper.setRssi(cursor.getString(12));
            // Adding data to list
            dataBaseHelperList.add(dataBaseHelper);
            //} while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }

    // Getting data
    public List<DataBaseHelper> getAllRecentData() {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();

                dataBaseHelper.setID(Integer.parseInt(cursor.getString(0)));
                dataBaseHelper.setTagID(cursor.getString(1));
                dataBaseHelper.setName(cursor.getString(2));
                dataBaseHelper.setDesignation(cursor.getString(3));
                dataBaseHelper.setPhoneNumber(cursor.getString(4));
                dataBaseHelper.setEmailID(cursor.getString(5));
                dataBaseHelper.setDate(cursor.getString(6));
                dataBaseHelper.setTime(cursor.getString(7));
                dataBaseHelper.setLastSeen(cursor.getString(8));
                dataBaseHelper.setMajor(cursor.getString(9));
                dataBaseHelper.setMinor(cursor.getString(10));
                dataBaseHelper.setUUID(cursor.getString(11));
                dataBaseHelper.setRssi(cursor.getString(12));
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
            } while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }

    // Getting data
    public List<DataBaseHelper> getAllTabData() {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();

                dataBaseHelper.setID(Integer.parseInt(cursor.getString(0)));
                dataBaseHelper.setTagID(cursor.getString(1));
                dataBaseHelper.setName(cursor.getString(2));
                dataBaseHelper.setDesignation(cursor.getString(3));
                dataBaseHelper.setPhoneNumber(cursor.getString(4));
                dataBaseHelper.setEmailID(cursor.getString(5));
                dataBaseHelper.setDate(cursor.getString(6));
                dataBaseHelper.setTime(cursor.getString(7));
                dataBaseHelper.setLastSeen(cursor.getString(8));
                dataBaseHelper.setMajor(cursor.getString(9));
                dataBaseHelper.setMinor(cursor.getString(10));
                dataBaseHelper.setUUID(cursor.getString(11));
                dataBaseHelper.setRssi(cursor.getString(12));
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
            } while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }



    // Updating single data
    public int updateMultipleDataList(DataBaseHelper dataBaseHelper, int KEY_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String column = "last_seen";
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, dataBaseHelper.getName());
        values.put(KEY_NUMBER, dataBaseHelper.getName());
        values.put(KEY_EMAILID, dataBaseHelper.getName());
        values.put(KEY_DESIGNATION, dataBaseHelper.getName());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        return db.update(TABLE_RECENT, values, "_id" + " = " + KEY_ID, null);
        /*ContentValues data=new ContentValues();
        data.put("Field1","bob");
        DB.update(Tablename, data, "_id=" + id, null);*/
    }

    // Updating single data in all tab
    public int updateMultipleDataListAllTab(DataBaseHelper dataBaseHelper, int KEY_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String column = "last_seen";
        ContentValues values = new ContentValues();
        //values.put(KEY_NAME, dataBaseHelper.getName());
        //values.put(KEY_NUMBER, dataBaseHelper.getPhoneNumber());
        values.put(KEY_NAME, dataBaseHelper.getName());
        values.put(KEY_NUMBER, dataBaseHelper.getName());
        values.put(KEY_EMAILID, dataBaseHelper.getName());
        values.put(KEY_DESIGNATION, dataBaseHelper.getName());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        return db.update(TABLE_ALL, values, "_id" + " = " + KEY_ID, null);
        /*ContentValues data=new ContentValues();
        data.put("Field1","bob");
        DB.update(Tablename, data, "_id=" + id, null);*/
    }

    // Updating single data
    public int updateSingleDataList(DataBaseHelper dataBaseHelper, int KEY_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String column = "last_seen";
        ContentValues values = new ContentValues();
        //values.put(KEY_NAME, dataBaseHelper.getName());
        //values.put(KEY_NUMBER, dataBaseHelper.getPhoneNumber());
        values.put(KEY_LAST_SEEN_TIME, dataBaseHelper.getLastSeen());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        return db.update(TABLE_RECENT, values, "_id" + " = " + KEY_ID, null);
        /*ContentValues data=new ContentValues();
        data.put("Field1","bob");
        DB.update(Tablename, data, "_id=" + id, null);*/
    }

    // Updating single data in all tab
    public int updateSingleDataListAllTab(DataBaseHelper dataBaseHelper, int KEY_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String column = "last_seen";
        ContentValues values = new ContentValues();
        //values.put(KEY_NAME, dataBaseHelper.getName());
        //values.put(KEY_NUMBER, dataBaseHelper.getPhoneNumber());
        values.put(KEY_LAST_SEEN_TIME, dataBaseHelper.getLastSeen());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        return db.update(TABLE_ALL, values, "_id" + " = " + KEY_ID, null);
        /*ContentValues data=new ContentValues();
        data.put("Field1","bob");
        DB.update(Tablename, data, "_id=" + id, null);*/
    }

    public void clearDatabase(String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM " + TABLE_NAME;
        db.execSQL(clearDBQuery);
    }

    // Deleting single data
    public void deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_RECENT, KEY_ID + " = ?", new String[] { String.valueOf(recent.getID()) });
        db.delete(TABLE_ALL, KEY_ID + " = " + id, null);
        db.close();
    }

    // Getting recent Count
    public int getRecordsCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_ALL;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);


        if (cursor != null && !cursor.isClosed()) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    /*public boolean CheckIsDataAlreadyInDBorNot(String TableName,String dbfield, String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_ALL + " where " + dbfield + "="
                + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount<=0){
            return false;
        }
        return true;
    }*/
}
