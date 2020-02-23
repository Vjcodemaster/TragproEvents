package scanning;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.TimingLogger;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;

import app_utility.NetworkState;
import app_utility.ServiceInterface;
import app_utility.SharedPreferencesClass;
import app_utility.TagProAsyncTask;
import receivers.BootCompleteReceiver;

import static app_utility.StaticReferenceClass.sGETTAGS;


public class SimpleService extends Service implements BeaconConsumer, ServiceInterface {
    protected static final String TAG = "BeaconService";

    SharedPreferences sharedPreferences;
    SharedPreferencesClass sPreferenceClass;
    NetworkState networkState;

    public static String APP_PREFERENCES = "TagProPreferences";

    public static SimpleService refofservice;

    MediaPlayer mp = new MediaPlayer();
    int mapKey = 0;
    //NotificationManager notifyMgr;
    //NotificationCompat.Builder nBuilder;
    //NotificationCompat.InboxStyle inboxStyle;
    String sFirstNotification = "";
    String sSecondNotification = "";

    //public HashSet<String> sHashSetTemp = new HashSet<>();
    //public ArrayList<String> alBeaconInfo = new ArrayList<>();
    //public ArrayList<String> alTagID = new ArrayList<>();
    //public ArrayList<String> alTempBeaconInfo = new ArrayList<>();
    //public ArrayList<String> alPreviousBeaconInfo = new ArrayList<>();

    private LinkedHashMap<Integer, ArrayList<String>> tempLinkedHM = new LinkedHashMap<>();
    private LinkedHashMap<Integer, ArrayList<String>> permanentLinkedHM = new LinkedHashMap<>();

    //private boolean isNewDataAdded = false;

    String sMajor = "";
    String sMinor = "";
    String sUUID = "";
    String sRSSI = "";
    String sDate = "";
    String sTime = "";
    String sPreviousTime = "";

    //HandlerThread handlerThread = null;

    //private String previousTagId = "";
    //private int compareTime = -1;
    //private int compareSizeWithPrevious = 0;
    private long startTime;

    private BeaconManager beaconManager;

    String sSessionID;

    TagProAsyncTask mTagProAsyncTask = null;

    TimingLogger timings;

    public static ServiceInterface serviceInterface;

    DatabaseHandler db;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHandler(getApplicationContext());
        Log.i(TAG, "Service created ...");
        beaconManager = BeaconManager.getInstanceForApplication(getBaseContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        sharedPreferences = getApplicationContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE); //1

