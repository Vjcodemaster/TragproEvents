package com.sspl.org.tp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import app_utility.CircularProgressBar;
import app_utility.NetworkState;
import app_utility.OnToastInteractionInterface;
import app_utility.SharedPreferencesClass;
import app_utility.SnackBarToast;
import app_utility.StaticReferenceClass;
import app_utility.TagProAsyncTask;
import configuration.ConfigFragment;

import static app_utility.StaticReferenceClass.sForgotPasswordUrl;
import static app_utility.StaticReferenceClass.sLoginUrl;

public class LoginActivity extends AppCompatActivity implements OnToastInteractionInterface {

    //String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //boolean isLocationAccepted = false, isStorageAccepted = false;

    LinearLayout snackBar_llLayout;

    String sResponse = "";

    SnackBarToast mSnackBarToast;

    public OnToastInteractionInterface mToastInterface;

    private TagProAsyncTask mTagProAsyncTask = null;

    SharedPreferencesClass sPreferenceClass;

    // UI references.
    private EditText etID;
    private EditText etPassword;

    private Snackbar snackbar;

    private View snackBarView;

    NetworkState networkState;

    View focusView = null;

    boolean isFromForgotPassword = false;

    Button btnSignIn, btnForgotPassword;

    public String snackbarMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //readPermission();

        sPreferenceClass = new SharedPreferencesClass(LoginActivity.this);

        /*if (getLoggedInValue(getApplicationContext()) && getConfigurationValue(getApplicationContext())) {
            sPreferenceClass.reDirectToHomeScreen();
            LoginActivity.this.finish();
        } else if (getLoggedInValue(getApplicationContext()) && !getConfigurationValue(getApplicationContext())) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            loadFragment();
        }*/
        sPreferenceClass.reDirectToHomeScreen();

        mToastInterface = this;
        // Set up the login form.
        etID = (EditText) findViewById(R.id.et_ID);

        networkState = new NetworkState();

        snackBar_llLayout = (LinearLayout) findViewById(R.id.snackBar_llLayout);

