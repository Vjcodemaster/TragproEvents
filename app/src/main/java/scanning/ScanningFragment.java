package scanning;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sspl.org.tp.R;

import app_utility.OnFragmentInteractionListener;
import app_utility.SharedPreferencesClass;
import receivers.MemoryMaster;


public class ScanningFragment extends Fragment {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private OnFragmentInteractionListener mListener;

    SharedPreferencesClass sPreferenceClass;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    DatabaseHandler db;

    //creating views to hold the view of textView of tablayout
    View firstTab;
    View secondTab;

    MemoryMaster mMemoryMaster;

    boolean isFromBluetoothActivityResult = false;          //handles onStart onBackPress for first time

    String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION};

    //protected static final int REQUEST_CHECK_SETTINGS = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sPreferenceClass = new SharedPreferencesClass(getActivity());
        db = new DatabaseHandler(getActivity().getApplicationContext());

        mMemoryMaster = new MemoryMaster();
        getActivity().registerComponentCallbacks(mMemoryMaster);
        /*setContentView(R.layout.fragment_scanning);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanning, container, false);

        /*RecyclerView recyclerView = (RecyclerView) view.findViewWithTag("recentTab_recyclerView");
        recyclerView.getAdapter().notifyDataSetChanged();*/
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container);

        //creates shadow effect under appbar layout below lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View viewShadow = view.findViewById(R.id.view_scanning_shadow);
            viewShadow.setVisibility(View.VISIBLE);
        }
        /*
        setting px as margin will insert separator between viewpager fragments. works
         */
        int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        mViewPager.setPageMargin(px);
        mViewPager.setPageMarginDrawable(R.color.whiteSmoke);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("RECENT"));
        tabLayout.addTab(tabLayout.newTab().setText("ALL"));

        //sets the size of textview of tablayout same
        tabLayout.setTabGravity(TabLayout.MODE_FIXED);
        //tabLayout.getChildAt(0).setBackgroundResource(R.drawable.tab_left_selected);
        //GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.tab_left_selected);
        //tabLayout.setTabGravity(View.TEXT_ALIGNMENT_CENTER);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setTag("fab_mainActivity");
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*
                switch(view.getId()){
                    case R.id.fab:
                        if(mViewPager.getCurrentItem()==0){
                            View child = mViewPager.getFocusedChild().findViewWithTag("recycler_view_recent");
                            child.setBackgroundColor(ContextCompat.getColor(ScanningFragment.this, R.color.blue_gray));
                            Toast.makeText(ScanningFragment.this,"First",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ScanningFragment.this,"Second",Toast.LENGTH_SHORT).show();
                        }
                }
            }
        });*/

        firstTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        secondTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);

        //listener
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override

            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

                //tab position
                int selectedTabPosition = tab.getPosition();

                //firs tab and second tab contains view of tabLayout tabs, which can be used to set drawables.
                View firstTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
                View secondTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
                switch (selectedTabPosition) {
                    case 0:
                        firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.tab_left_selected, null));
                        secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.tab_right_unselected, null));
                        //Toast.makeText(ScanningFragment.this, "1st tab", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.tab_left_unselected, null));
                        secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.tab_right_selected, null));
                        //Toast.makeText(ScanningFragment.this, "2nd tab", Toast.LENGTH_SHORT).show();
                        break;
                }
                super.onTabSelected(tab);
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());

        mViewPager.setAdapter(mSectionsPagerAdapter);


        /*final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                mViewPager.setAdapter(mSectionsPagerAdapter);
                //call function
                ha.postDelayed(this, 5000);
            }
        }, 5000);*/


        //enableBluetooth();

        /*Intent in = new Intent(getActivity(), SimpleService.class);
        getActivity().startService(in);*/
        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        *//*
        result code is set by android if result is success switch case is executed.
        so if bluetooth is switched on the result will be result ok. that means we need to start service.
       else
       start alert dialog which takes user to settings screen to switch on bluetooth.
         *//*
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 2:
                    Intent in = new Intent(getActivity(), SimpleService.class);
                    getActivity().startService(in);
                    break;
                case 3:
                    enableBluetooth();
                    break;
            }
        else {
            isFromBluetoothActivityResult = true;
            @SuppressWarnings("unused") AlertDialogs alertDialogs = new AlertDialogs(getActivity(), 0);
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();
        getActivity().stopService(new Intent(getActivity(), SimpleService.class));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().stopService(new Intent(getActivity(), SimpleService.class));
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
        } else {
            mListener.onFragmentMessage("REQUEST_LOCATION", getActivity().getString(R.string.app_name), false);
        }
        /*
        below bluetooth code is written on onStart because when we redirect user to bluetooth settings app goes to background, when
        user is done with the settings user will press back and comeback to app. now App state changes from background to foreground
        state and that can be only retrieved onStart. So here again we will check whether user has given permission or not. if user
        has granted permission then we will start the service else redirect to home screen. in case if service is already running
        we do not start it again. we will ignore.
        isFromBluetoothActivityResult - will execute onBackPressed only if it is from onActivityResult.
         */
        /*final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && !isMyServiceRunning(scanning.SimpleService.class)) {
            Intent in = new Intent(getActivity(), SimpleService.class);
            getActivity().startService(in);
            Log.e("Service", "Service is started");
        } else if (isMyServiceRunning(scanning.SimpleService.class)) {
            Log.e("Service", "Service is already running");
        } else {
            if (isFromBluetoothActivityResult)
                getActivity().onBackPressed();
            Log.e("bluetooth", "bluetooth access denied");
        }

        mListener.onFragmentMessage("REQUEST_LOCATION", getString(R.string.app_name), false);
        //requestLocationPermission();
        isFromBluetoothActivityResult = false;*/
        //Log.e("foreground", "App is in foreground");
    }

    /*@Override
    public void onResume(){
        super.onResume();
        if (!hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
        } else {
            mListener.onFragmentMessage("REQUEST_LOCATION", getActivity().getString(R.string.app_name), false);
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener.onFragmentMessage("SORT_SCAN_VALUE", 0, false);
        //db.clearDatabase("DataBaseHelper");
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        OnFragmentInteractionListener is  used for communication between activity or fragments.
        OnFragmentInteractionListener is a interface implemented from utility package which is custom.
         */
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }
}
