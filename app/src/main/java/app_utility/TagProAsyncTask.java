package app_utility;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sspl.org.tp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import scanning.DataBaseHelper;
import scanning.DatabaseHandler;

import static app_utility.StaticReferenceClass.portNo;
import static app_utility.StaticReferenceClass.sAPP_PREFERENCES;
import static app_utility.StaticReferenceClass.sSERVERURL;

//@SuppressWarnings("ALL")
public class TagProAsyncTask extends AsyncTask<String, Void, String> {
    private String sUserId = "";
    private String sPassword = "";
    private String res;
    private Activity aActivity;
    private SharedPreferencesClass sPreferenceClass;
    private SharedPreferences sharedPreferences;

    private String sSessionID = "";
    private String sException = "";

    private int nVisibility;

    private boolean isReceiveInfoChecked;

    private ArrayList<String> alSavedCheckBoxTag;

    private ArrayList<ArrayList<String>> alTempBeaconInfo;

    private OnToastInteractionInterface mToastInterface;

    private String sDate, sTime;

    private Snackbar snackbar;
    private View snackBarView;

    private String snackbarMessage = "";

    private SnackBarToast mSnackBarToast = null;

    //private String sUUID, sMajor, sMinor, sRSSI;

    private Dialog mDialog;

    private CircularProgressBar circularProgressBar;

    private Context mContext;

    private ServiceInterface serviceInterface;

    private ArrayList<String> alBeaconInfo = new ArrayList<>();


    public TagProAsyncTask(String nUserId, String sPassword, Activity aActivity, SharedPreferencesClass sPreferenceClass,
                           CircularProgressBar circularProgressBar, OnToastInteractionInterface mToastInterface) {
        this.sPassword = sPassword;
        this.sUserId = nUserId;
        this.aActivity = aActivity;
        this.sPreferenceClass = sPreferenceClass;
        this.circularProgressBar = circularProgressBar;
        this.mToastInterface = mToastInterface;
    }

    public TagProAsyncTask(Activity aActivity, String sSessionID, int nVisibility, boolean isReceiveInfoChecked,
                           ArrayList<String> alSavedCheckBoxTag, SharedPreferencesClass sPreferenceClass,
                           CircularProgressBar circularProgressBar, OnToastInteractionInterface mToastInterface) {
        this.aActivity = aActivity;
        this.sSessionID = sSessionID;
        this.nVisibility = nVisibility;
        this.isReceiveInfoChecked = isReceiveInfoChecked;
        this.alSavedCheckBoxTag = alSavedCheckBoxTag;
        this.sPreferenceClass = sPreferenceClass;
        this.circularProgressBar = circularProgressBar;
        this.mToastInterface = mToastInterface;
    }