        etPassword = (EditText) findViewById(R.id.password);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {

                /*as soon as the focus moves to this editBox respective hint will be shown
                this will also listen to soft keyboard Login button.
                just like real login button
                */
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    sResponse = getResources().getString(R.string.sign_in); //SIGN_IN
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        btnSignIn = (Button) findViewById(R.id.sign_in);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                /*
                *using the below (if else) we use same button for multipurpose, it will act has signIn as well as
                *Request password depending on the sResponse.
                */
                if (btnForgotPassword.getVisibility() == View.GONE) {
                    //this is used for switch case of which request to be send
                    sResponse = getResources().getString(R.string.forgot_password); //FORGOT_PASSWORD
                    String ID = etID.getText().toString();
                    decideActionUsingSwitch(sResponse, ID, "");
                } else {
                    sResponse = getResources().getString(R.string.sign_in);
                    attemptLogin();
                }
            }
        });

        btnForgotPassword = (Button) findViewById(R.id.btn_forgot_password);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromForgotPassword = true;
                forgotPassword();
                //changes the title of actionbar to forgot password
                if (LoginActivity.this.getSupportActionBar() != null) {
                    LoginActivity.this.getSupportActionBar().setTitle(getResources().getString(R.string.action_forgot_password));
                }
            }
        });

        snackbar = Snackbar.make(snackBar_llLayout, snackbarMessage, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackBarView = snackbar.getView();

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mTagProAsyncTask != null) {
            return;
        }
        // Reset errors.
        etID.setError(null);
        etPassword.setError(null);
        boolean cancel = false;

        // Store values at the time of the login attempt.
        String ID = etID.getText().toString();
        String password = etPassword.getText().toString();


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(ID)) {
            etID.setError(getString(R.string.error_field_required));
            focusView = etID;
            cancel = true;
        } else if (!isEmailValid(ID)) {
            etID.setError(getString(R.string.error_invalid_email));
            focusView = etID;
            cancel = true;
        }

        /*There was an error; don't attempt login and focus the first
            form field with an error.
            */
        if (cancel) {
            focusView.requestFocus();
        } else {
            decideActionUsingSwitch(sResponse, ID, password);
        }
    }

    private void forgotPassword() {
        etPassword.setVisibility(View.GONE);

        //setting focus to editTextBox
        focusView = etID;
        //focusing onto the editBox
        focusView.requestFocus();
        //shows message when editBox is focused
        etID.setError(getResources().getString(R.string.enter_id));

        //hides few components
        btnForgotPassword.setVisibility(View.GONE);
        btnSignIn.setText(getResources().getString(R.string.btn_request_password));
        LinearLayout llVisibility = (LinearLayout) findViewById(R.id.llVisibility);
        llVisibility.setVisibility(View.GONE);
    }

    /*private boolean isEmailValid(String email) {
        return email.contains("@");
    }*/
    //checking number of characters of ID entered
    private boolean isEmailValid(String ID) {
        return ID.length() == 4;
    }

    //checking number of characters of entered password
    private boolean isPasswordValid(String password) {
        return password.length() == 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }*/

    //shows snackbar if there is no Internet connection
    public void noInternetConnectivity() {
        mSnackBarToast = new SnackBarToast(getApplicationContext(), snackBarView, snackbar, snackbarMessage);
        mSnackBarToast.execute(1);
    }

    /*this will be executed only after checking all the errors in entered ID and Password
    * Then execute the case depending upon the sResponse received
    * */
    private void decideActionUsingSwitch(String sResponse, String ID, String password) {
        switch (sResponse) {
            case "SIGN_IN":
                //Toast.makeText(LoginActivity.this, "from Sign In switch", Toast.LENGTH_LONG).show();
                if (!networkState.isOnline() && !networkState.isNetworkAvailable(LoginActivity.this)) {
                    noInternetConnectivity();
                } else {
                    CircularProgressBar circularProgressBar = new CircularProgressBar(LoginActivity.this);
                    circularProgressBar.setCanceledOnTouchOutside(false);
                    circularProgressBar.setCancelable(false);
                    circularProgressBar.show();
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    mTagProAsyncTask = new TagProAsyncTask(ID, password, LoginActivity.this, sPreferenceClass, circularProgressBar, mToastInterface);
                    mTagProAsyncTask.execute("1", sLoginUrl); // + "test"
                    mTagProAsyncTask = null;
                    isFromForgotPassword = false;
                }
                break;
            case "FORGOT_PASSWORD":
                //Toast.makeText(LoginActivity.this, "from Forgot password switch", Toast.LENGTH_LONG).show();
                if (!networkState.isOnline() && !networkState.isNetworkAvailable(LoginActivity.this)) {
                    noInternetConnectivity();
                } else {
                    CircularProgressBar circularProgressBar = new CircularProgressBar(LoginActivity.this);
                    circularProgressBar.setCanceledOnTouchOutside(false);
                    circularProgressBar.setCancelable(false);
                    circularProgressBar.show();
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    mTagProAsyncTask = new TagProAsyncTask(ID, password, LoginActivity.this, sPreferenceClass, circularProgressBar, mToastInterface);
                    mTagProAsyncTask.execute("2", sForgotPasswordUrl);
                    mTagProAsyncTask = null;
                    isFromForgotPassword = false;
                }
                break;
            default:
                System.out.println("None of the above is executed");
        }
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        if (isFromForgotPassword) {
            Intent in = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(in);
        }
        super.onBackPressed();  // optional depending on your needs
    }

    public boolean getLoggedInValue(Context context) {
        SharedPreferences sharedPreferences;
        boolean isLoggedIn;
        sharedPreferences = context.getSharedPreferences(StaticReferenceClass.sAPP_PREFERENCES, Context.MODE_PRIVATE); //1
        isLoggedIn = sharedPreferences.getBoolean(getResources().getString(R.string.is_login), false); //2
        return isLoggedIn;
    }

    public boolean getConfigurationValue(Context context) {
        SharedPreferences sharedPreferences;
        boolean isConfigCompleted;
        sharedPreferences = context.getSharedPreferences(StaticReferenceClass.sAPP_PREFERENCES, Context.MODE_PRIVATE); //1
        isConfigCompleted = sharedPreferences.getBoolean(getResources().getString(R.string.is_configuration_completed), false); //2
        return isConfigCompleted;
    }

    public void hideKeyboard() {
        //hiding keyboard
        View v = getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert v != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /*public void readPermission()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    alPermission.add(p);
                    String temp = p.replace("android", "Manifest");
                    //PERMISSIONS[alPermission.size()] = temp;
                    Log.d("PERMISSION", "Permission : " + p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    /*public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }*/

    @Override
    public void onRequestPermissionsResult(int PERMISSION_ALL, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (PERMISSION_ALL) {
            case 2: /*{
                // If request is cancelled, the result arrays are empty.
                //isLocationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                //isStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

            }*/
                break;
               /* if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }*/
        }
    }

    @Override
    public void onResultReceived(String sMessage, int nCase, String sActivityName) {
        switch (sActivityName) {
            case "LOGIN_ACTIVITY":
                SnackBarToast mSnackBarToast = new SnackBarToast(LoginActivity.this, snackBarView, snackbar, snackbarMessage);
                mSnackBarToast.execute(nCase);
                break;
            case "CONFIGURATION":
                loadFragment();
                break;
            case "SCANNING":
                //loadScanningFragment();
                break;
        }
    }

    private void loadFragment() {
        SharedPreferences sharedPreferences;
        sharedPreferences = LoginActivity.this.getSharedPreferences(StaticReferenceClass.sAPP_PREFERENCES, Context.MODE_PRIVATE); //1
        String sIDSPreferences = sharedPreferences.getString(getResources().getString(R.string.id_arraylist), "");
        String sCategorySPreferences = sharedPreferences.getString(getResources().getString(R.string.category_arraylist), "");
        ArrayList<String> alID = new ArrayList<>(Arrays.asList(sIDSPreferences.split(",")));
        ArrayList<String> alCategory = new ArrayList<>(Arrays.asList(sCategorySPreferences.split(",")));
        int nUserType = sharedPreferences.getInt(getResources().getString(R.string.user_type), 1);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ConfigFragment();
        Bundle mBundle = new Bundle();
        mBundle.putStringArrayList("IDArray", alID);
        mBundle.putStringArrayList("CategoryArray", alCategory);
        mBundle.putInt("userType", nUserType);
        fragment.setArguments(mBundle);
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }
}