        beaconManager.setBackgroundScanPeriod(1000L);
        beaconManager.setBackgroundBetweenScanPeriod(0L);
        /*beaconManager.setBackgroundScanPeriod(1100l);
        beaconManager.setForegroundBetweenScanPeriod(5000l);*/
        sPreferenceClass = new SharedPreferencesClass(getApplicationContext());
        networkState = new NetworkState();
        refofservice = this;
        serviceInterface = this;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand called");
        timings = new TimingLogger("beacon", "Beacon For loop");
        beaconManager.setBackgroundMode(false);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        beaconManager.unbind(this);
        Toast.makeText(SimpleService.this, "service destroyed", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Service destroyed ...");
        //sHashSetAttractionData.clear();
        //sHashSetDnT.clear();
        //sHashSetAttractionName.clear();

        sFirstNotification = "";
        sSecondNotification = "";
        stopSelf();
        SimpleService.this.stopSelf();
        Intent in = new Intent(SimpleService.this, BootCompleteReceiver.class);
        startService(in);
        /*Intent service = new Intent(this, SimpleService.class);
        startService(service);*/
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "<<< onBeaconServiceConnect  >>>");
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "onBeaconServiceConnect \ngetId1: " + region.getId1() + "\ngetId2: " + region.getId2() + "\ngetId3: " +
                        region.getId3());
                Log.i(TAG, "**************-------------****************");
                logBeaconData(true);
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "********!!!!!!!!! didExitRegion !!!!!!!!!!*******");
                mapKey = 0;
                logBeaconData(false);
                try {
                    beaconManager.stopRangingBeaconsInRegion(new Region("sBeacon", null, null, null));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "didDetermineStateForRegion \ngetId1: " + region.getId1() + "\ngetId2: " + region.getId2() + "\ngetId3: " + region.getId3());
            }
        });
        try {
            //beaconManager.startRangingBeaconsInRegion(new Region("sBeacon", Identifier.parse("c898c9e4-2915-4f2f-a8cc-d3f95bb2e510"), null, null));
            beaconManager.startMonitoringBeaconsInRegion(new Region("sBeacon", null, null, null));
        } catch (RemoteException e) {
            Log.i(TAG, "RemoteException: " + e);
        }
    }

    private void logBeaconData(final boolean enter) {

        try {
            beaconManager.setRangeNotifier(new RangeNotifier() {
                //private int nProximity;
                private int nDetectedBeaconProximity;
                private Collection<Beacon> mBeacon;
                String TagId;


                @Override
                public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                    int beaconSize = beacons.size();
                    this.mBeacon = beacons;
                    Log.e("beaconSize", String.valueOf(beaconSize));


                    if (beacons.size() > 0) {
                        //new AsyncTaskBeaconScan(beacons, getApplicationContext()).execute();
                        //Collections.addAll(sHashSetBeaconPair, beacons);
                        for (Beacon beacon : mBeacon) {
                            startTime = System.nanoTime();
                            Log.e("beacon", "Initialization");
                            timings.dumpToLog();
                            if (beacon.getDistance() < 2.0) {
                                //Immediate
                                nDetectedBeaconProximity = 0;
                            } else if (beacon.getDistance() > 2.0 && beacon.getDistance() < 36.0) {
                                //near
                                nDetectedBeaconProximity = 1;
                            } else {
                                //far
                                nDetectedBeaconProximity = 2;
                            }
                            String getuuid = beacon.getId1().toString();
                            //if (getuuid.equals("53530200-0101-4a41-0108-034d004b014d")) {
                            //getuuid.equals("c898c9e4-2915-4f2f-a8cc-d3f95bb2e510") ||
                            String getmajor;
                            ///////////////////////////////////////////////////////////////////////
                            String sMinorId1 = beacon.getId3().toHexString();
                            String sMinorId2 = sMinorId1.substring(2, 4);
                            sMinorId2 = String.valueOf(Integer.parseInt(sMinorId2, 16));
                            while (sMinorId2.length() < 3)
                                sMinorId2 = "0" + sMinorId2;
                            String sMinorId3 = sMinorId1.substring(4);
                            sMinorId3 = String.valueOf(Integer.parseInt(sMinorId3, 16));
                            while (sMinorId3.length() < 3)
                                sMinorId3 = "0" + sMinorId3;
                            String getminor = sMinorId2 + sMinorId3;
                            //////////////////////////////////////////////////////////////////////////

                            String hex = beacon.getId2().toHexString();
                            String sBatteryStatus = hex.substring(2, 3);
                            String majorNumber = hex.substring(3);
                            int nOriginalMajor = Integer.parseInt(majorNumber, 16);
                            getmajor = String.valueOf(nOriginalMajor);
                            DateFormat df = new SimpleDateFormat("dd/MM/yy\nHH:mm", Locale.getDefault());
                            String dateTime = df.format(Calendar.getInstance().getTime());
                            String[] dateTimeArray = dateTime.split("\n");

                            sMajor = getmajor;
                            if (getminor.substring(0, 1).contains("0")) {
                                sMinor = getminor.substring(2);
                            } else
                                sMinor = getminor;
                            sUUID = getuuid;

                            sDate = dateTimeArray[0];
                            sTime = dateTimeArray[1];
                            sRSSI = String.valueOf(beacon.getRssi());

                            Log.e(TAG, "From SimpleService");
                            Log.e(TAG, " UUID: " + beacon.getId1());
                            Log.e(TAG, " Major: " + getmajor);
                            Log.e(TAG, " Minor: " + sMinor);
                            Log.e(TAG, " RSSI: " + sRSSI);
                            Log.e(TAG, " Power: " + beacon.getTxPower());
                            Log.e(TAG, " Distance: " + beacon.getDistance());
                            Log.e(TAG, " BatteryStatus: " + sBatteryStatus);
                            Log.e(TAG, "End of SimpleService");

                            double distanceOfBeacon = calculateAccuracy(beacon.getTxPower(), beacon.getRssi());
                            Log.e(TAG, "Distance in meters: " + distanceOfBeacon);

                            TagId = sMajor + sMinor;

                            //checks if TagID exists in sqLite
                            boolean isDataPresentInAllTab;
                            int nID = db.getIdForStringTabAll(TagId);
                            isDataPresentInAllTab = nID != -1;

                            /*if TagID is present in SQLite then we will extract lastseen incase if the first
                            condition is not executed.
                            */
                            if(nID>=1) {
                                final ArrayList<DataBaseHelper> dbData1 = new ArrayList<>(db.getAllTabData());
                                sPreviousTime = dbData1.get(nID-1).getLastSeen();
                            }

                            /*checks if data is present in SQLite and also if major is 2901. because 2901 is
                            tagpro events beacons.
                            *Incase if it is not present all the details of beacon is added to temporary ArrayList
                             with TagID as key and Arraylist as value. so here we will save all these key and value
                             inside a temporary LinkedHashMap which will be cleared later once the data is sent to
                             server.
                            *else if permanentLinkedHM do not contain the TagID and sqLite database contains TagID
                             then data of sqLite will be put into ArrayList, all the data of TagID will be added into
                             arraylist including lastseen, then the data is stored into permanentLinkedHashMap.
                             TagID will be Key and arraylist itself will be value. Then the values of permanentLinkedHM
                             will be saved into new Arraylist and then the newly added beacon will be added into
                             alBeaconInfo Arraylist in the form of string separating with comma's.
                             This info is sent to fragmentRecent via Intent to update recyclerview of Recent Tab.
                             lastseen sqLite db will also be updated using update method and the same info will be sent
                             to fragmentAll via Intent to update recyclerview of AllTab
                            *else if data is present in sqLite db and permanentLinkedHM then we need to update the lastseen
                             of both.
                             */
                            if (!isDataPresentInAllTab && sMajor.contentEquals("2901")) {
                                ArrayList<String> alTempLinkedHM = new ArrayList<>();
                                alTempLinkedHM.add(TagId);
                                alTempLinkedHM.add(sMajor);
                                alTempLinkedHM.add(sMinor);
                                alTempLinkedHM.add(sUUID);
                                alTempLinkedHM.add(sRSSI);
                                tempLinkedHM.put(Integer.parseInt(TagId), alTempLinkedHM);
                            } else if (!permanentLinkedHM.containsKey(Integer.parseInt(TagId)) && isDataPresentInAllTab) {
                                final ArrayList<DataBaseHelper> dbData = new ArrayList<>(db.getAllTabData());
                                ArrayList<String> alPermanentLinkedHM = new ArrayList<>();
                                alPermanentLinkedHM.add(dbData.get(nID - 1).getTagID());
                                alPermanentLinkedHM.add(dbData.get(nID - 1).getName());
                                alPermanentLinkedHM.add(dbData.get(nID - 1).getDesignation());
                                alPermanentLinkedHM.add(dbData.get(nID - 1).getPhoneNumber());
                                alPermanentLinkedHM.add(dbData.get(nID - 1).getEmailID());
                                alPermanentLinkedHM.add(dbData.get(nID - 1).getDate());
                                alPermanentLinkedHM.add(dbData.get(nID - 1).getTime());
                                alPermanentLinkedHM.add(sTime);
                                permanentLinkedHM.put(Integer.parseInt(TagId), alPermanentLinkedHM);

                                ArrayList<String> alTemp = new ArrayList<>();
                                ArrayList<ArrayList<String>> alPermanentLHM = new ArrayList<>(permanentLinkedHM.values());
                                ArrayList<String> alBeaconInfoLocal = new ArrayList<>();
                                if (alPermanentLHM.size() >= 1) {
                                    for (int i = 0; i < alPermanentLHM.size(); i++) {
                                        alTemp.addAll(alPermanentLHM.get(i));
                                        alBeaconInfoLocal.add(alTemp.get(0) + "," + alTemp.get(1) + "," + alTemp.get(2) + "," + alTemp.get(3) + ","
                                                + alTemp.get(4) + "," + alTemp.get(5) + "," + alTemp.get(6) + "," + alTemp.get(7));
                                        alTemp.clear();
                                    }
                                    Intent recyclerIntent = new Intent("android.intent.action.DataBaseHelper.RECYCLER_NOTIFY");
                                    recyclerIntent.putStringArrayListExtra("alBeaconInfo", alBeaconInfoLocal);
                                    getApplicationContext().sendBroadcast(recyclerIntent);
                                }

                                db.updateSingleDataListAllTab(new DataBaseHelper(sTime, nID), nID);

                                //update recyclerview's position with lastseen without refreshing whole recyclerview
                                if (db.getNameFromAllTab(nID) != null) {
                                    addLatestTime(nID);
                                }
                                //alBeaconInfo.clear();
                            } else if (db.getNameFromAllTab(nID) != null && isDataPresentInAllTab && !sPreviousTime.contentEquals(sTime)) {

                                db.updateSingleDataListAllTab(new DataBaseHelper(sTime, nID), nID);
                                addLatestTime(nID);

                                /*
                                removed the 7th index of permanentLinkedHM to add latest lastseen time
                                 */
                                if (permanentLinkedHM.size() >= 1) {
                                    permanentLinkedHM.get(Integer.parseInt(TagId)).remove(7);
                                    permanentLinkedHM.get(Integer.parseInt(TagId)).add(7, sTime);
                                    ArrayList<ArrayList<String>> alTempLinkedHM = new ArrayList<>(permanentLinkedHM.values());
                                    ArrayList<String> alTemp = new ArrayList<>();
                                    ArrayList<String> alBeaconInfoLocal = new ArrayList<>();
                                    int indexOfTagId;

                                    /*
                                    below for loop will add all the data present from permanentLinkedHM to alBeaconInfoLocal.
                                     */
                                    if (permanentLinkedHM.size() >= 1) {
                                        for (int i = 0; i < permanentLinkedHM.size(); i++) {
                                            alTemp.addAll(alTempLinkedHM.get(i));
                                            alBeaconInfoLocal.add(alTemp.get(0) + "," + alTemp.get(1) + "," + alTemp.get(2) + "," + alTemp.get(3) + ","
                                                    + alTemp.get(4) + "," + alTemp.get(5) + "," + alTemp.get(6) + "," + alTemp.get(7));
                                            alTemp.clear();
                                        }
                                    }

                                    /*
                                    below code will get the position of the TagID which is present in permanentHM to update
                                    the particular view of recyclerview with lastseen
                                     */
                                    for (int i = 0; i < alTempLinkedHM.size(); i++) {
                                        alTemp.addAll(alTempLinkedHM.get(i));
                                        if (alTemp.get(i).contentEquals(TagId)) {
                                            indexOfTagId = i;
                                            Intent recyclerIntent = new Intent("android.intent.action.DataBaseHelper.UPDATETIME");
                                            recyclerIntent.putStringArrayListExtra("alBeaconInfo", alBeaconInfoLocal);
                                            recyclerIntent.putExtra("LastSeen", sTime);
                                            recyclerIntent.putExtra("Index", indexOfTagId);
                                            getApplicationContext().sendBroadcast(recyclerIntent);
                                            break;
                                        }
                                    }
                                }
                            }
                        }//End of for loop

                        /*
                        below handler executes asyncTask based on few conditions.
                        * if internet is present and isOnline.
                        * handler is used here because there is UI operations that needs to be executed after asyncTask is
                          Completed.
                        * AsyncTask is executed only when it is null and TempLinkedHM size is >= 0, and if asyncTask is executed
                          all the data of tempLinkedHM is cleared to save only those beacons which are not detected before.
                        * Incase if AsyncTask is Running it wont be null and if there is a beacon detected it must be saved
                          somewhere until the AsyncTask is completed. so we will store that in tempLinkedHM so that it is not
                          missed and the data is not lost. so whenever beacon collection forloop is executed, whatever data of
                          beacon added in this else statement will be sent to the server.
                         */

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<ArrayList<String>> alTempLinkedHM = new ArrayList<>(tempLinkedHM.values());
                                if (alTempLinkedHM.size() != 0 && networkState.isConnectingToInternet(getApplicationContext()) && networkState.isOnline()) {
                                    if (mTagProAsyncTask == null) {
                                        sSessionID = sharedPreferences.getString("sessionID", null);
                                        mTagProAsyncTask = new TagProAsyncTask(getApplicationContext(), sSessionID, sPreferenceClass, alTempLinkedHM, sTime, sDate, serviceInterface);
                                        mTagProAsyncTask.execute("4", sGETTAGS);
                                        tempLinkedHM.clear();
                                    } else {
                                        ArrayList<String> alTemp = new ArrayList<>();
                                        for (int i = 0; i < alTempLinkedHM.size(); i++) {
                                            alTemp.addAll(alTempLinkedHM.get(i));
                                            tempLinkedHM.put(Integer.parseInt(alTemp.get(0)), alTempLinkedHM.get(i));
                                        }
                                    }
                                }

                                //below code is to check with timer for the total time consumed to execute this method
                                long endTime = System.nanoTime();
                                long nsToMs = (endTime - startTime) / 1000000;
                                double seconds = nsToMs / 1000.0;
                                Log.e("for loop time", String.valueOf(nsToMs));
                                Log.e("for loop time", String.valueOf(seconds));
                            }

                        });


                        /*
                        data will be only fetched from server if we have a new data in it.
                        alTemBeaconInfo contains only the info that is not been executed before from the above conditions.
                         */
                        /*if (alTempBeaconInfo.size() >= 1) {
                            //ArrayList<DataBaseHelper> dbData = new ArrayList<>(db.getAllTabData());
                            int nID = db.getIdForStringTabAll(TagId);
                            if (nID <= 0)
                                getDataFromServer(alTempBeaconInfo);
                        }*/
                        //below code is to check with timer for the total time consumed to execute this method
                        /*long endTime = System.nanoTime();

                        long nsToMs = (endTime-startTime)/ 1000000;
                        double seconds = nsToMs / 1000.0;

                        Log.e("for loop time", String.valueOf(nsToMs));
                        Log.e("for loop time", String.valueOf(seconds));*/
                    }
                }

            });

            try {
                beaconManager.startRangingBeaconsInRegion(new Region("sBeacon", null, null, null));
                Log.i(TAG, "*** startRangingBeaconsInRegion ***");
            } catch (RemoteException e) {
                Log.i(TAG, "RemoteException: " + e);
            }
        } catch (Exception e) {
            //in case if we get any exception service is restarted to continue the work
            Intent service = new Intent(getApplicationContext(), SimpleService.class);
            startService(service);
        }
    }

    /*
    gets data from server only if new data is added
     */
   /* public void getDataFromServer(ArrayList<String> alTempBeaconInfo) {
        sSessionID = sharedPreferences.getString("sessionID", null);
        TagProAsyncTask mTagProAsyncTask = null;

        if (networkState.isConnectingToInternet(getApplicationContext()) && networkState.isOnline()) {
            mTagProAsyncTask = new TagProAsyncTask(getApplicationContext(), sSessionID, sPreferenceClass, alTempBeaconInfo, sTime, sDate, serviceInterface);
            mTagProAsyncTask.execute("4", sGETTAGS);
        }
        //alTempBeaconInfo is cleared so that we request server for the data only when new beacon is detected.
        try {
            if (mTagProAsyncTask != null && mTagProAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                alTempBeaconInfo.clear();
                //notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //checking the time taken to execute whole task
        long endTime = System.nanoTime();
        long nsToMs = (endTime - startTime) / 1000000;
        double seconds = nsToMs / 1000.0;
        Log.e("for loop time", String.valueOf(nsToMs));
        Log.e("for loop time", String.valueOf(seconds));
    }*/

    //updates position of AllTab recyclerview
    public void addLatestTime(int position) {
        Intent recyclerIntent = new Intent("android.intent.action.UPDATETIME");
        recyclerIntent.putExtra("Index", position);
        //recyclerIntent.putStringArrayListExtra("alBeaconInfo", alBeaconInfo);
        getApplicationContext().sendBroadcast(recyclerIntent);
    }

    /*
    this is called only from RecentTabRecyclerViewAdapter to delete the index of arraylist once user deletes them.
    once user selects and deletes we have to remove those from sHashset and alBeaconInfo & alTagId arraylist
     so that it wont repeat or it will execute next time when the beacon is detected again.
     alTagID holds the major & minor that needs to be deleted from the sHashSetTemp.
     */
    public void deleteFromRecent(ArrayList<Integer> alTagIDToRemove) {
        for (int i = 0; i < alTagIDToRemove.size(); i++) {
            permanentLinkedHM.remove(alTagIDToRemove.get(i));
        }
    }

    /*
     * This Override method of ServiceInterface is called from TagproAsyncTask to perform certain tasks in service class.
     * case "UPDATE_ARRAYLIST" is called when data is received from server and the data is already been updated to sqLite db.
       so now we need to add the same data to permanentLinkedHM and fragmentRecent recyclerview.
       code explanation:
       * first of all new ArrayList of Arraylist String is created to store all the received data from callback.
       * alTempBeaconInfo now contains all the data that needs to be updated to permanentLinkedHM and recyclerview.
       * Since the data is in the form of ArrayList of ArrayList string (ArrayList<ArrayList<String>>), each of
         alTempBeaconInfo data needs to be extracted from forloop and each index should be extracted to another
         simple ArrayList String.
       * Next all the data from sqLite db is loaded into ArrayList to extract the data of all newly added data into
         permanentLinkedHM and recyclerview using TagID.
       * Next using the position of TagID present in the db data is picked and saved into alPermanentLinkedHM arraylist.
       * Then all the data from alPermanentLinkedHM is put into permanentLinkedHM using TagID as key and alPermanentLinkedHM
         as value.
       * alTemp is cleared every time forloop is executed or if it comes out of forloop so that duplicate data is not stored
         in permanentLinkedHM.
       * Now all the data of permanentLinkedHM is extracted into alBeaconInfoLocal in form of String and sent via Intent to
         FragmentRecent which updates the recyclerview.

     * case "RELEASE_ASYNCTASK" is called whenever socket time out exception arrives. so that tagproAsyncTask is set to null.
       * AsyncTask runs only when TagproAsyncTask is set to null so that it doesn't interrupt running AsyncTask.
     */
    @Override
    public void onServiceCallBack(String TAG, final Object data, boolean isCalled, final ArrayList<ArrayList<String>> arrayList) {
        switch (TAG) {
            case "UPDATE_ARRAYLIST":
                mTagProAsyncTask = null;
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<ArrayList<String>> alTempBeaconInfo = new ArrayList<>();
                        alTempBeaconInfo.addAll(arrayList);
                        //ArrayList<String> alBeaconInfo = new ArrayList<>();
                        if (alTempBeaconInfo.size() >= 1) {
                            ArrayList<String> alTemp = new ArrayList<>();
                            ArrayList<DataBaseHelper> dataBaseHelperAL = new ArrayList<>(db.getAllTabData());
                            for (int i = 0; i < alTempBeaconInfo.size(); i++) {
                                alTemp.addAll(alTempBeaconInfo.get(i));
                                int nID = db.getIdForStringTabAll(alTemp.get(0));
                                if (nID >= 1) {
                                    if (dataBaseHelperAL.get(nID - 1).getName() != null || !dataBaseHelperAL.get(nID - 1).getName().contains("")) {
                                        ArrayList<String> alPermanentLinkedHM = new ArrayList<>();
                                        alPermanentLinkedHM.add(dataBaseHelperAL.get(nID - 1).getTagID());
                                        alPermanentLinkedHM.add(dataBaseHelperAL.get(nID - 1).getName());
                                        alPermanentLinkedHM.add(dataBaseHelperAL.get(nID - 1).getDesignation());
                                        alPermanentLinkedHM.add(dataBaseHelperAL.get(nID - 1).getPhoneNumber());
                                        alPermanentLinkedHM.add(dataBaseHelperAL.get(nID - 1).getEmailID());
                                        alPermanentLinkedHM.add(dataBaseHelperAL.get(nID - 1).getDate());
                                        alPermanentLinkedHM.add(dataBaseHelperAL.get(nID - 1).getTime());
                                        alPermanentLinkedHM.add(dataBaseHelperAL.get(nID - 1).getTime());
                                        permanentLinkedHM.put(Integer.parseInt(alTemp.get(0)), alPermanentLinkedHM);
                                        alTemp.clear();
                                    }
                                }
                                alTemp.clear();
                            }
                            if (permanentLinkedHM.size() >= 1) {
                                ArrayList<ArrayList<String>> alPermanentLinkedHM = new ArrayList<>(permanentLinkedHM.values());
                                ArrayList<String> alBeaconInfoLocal = new ArrayList<>();
                                for (int i = 0; i < alPermanentLinkedHM.size(); i++) {
                                    alTemp.addAll(alPermanentLinkedHM.get(i));
                                    alBeaconInfoLocal.add(alTemp.get(0) + "," + alTemp.get(1) + "," + alTemp.get(2) + "," + alTemp.get(3) + ","
                                            + alTemp.get(4) + "," + alTemp.get(5) + "," + alTemp.get(6) + "," + alTemp.get(7));
                                    alTemp.clear();
                                }
                                Intent recyclerIntent = new Intent("android.intent.action.DataBaseHelper.RECYCLER_NOTIFY");
                                recyclerIntent.putStringArrayListExtra("alBeaconInfo", alBeaconInfoLocal);
                                getApplicationContext().sendBroadcast(recyclerIntent);
                            }
                            //alBeaconInfo.clear();
                            alTemp.clear();
                        }
                    }
                });
                break;
            case "RELEASE_ASYNCTASK":
                mTagProAsyncTask = null;
                break;
        }
    }

    @Override
    public void onScanningDialogCallBack(String TAG, Object data, boolean isCalled) {
        switch (TAG) {
            case "SCANNING_DIALOG_ASCENDING":
                new Handler().post(new Runnable() {
                    ArrayList<ArrayList<String>> alPermanentLHM = new ArrayList<>(permanentLinkedHM.values());
                    ArrayList<String> alTemp = new ArrayList<>();
                    ArrayList<String> alBeaconInfo = new ArrayList<>();
                    Intent recyclerIntent;

                    @Override
                    public void run() {
                        if (alPermanentLHM.size() >= 1) {
                            Collections.sort(alPermanentLHM, new Comparator<ArrayList<String>>() {

                                @Override
                                public int compare(ArrayList<String> lhs, ArrayList<String> rhs) {
                                    String valA = "";
                                    String valB = "";
                                    try {
                                        valA = lhs.get(6);
                                        valB = rhs.get(6);
                                    } catch (Exception e) {
                                        //do something
                                    }
                                    return valA.compareTo(valB);
                                }
                            });

                            permanentLinkedHM.clear();
                            for (int i = 0; i < alPermanentLHM.size(); i++) {
                                alTemp.addAll(alPermanentLHM.get(i));
                                int nTagID = Integer.parseInt(alTemp.get(0));
                                permanentLinkedHM.put(nTagID, alPermanentLHM.get(i));
                                alBeaconInfo.add(alTemp.get(0) + "," + alTemp.get(1) + "," + alTemp.get(2) + "," + alTemp.get(3) + ","
                                        + alTemp.get(4) + "," + alTemp.get(5) + "," + alTemp.get(6) + "," + alTemp.get(7));
                                alTemp.clear();
                            }

                            recyclerIntent = new Intent("android.intent.action.DataBaseHelper.RECYCLER_NOTIFY");
                            recyclerIntent.putStringArrayListExtra("alBeaconInfo", alBeaconInfo);
                            getApplicationContext().sendBroadcast(recyclerIntent);
                            alBeaconInfo.clear();
                        }
                    }

                });
                break;

            case "SCANNING_DIALOG_DESCENDING":
                new Handler().post(new Runnable() {
                    ArrayList<ArrayList<String>> alPermanentLHM = new ArrayList<>(permanentLinkedHM.values());
                    ArrayList<String> alTemp = new ArrayList<>();
                    ArrayList<String> alBeaconInfo = new ArrayList<>();
                    Intent recyclerIntent;

                    @Override
                    public void run() {
                        if (alPermanentLHM.size() >= 1) {
                            Collections.sort(alPermanentLHM, new Comparator<ArrayList<String>>() {

                                @Override
                                public int compare(ArrayList<String> lhs, ArrayList<String> rhs) {
                                    String valA = "";
                                    String valB = "";
                                    try {
                                        valA = lhs.get(6);
                                        valB = rhs.get(6);
                                    } catch (Exception e) {
                                        //do something
                                    }
                                    return valB.compareTo(valA);
                                }
                            });

                            permanentLinkedHM.clear();
                            for (int i = 0; i < alPermanentLHM.size(); i++) {
                                alTemp.addAll(alPermanentLHM.get(i));
                                int nTagID = Integer.parseInt(alTemp.get(0));
                                permanentLinkedHM.put(nTagID, alPermanentLHM.get(i));
                                alBeaconInfo.add(alTemp.get(0) + "," + alTemp.get(1) + "," + alTemp.get(2) + "," + alTemp.get(3) + ","
                                        + alTemp.get(4) + "," + alTemp.get(5) + "," + alTemp.get(6) + "," + alTemp.get(7));
                                alTemp.clear();
                            }

                            recyclerIntent = new Intent("android.intent.action.DataBaseHelper.RECYCLER_NOTIFY");
                            recyclerIntent.putStringArrayListExtra("alBeaconInfo", alBeaconInfo);
                            getApplicationContext().sendBroadcast(recyclerIntent);
                            alBeaconInfo.clear();
                        }
                    }
                });
                break;

        }
    }

    //Calculates distance of beacon with accuracy
    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10 / 2.5);
        } else {
            return (0.89976) * Math.pow(ratio, 7.7095 / 2.5) + 0.111;
        }
    }
}