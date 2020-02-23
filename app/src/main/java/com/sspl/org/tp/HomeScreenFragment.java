package com.sspl.org.tp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Stack;

import app_utility.OnFragmentInteractionListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link app_utility.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeScreenFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeScreenFragment extends Fragment implements OnFragmentInteractionListener {

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    int index = 0;

    //creating views to hold the view of textView of tablayout
    View sessionsTab;
    View boothsTab;
    View productsTab;
    View speakersTab;

    View navigationDrawerView;
    NavigationView navigationView;

    IntentFilter backStackIF;
    BroadcastReceiver mBroadCastReceiver;

    Stack<Integer> pageHistory;
    int currentPage;
    boolean saveToHistory;

    private OnFragmentInteractionListener mListener;

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * //@return A new instance of fragment HomeScreenFragment.
     */

   /* public static HomeScreenFragment newInstance(String param1, String param2) {
        HomeScreenFragment fragment = new HomeScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backStackIF = new IntentFilter("android.intent.action.BACKSTACK");
        mBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                index = intent.getExtras().getInt("tabIndex");

                if (index >= 0) {
                    selectPage(index);
                }
            }
        };
        getActivity().registerReceiver(mBroadCastReceiver, backStackIF);
    }

    @Override
    public void onDestroy() {
        /*
        unregistering receivers to avoid memory leak
         */
        if (mBroadCastReceiver != null) {
            getActivity().unregisterReceiver(mBroadCastReceiver);
            mBroadCastReceiver = null;
        }

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        registering back receivers to start receiving broadcast
         */
        getActivity().registerReceiver(mBroadCastReceiver, backStackIF);
        getActivity().registerReceiver(mBroadCastReceiver, backStackIF);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        // Inflate the layout for this fragment

        //creates shadow effect under appbar layout below lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View viewShadow = view.findViewById(R.id.view_shadow);
            viewShadow.setVisibility(View.VISIBLE);
        }

        if (getArguments() != null) {
            index = getArguments().getInt("index");
        }

        pageHistory = new Stack<>();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_container);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (saveToHistory)
                    pageHistory.push(currentPage);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        saveToHistory = true;

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /*
        will help to leave a gap between each tabs or gap in the beginning of tab.
         */
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
            drawable.setSize(60, 5);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Sessions"));
        tabLayout.addTab(tabLayout.newTab().setText("Booths"));
        tabLayout.addTab(tabLayout.newTab().setText("Products"));
        tabLayout.addTab(tabLayout.newTab().setText("Speakers"));

        //sets the size of textview of tablayout same
        //tabLayout.setTabGravity(TabLayout.MODE_FIXED);

        sessionsTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        boothsTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
        productsTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2);
        speakersTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(3);

        //listener
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override

            public void onTabSelected(TabLayout.Tab tab) {

                HomeScreen homeScreen = (HomeScreen) getActivity();
                if (homeScreen != null && !homeScreen.isNavHomeChecked) {
                    mViewPager.setCurrentItem(tab.getPosition());

                    int selectedTabPosition = tab.getPosition(); //tab position

                    //firs tab and second tab contains view of tabLayout tabs, which can be used to set drawables.
                    View firstTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
                    View secondTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
                    View thirdTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2);
                    View fourthTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(3);
                    if (container != null) {
                        navigationDrawerView = container.getRootView();
                        navigationView = (NavigationView) navigationDrawerView.findViewById(R.id.nav_view);
                    }
                    if (navigationView != null) {
                        switch (selectedTabPosition) {
                            case 0:
                                if (!navigationView.isSelected()) {
                                    navigationView.setCheckedItem(R.id.nav_session);
                                }
                                firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_selected, null));
                                secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                thirdTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                fourthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                        /*int delay = (150) + 750; //this is starting delay
                        ViewGroup vgTab = (ViewGroup)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
                        vgTab.setScaleX(0f);
                        vgTab.setScaleY(0f);

                        vgTab.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setStartDelay(delay)
                                .setInterpolator(new FastOutSlowInInterpolator())
                                .setDuration(450)
                                .start();*/
                                //Toast.makeText(ScanningActivity.this, "1st tab", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                if (!navigationView.isSelected()) {
                                    navigationView.setCheckedItem(R.id.nav_booths);
                                }
                                firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_selected, null));
                                thirdTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                fourthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                break;
                            case 2:
                                if (!navigationView.isSelected()) {
                                    navigationView.setCheckedItem(R.id.nav_products);
                                }
                                firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                thirdTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_selected, null));
                                fourthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                break;
                            case 3:
                                if (!navigationView.isSelected()) {
                                    navigationView.setCheckedItem(R.id.nav_speakers);
                                }
                                firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                thirdTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                                fourthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_selected, null));
                                break;
                        }
                    }
                    super.onTabSelected(tab);
                }
            }
        });

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), 4);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //call index of the tab depending upon what user clicks on home screen.(index is sent via arguments)
        if (index > 0) {
            selectPage(index);
        }

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

     /*
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */

    @Override
    public void onFragmentMessage(String TAG, Object data, boolean isNavHomeChecked) {

    }

    void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, true);
        mViewPager.setCurrentItem(pageIndex);
    }


   /* public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            return true;
        }

        return true;

    }*/

    /*
    handleBackPressed handles complete back press that is triggered from HomeScreen Activity.
    this method calls onBackPressed based on the condition.
     */
    public static boolean handleBackPressed(FragmentManager fm) {
        if (fm.getFragments() != null) {
            for (Fragment frag : fm.getFragments()) {
                if (frag != null && frag.isVisible() && frag instanceof HomeScreenFragment) {
                    if (((HomeScreenFragment) frag).onBackPressed()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean onBackPressed() {
        FragmentManager fm = getChildFragmentManager();
        if (handleBackPressed(fm)) {
            return true;
        } else if (getUserVisibleHint() && fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return true;
        }
        return false;
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