    public TagProAsyncTask(Context context, String sSessionID, SharedPreferencesClass sPreferenceClass, ArrayList<ArrayList<String>> alTempBeaconInfo,
                           String sTime, String sDate, ServiceInterface serviceInterface) {
        this.mContext = context;
        this.sSessionID = sSessionID;
        this.sPreferenceClass = sPreferenceClass;
        this.alTempBeaconInfo = alTempBeaconInfo;
        this.sTime = sTime;
        this.sDate = sDate;
        this.serviceInterface = serviceInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        int type = Integer.parseInt(params[0]);
        int nSourceAppId = 2;
        switch (type) {
            case 1:
                res = LogInPost(params[1], sUserId, sPassword, nSourceAppId);
                break;
            case 2:
                res = forgotPassword(params[1], sUserId, nSourceAppId);
                break;
            case 3:
                res = Category(params[1], sSessionID, nVisibility, isReceiveInfoChecked, alSavedCheckBoxTag);
                break;
            case 4:
                res = GetBeaconInfo(params[1], sSessionID, alTempBeaconInfo, sTime, sDate);
                break;
            case 5:
                //res = FetchBeaconInfo(params[1], sSessionID, sUUID, sMajor, sMinor, sRSSI, sDate, sTime);
                break;

        }
        return res;

    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        JSONArray jsonArray;
        JSONObject jsonExtract;
        JSONArray jsonArrayTemp;
        String sFirstName = "", sEmailID = "", sMobileNumber = "", sDesignation = "";
        String TagId = "";
        final DatabaseHandler db = new DatabaseHandler(mContext);
        if (sException.equals("connect timed out") || result.equals("timeout")) {

            mToastInterface.onResultReceived("", 4, aActivity.getResources().getString(R.string.login_activity));
        } else if (result.contains("{")) {
            JSONObject jsonResponse;

            try {
                jsonResponse = new JSONObject(result);
                int nResponse = jsonResponse.getInt("responseCode");
                SharedPreferences.Editor editor;
                if (aActivity != null) {
                    sharedPreferences = aActivity.getApplicationContext().getSharedPreferences(sAPP_PREFERENCES, Context.MODE_PRIVATE);
                } else {
                    sharedPreferences = mContext.getSharedPreferences(sAPP_PREFERENCES, Context.MODE_PRIVATE);
                }
                switch (nResponse) {
                    case 777:
                        try {
                            //notifyAll();
                            jsonArray = jsonResponse.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonExtract = jsonArray.getJSONObject(i);

                                jsonArrayTemp = jsonExtract.getJSONArray("Name");
                                JSONObject jsonName = jsonArrayTemp.getJSONObject(0);
                                sFirstName = jsonName.getString("FirstName");

                                //accessing contact details array
                                jsonArrayTemp = jsonExtract.getJSONArray("Contact");
                                JSONObject jsonContact = jsonArrayTemp.getJSONObject(0);
                                sMobileNumber = jsonContact.getString("Mobilenumber");
                                sEmailID = jsonContact.getString("EmailID");

                                //accessing organization info
                                jsonArrayTemp = jsonExtract.getJSONArray("Organization");

                                JSONObject jsonOrganization = jsonArrayTemp.getJSONObject(0);
                                String sTagID = jsonOrganization.getString("TagID");
                                sDesignation = jsonOrganization.getString("Name");
                                boolean isDataPresentInAllTab;
                                int nID = db.getIdForStringTabAll(sTagID);
                                isDataPresentInAllTab = nID != -1;
                                if (!isDataPresentInAllTab) {
                                    db.addData(new DataBaseHelper(sTagID, sFirstName, sDesignation, sMobileNumber, sEmailID,
                                            sDate, sTime, sTime));
                                }
                            }

                            if (sFirstName != null && !sFirstName.equals("")) {

                                //this is created to transfer data to Broadcast receiver using putExtra
                                Intent recyclerIntent = new Intent("android.intent.action.RECYCLER_NOTIFY");
                                mContext.sendBroadcast(recyclerIntent);
                            }

                            serviceInterface.onServiceCallBack("UPDATE_ARRAYLIST", "", true, alTempBeaconInfo);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 803:
                        /*editor = sharedPreferences.edit();
                        *//*saving sUserId into sharedPreferences whose value can be accessed using "USER_ID"
                        same applies for "IS_LOGIN"
                         *//*
                        editor.putString("USER_ID", String.valueOf(sUserId));
                        editor.putBoolean("IS_LOGIN", true);
                        editor.apply();*/
                        snackbarMessage = aActivity.getApplicationContext().getResources().getString(R.string.user_does_not_exist);
                        mSnackBarToast = new SnackBarToast(aActivity.getApplicationContext(), snackBarView, snackbar, snackbarMessage);
                        mSnackBarToast.execute(3);

                        //mToastInterface.onResultReceived("", 3, aActivity.getResources().getString(R.string.login_activity));
                        /*final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sPreferenceClass.redirectLoginScreen();
                            }
                        }, 4000);*/
                        //sPreferenceClass.redirectLoginScreen();

                        //Toast.makeText(context, "User ID doesn't Exist", Toast.LENGTH_LONG).show();
                        //sPreferenceClass.userHasAlreadyLoggedIn(); //calling method of sPreferenceClass once success
                        break;
                    case 807:
                        //redirecting to login screen as we have made few components invisible for forgot pw
                        sPreferenceClass.redirectLoginScreen();
                        break;
                    case 809:
                        Toast.makeText(aActivity.getApplicationContext(), "Password Updated", Toast.LENGTH_LONG).show();
                        break;
                    case 815:
                        Toast.makeText(aActivity.getApplicationContext(), "Beacon data sent", Toast.LENGTH_LONG).show();
                        break;
                    case 826:
                        Toast.makeText(aActivity.getApplicationContext(), "Check your Mail", Toast.LENGTH_LONG).show();
                        break;
                    case 901:
                        Toast.makeText(aActivity.getApplicationContext(), "Configuration Data updated", Toast.LENGTH_LONG).show();
                        break;
                    case 800:
                        Toast.makeText(aActivity.getApplicationContext(), "system error,server error", Toast.LENGTH_LONG).show();
                        break;
                    case 801:
                        Toast.makeText(aActivity.getApplicationContext(), "check your User ID", Toast.LENGTH_LONG).show();
                        break;
                    case 802:
                        /*we are casting loginActivity to context that we received in TagProAsyncTask constructor
                        here loginActivity is a reference of LoginActivity so that method inside LoginActivity can
                        be called using this reference
                        */
                        sPreferenceClass.redirectLoginScreen();
                        /*loginActivity.invalidUserNamePassword();
                        //assigning string value to one of the string that is in LoginActivity
                        loginActivity.snackbarMessage = aActivity.getApplicationContext().getResources().getString(R.string.invalid_username_password);*/
                        //Toast.makeText(context, "check your User ID and Password", Toast.LENGTH_LONG).show();
                        break;
                    case 804:
                        snackbarMessage = aActivity.getApplicationContext().getResources().getString(R.string.user_does_not_exist);
                        mSnackBarToast = new SnackBarToast(aActivity.getApplicationContext(), snackBarView, snackbar, snackbarMessage);
                        mSnackBarToast.execute(3);
                        //mToastInterface.onResultReceived("", 3, aActivity.getResources().getString(R.string.login_activity));
                        //permission denied
                        break;
                    case 805:
                        //int nUserType = jsonResponse.getInt("usertype");
                        int nUserType = 0;
                        editor = sharedPreferences.edit();

                        /*saving sUserId into sharedPreferences whose value can be accessed using "USER_ID"
                        same applies for "IS_LOGIN"
                         */
                        editor.putString(aActivity.getResources().getString(R.string.user_id), String.valueOf(sUserId));
                        editor.putBoolean(aActivity.getResources().getString(R.string.is_login), true);
                        editor.putInt(aActivity.getResources().getString(R.string.user_type), nUserType);
                        editor.putBoolean(aActivity.getResources().getString(R.string.is_configuration_completed), false);
                        //editor.apply();

                        ArrayList<String> alID = new ArrayList<>();
                        ArrayList<String> alCategory = new ArrayList<>();
                        try {
                            jsonArray = jsonResponse.getJSONArray("key");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonExtract = jsonArray.getJSONObject(i);
                                alID.add(jsonExtract.getString("_id"));
                                alCategory.add(jsonExtract.getString("Name"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String sIDSPreferences = alID.toString().substring(1, alID.toString().length() - 1).replace(" ", "");
                        String sCategorySPreferences = alCategory.toString().substring(1, alCategory.toString().length() - 1).replace(" ", "");
                        editor.putString(aActivity.getResources().getString(R.string.id_arraylist), sIDSPreferences);
                        editor.putString(aActivity.getResources().getString(R.string.category_arraylist), sCategorySPreferences);
                        editor.commit();

                        mToastInterface.onResultReceived("", 3, aActivity.getResources().getString(R.string.configuration));
                        //int nUserType = jsonResponse.getInt("usertype");
                        /*Fragment fragment = new ConfigFragment();
                        Bundle mBundle = new Bundle();
                        mBundle.putStringArrayList("IDArray", alID);
                        mBundle.putStringArrayList("CategoryArray", alCategory);
                        mBundle.putInt("userType", nUserType);
                        fragment.setArguments(mBundle);
                        responseSuccess(fragment);*/
                        //sPreferenceClass.reDirectToHomeScreen();
                        //responseSuccess(fragment);
                        //aActivity.finish();
                        break;
                    case 806:
                        snackbarMessage = aActivity.getApplicationContext().getResources().getString(R.string.user_does_not_exist);
                        mSnackBarToast = new SnackBarToast(aActivity.getApplicationContext(), snackBarView, snackbar, snackbarMessage);
                        mSnackBarToast.execute(3);
                        //Toast.makeText(aActivity.getApplicationContext(), "check your User ID", Toast.LENGTH_LONG).show();
                        break;
                    case 817:
                        /*success from server regarding configure screen.
                        redirect to home screen or scanning depending on the user.
                        then clear configuration data that is saved in shared preference as it is no longer useful.
                        */
                        //int userType;
                        //userType = sharedPreferences.getInt(aActivity.getResources().getString(R.string.user_type), 0);

                        //if (userType == 1) {
                        sPreferenceClass.reDirectToHomeScreen();
                        aActivity.finish();
                        sPreferenceClass.removeConfigData();
                        /*} else {
                            mToastInterface.onResultReceived("", 3, aActivity.getResources().getString(R.string.scanning));
                        }*/
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if ((circularProgressBar != null) && circularProgressBar.isShowing()) {
            try {
                circularProgressBar.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        /*if (mDialog != null)
            if (mDialog.isShowing())
                mDialog.dismiss();*/
    }


    private String LogInPost(String uri, String nUserId, String sPassword, int nSourceAppId) {
        HttpsURLConnection urlConnection;
        String result = "";


        SharedPreferences.Editor editor;
        sharedPreferences = aActivity.getApplicationContext().getSharedPreferences(sAPP_PREFERENCES, Context.MODE_PRIVATE);
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream caInput;
            InputStream in;
            caInput = new BufferedInputStream(aActivity.getApplicationContext().getAssets().open("sohamsaa.crt"));
            in = aActivity.getApplicationContext().getResources().openRawResource(R.raw.mykeystore);
            //InputStream caInput = new BufferedInputStream(aActivity.getApplicationContext().getAssets().open("sohamsaa.crt"));
            Certificate certificate;
            certificate = certificateFactory.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) certificate).getSubjectDN());
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            //in = aActivity.getApplicationContext().getResources().openRawResource(R.raw.mykeystore);
            keyStore.load(in, "mysecret".toCharArray());
            keyStore.setCertificateEntry("certificate", certificate);
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
            trustManagerFactory.init(keyStore);
            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManagerFactory.getTrustManagers(), null);
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    //HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                    return true;
                }
            };

            try {

                urlConnection = (HttpsURLConnection) ((new URL(uri).openConnection()));
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setHostnameVerifier(hostnameVerifier);
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(20000);
                urlConnection.setInstanceFollowRedirects(false);

                urlConnection.connect();
                urlConnection.getServerCertificates();     // Acceptable response from server

                JSONObject jsonObject = new JSONObject();
                //jsonObject.put("Reader_id", mLogInDetails.getsReaderId());
                jsonObject.put("password", sPassword);
                jsonObject.put("UserID", nUserId);
                jsonObject.put("sourceApp", nSourceAppId);

                /*String data = "test";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", data);*/


                OutputStream outputStream;
                outputStream = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(jsonObject.toString());  // write json object here
                writer.flush();
                outputStream.flush();
                writer.close();
                outputStream.close();

                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                result = sb.toString();

                //creating object of sharedPreferences to store session value
                Map<String, List<String>> headerFields = urlConnection.getHeaderFields(); // fetch headers using url methods
                List<String> cookieList = headerFields.get("Set-Cookie");
                CookieManager cookieManager = new CookieManager();
                if (cookieList != null) {

                    String cookie = cookieList.get(0);
                    CookieStore store = cookieManager.getCookieStore();
                    HttpCookie cookieValue = HttpCookie.parse(cookie).get(0);
                    String cookies = cookieValue.toString();
                    String[] sessionId = cookies.split("=");

                    //storing sessionID to sharedPreferences
                    editor = sharedPreferences.edit();
                    editor.putString("sessionID", sessionId[1]);
                    editor.apply();


                    //mUtility.setData("sessionID", sessionId[1], aActivity);

                    store.add(new URI("key"), cookieValue);
                    Log.e("sessionIdLogin", sessionId[1]);
                }
                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(sSERVERURL, portNo);
                HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                SSLSession sslSession = sslSocket.getSession();
                // Verify that the certificate hostname is for mail.google.com
                // This is due to lack of SNI support in the current SSLSocket.
                if (!defaultHostnameVerifier.verify(sSERVERURL + portNo, sslSession)) {
                    throw new SSLHandshakeException("Expected " + sSERVERURL + portNo + "found " + sslSession.getPeerPrincipal());
                }
                // At this point SSLSocket performed certificate verification and
                // we have performed hostname verification, so it is safe to proceed.
                // sslSocket use socket ...
                sslSocket.close();

                return result;
                //result = urlConnection.getResponseMessage();
            } catch (IOException | JSONException | URISyntaxException e) {
                sException = e.getMessage();
                e.printStackTrace();
            }
            //  return result
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String forgotPassword(String uri, String nUserId, int nSourceAppId) {
        HttpsURLConnection urlConnection;
        String result;

        SharedPreferences.Editor editor;
        sharedPreferences = aActivity.getApplicationContext().getSharedPreferences(sAPP_PREFERENCES, Context.MODE_PRIVATE);
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream caInput;
            InputStream in;
            caInput = new BufferedInputStream(aActivity.getApplicationContext().getAssets().open("sohamsaa.crt"));
            in = aActivity.getApplicationContext().getResources().openRawResource(R.raw.mykeystore);
            //caInput = new BufferedInputStream(aActivity.getApplicationContext().getAssets().open("sohamsaa.crt"));
            //in = aActivity.getApplicationContext().getResources().openRawResource(R.raw.mykeystore);
            Certificate certificate;
            certificate = certificateFactory.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) certificate).getSubjectDN());
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(in, "mysecret".toCharArray());
            keyStore.setCertificateEntry("certificate", certificate);
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
            trustManagerFactory.init(keyStore);
            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManagerFactory.getTrustManagers(), null);
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    //HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                    return true;
                }
            };

            try {

                urlConnection = (HttpsURLConnection) ((new URL(uri).openConnection()));
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setHostnameVerifier(hostnameVerifier);
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(15000);
                urlConnection.setInstanceFollowRedirects(false);

                urlConnection.connect();
                urlConnection.getServerCertificates();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("UserID", nUserId);
                jsonObject.put("sourceApp", nSourceAppId);


                OutputStream outputStream;
                outputStream = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(jsonObject.toString());  // write json object here
                writer.flush();
                outputStream.flush();
                writer.close();
                outputStream.close();

                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                result = sb.toString();

                //creating object of sharedPreferences to store session value
                Map<String, List<String>> headerFields = urlConnection.getHeaderFields(); // fetch headers using url methods
                List<String> cookieList = headerFields.get("Set-Cookie");
                CookieManager cookieManager = new CookieManager();
                if (cookieList != null) {

                    String cookie = cookieList.get(0);
                    CookieStore store = cookieManager.getCookieStore();
                    HttpCookie cookieValue = HttpCookie.parse(cookie).get(0);
                    String cookies = cookieValue.toString();
                    String[] sessionId = cookies.split("=");

                    //storing sessionID to sharedPreferences

                    editor = sharedPreferences.edit();
                    editor.putString("sessionID", sessionId[1]);
                    editor.apply();
                    //mUtility.setData("sessionID", sessionId[1], aActivity);

                    store.add(new URI("key"), cookieValue);
                    Log.e("sessionIdLogin", sessionId[1]);
                }
                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(sSERVERURL, portNo);
                HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                SSLSession sslSession = sslSocket.getSession();
                // Verify that the certificate hostname is for mail.google.com
                // This is due to lack of SNI support in the current SSLSocket.
                if (!defaultHostnameVerifier.verify(sSERVERURL + portNo, sslSession)) {
                    throw new SSLHandshakeException("Expected " + sSERVERURL + portNo + "found " + sslSession.getPeerPrincipal());
                }
                // At this point SSLSocket performed certificate verification and
                // we have performed hostname verification, so it is safe to proceed.
                // sslSocket use socket ...
                sslSocket.close();


                return result;
                //result = urlConnection.getResponseMessage();


            } catch (IOException | JSONException e) {
                result = e.getMessage();
                e.printStackTrace();
            }

            return result;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | URISyntaxException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String Category(String uri, String sSessionID, int nVisibility, boolean isReceiveInfoChecked, ArrayList<String> alSavedCheckBoxTag) {
        HttpsURLConnection urlConnection;
        String result = "";

        //SharedPreferences.Editor editor;
        sharedPreferences = aActivity.getApplicationContext().getSharedPreferences(sAPP_PREFERENCES, Context.MODE_PRIVATE);
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream caInput;
            InputStream in;
            caInput = new BufferedInputStream(aActivity.getApplicationContext().getAssets().open("sohamsaa.crt"));
            in = aActivity.getApplicationContext().getResources().openRawResource(R.raw.mykeystore);
            /*InputStream caInput = new BufferedInputStream(aActivity.getApplicationContext().getAssets().open("sohamsaa.crt"));
            InputStream in = aActivity.getApplicationContext().getResources().openRawResource(R.raw.mykeystore);*/
            Certificate certificate;
            certificate = certificateFactory.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) certificate).getSubjectDN());
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(in, "mysecret".toCharArray());
            keyStore.setCertificateEntry("certificate", certificate);
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
            trustManagerFactory.init(keyStore);
            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManagerFactory.getTrustManagers(), null);
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    //HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                    return true;
                }
            };

            try {
                CookieManager cookieManager = new CookieManager();
                CookieHandler.setDefault(cookieManager);

                urlConnection = (HttpsURLConnection) ((new URL(uri).openConnection()));
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("sessionid", sSessionID);
                urlConnection.setHostnameVerifier(hostnameVerifier);
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(15000);
                urlConnection.setInstanceFollowRedirects(false);

                urlConnection.connect();
                urlConnection.getServerCertificates();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("RecInfo", isReceiveInfoChecked);
                jsonObject.put("Privacy", nVisibility);
                jsonObject.put("Ioi", alSavedCheckBoxTag);

                /*for(int i=0;i<alSavedCheckBoxTag.size(); i++){

                }*/

                OutputStream outputStream;
                outputStream = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(jsonObject.toString());  // write json object here
                writer.flush();
                outputStream.flush();
                writer.close();
                outputStream.close();

                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                result = sb.toString();

                /*//creating object of sharedPreferences to store session value
                Map<String, List<String>> headerFields = urlConnection.getHeaderFields(); // fetch headers using url methods
                List<String> cookieList = headerFields.get("Set-Cookie");
                CookieManager cookieManager = new CookieManager();
                if (cookieList != null) {

                    String cookie = cookieList.get(0);
                    CookieStore store = cookieManager.getCookieStore();
                    HttpCookie cookieValue = HttpCookie.parse(cookie).get(0);
                    String cookies = cookieValue.toString();
                    String[] sessionId = cookies.split("=");

                    //storing sessionID to sharedPreferences

                    editor = sharedPreferences.edit();
                    editor.putString("sessionID", sessionId[1]);
                    editor.apply();
                    //mUtility.setData("sessionID", sessionId[1], aActivity);

                    store.add(new URI("key"), cookieValue);
                    Log.e("sessionIdLogin", sessionId[1]);
                }*/
                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(sSERVERURL, portNo);
                HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                SSLSession sslSession = sslSocket.getSession();
                // Verify that the certificate hostname is for mail.google.com
                // This is due to lack of SNI support in the current SSLSocket.
                if (!defaultHostnameVerifier.verify(sSERVERURL + portNo, sslSession)) {
                    throw new SSLHandshakeException("Expected " + sSERVERURL + portNo + "found " + sslSession.getPeerPrincipal());
                }
                // At this point SSLSocket performed certificate verification and
                // we have performed hostname verification, so it is safe to proceed.
                // sslSocket use socket ...
                sslSocket.close();


                return result;
                //result = urlConnection.getResponseMessage();


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return result;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        return result;
    }


    private String GetBeaconInfo(String uri, String sSessionID, ArrayList<ArrayList<String>> alTempBeaconInfo, String sTime, String sDate) {
        HttpsURLConnection urlConnection;
        String result = "";

        //SharedPreferences.Editor editor;
        //sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(mContext.getAssets().open("sohamsaa.crt"));
            Certificate certificate;
            certificate = certificateFactory.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) certificate).getSubjectDN());
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            InputStream in = mContext.getResources().openRawResource(R.raw.mykeystore);
            keyStore.load(in, "mysecret".toCharArray());
            keyStore.setCertificateEntry("certificate", certificate);
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
            trustManagerFactory.init(keyStore);
            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManagerFactory.getTrustManagers(), null);
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    //HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                    return true;
                }
            };

            try {
                CookieManager cookieManager = new CookieManager();
                CookieHandler.setDefault(cookieManager);

                urlConnection = (HttpsURLConnection) ((new URL(uri).openConnection()));
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("sessionid", sSessionID);
                urlConnection.setHostnameVerifier(hostnameVerifier);
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(20000);
                urlConnection.setReadTimeout(20000);
                urlConnection.setInstanceFollowRedirects(false);

                urlConnection.connect();
                urlConnection.getServerCertificates();

                JSONObject jsonParentObject = new JSONObject();

                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < alTempBeaconInfo.size(); i++) {
                    JSONObject objectArray = new JSONObject();
                    ArrayList<String> alTemp = new ArrayList<>();
                    alTemp.addAll(alTempBeaconInfo.get(i));
                    objectArray.put("MajorNumber", alTemp.get(1));
                    objectArray.put("MinorNumber", alTemp.get(2));
                    objectArray.put("UUID", alTemp.get(3));
                    objectArray.put("RSSI", alTemp.get(4));
                    jsonArray.put(i, objectArray);
                    alTemp.clear();
                }

                jsonParentObject.put("scanData", jsonArray);
                jsonParentObject.put("IrBeaconDate", sDate);
                jsonParentObject.put("Time", sTime);

                OutputStream outputStream;
                outputStream = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(jsonParentObject.toString());  // write json object here
                writer.flush();
                outputStream.flush();
                writer.close();
                outputStream.close();

                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                result = sb.toString();

                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(sSERVERURL, portNo);
                HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                SSLSession sslSession = sslSocket.getSession();
                // Verify that the certificate hostname is for mail.google.com
                // This is due to lack of SNI support in the current SSLSocket.
                if (!defaultHostnameVerifier.verify(sSERVERURL + portNo, sslSession)) {
                    throw new SSLHandshakeException("Expected " + sSERVERURL + portNo + "found " + sslSession.getPeerPrincipal());
                }
                // At this point SSLSocket performed certificate verification and
                // we have performed hostname verification, so it is safe to proceed.
                // sslSocket use socket ...
                sslSocket.setKeepAlive(true);
                sslSocket.close();


                return result;
                //result = urlConnection.getResponseMessage();


            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e("Time out Exception", e.toString());
            }

            return result;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*private void responseSuccess(Fragment fragment) {
        FragmentTransaction transaction = ((FragmentActivity) aActivity).getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.animation, R.anim.animation);
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/
}