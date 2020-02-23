package app_utility;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.sspl.org.tp.HomeScreen;
import com.sspl.org.tp.LoginActivity;

public class SharedPreferencesClass {

    // Context
    private Context _context;

    // Shared_pref file name
    //private static final String PREF_NAME = "TagProPref";

    private static final String APP_PREFERENCES = "TagProPreferences";

    private static final int PRIVATE_MODE = 0;

    // All Shared Preferences Keys
    // private static final String IS_LOGIN = "IsLoggedIn";


    // User name (make variable public to access from outside)
    //private static final String KEY_NAME = "name";

    //private static final String KEY_TAG_ID = "tagID";


    // Constructor
    public SharedPreferencesClass(Context context) {
        this._context = context;

        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();
    }

    /*
     * Create login session
     * */
    /*public void createLoginSession(String name, String tagID){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, false);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_TAG_ID, tagID);

        // commit changes
        editor.commit();
    }

    *//*
     * Get stored session data
     * *//*
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        // user name
        user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_TAG_ID, sharedPreferences.getString(KEY_TAG_ID, null));

        // return user
        return user;
    }

    *//*
     * Clear session details
     * *//*
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }*/

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */

    void redirectLoginScreen() {

        //new ProgressBarAsync().execute();
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public void reDirectToHomeScreen() {

        //new ProgressBarAsync().execute();
        Intent i = new Intent(_context, HomeScreen.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        // Staring Login Activity
        _context.startActivity(i);
    }

    void removeConfigData(){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean("IS_CONFIGURATION_COMPLETED", true);
        editor.remove("ID_ARRAYLIST");
        editor.remove("CATEGORY_ARRAYLIST");
        editor.apply();
    }

    public void setUserFirstTime(){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean("IS_USER_FIRST_TIME", false);
        editor.apply();
    }

    /*public void redirectToIRScreen() {

        //new ProgressBarAsync().execute();
        Intent i = new Intent(_context, HomeScreen.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }*/

}