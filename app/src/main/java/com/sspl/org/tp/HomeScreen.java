package com.sspl.org.tp;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.HashSet;

import app_utility.CircularProgressBar;
import app_utility.OnFragmentInteractionListener;
import app_utility.SharedPreferencesClass;
import app_utility.StaticReferenceClass;
import dialogs.AlertDialogs;
import dialogs.FilterPopupWindow;
import dialogs.SortPopupWindow;
import dialogs.SortScanTimePopupWindow;
import library.ZoomOutPageTransformer;
import messaging.InboxFragment;
import scanning.ScanningFragment;
import scanning.SimpleService;


public class HomeScreen extends AppCompatActivity implements OnFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    ImageButton ibBackArrow, ibSearchBackArrow;

    View popupView;
    int nUserDisplayWidth;
    int nUserDisplayHeight;
    int[] nOffSetLocation;
    int nDisplayDDXOffSet; //display drop down x off set
    int nDisplayOffSetD3;

    private boolean isPageAnimationCleared = false;
    private boolean isPageAnimationReset = false;
    private int nSharedElementFlag = 0;
    private boolean isHomeScreenFragmentLoaded = false;

    private boolean isSearchOpened = false; //boolean to check whether user already clicked on search of toolbar
    private EditText etSearch;  //when user clicks search, user will be able to type into editBox

    Button btnHomeReadMore;

    Animation animZoomIn, animZoomOut;

    int nCount = 10;

    int nSortTimeRadioButtonFlag = 0;

    int nSortRadioButtonFlag = 0;

    private ViewPager mViewPagerSlideShow;

    View vPage;

    int nTabIndex = 0;

    /*
    *isNavHomeChecked checks if user has already in homescreen or not.
    *isReviewSelected checks if user has clicked on Review button of homescreen,
      then no need to execute other fragment transaction.
     */
    public boolean isNavHomeChecked = false, isReviewSelected = false; //isOtherOptionsSelected = false;
    HashSet<Integer> nHashSetTabIndex;
    Dialog dialog;

    View viewActionBar;
    TextView textviewTitle;
    Toolbar toolbar;

    Menu menu;
    ImageView[] imageViews;

    int[] mResources = {
            R.drawable.imageslide,
            R.drawable.imageslide1,
            R.drawable.imageslide2,
    };

    int nCurrentPage = 0;
    int nCheck;


    //boolean isFromBluetoothActivityResult = false;          //handles onStart onBackPress for first time

    protected static final int REQUEST_CHECK_SETTINGS = 3;

    OnFragmentInteractionListener mListener;

    //final Handler delayedHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_screen);

        SharedPreferences sharedPreferences;
        sharedPreferences = HomeScreen.this.getSharedPreferences(StaticReferenceClass.sAPP_PREFERENCES, Context.MODE_PRIVATE); //1

        final int userType = sharedPreferences.getInt(HomeScreen.this.getResources().getString(R.string.user_type), 0);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        /*
         below menu code is used to find drawer_view items and set title to null.
         The reason why we are setting the value to null is because there is a custom logout design that we can
         only achieve using custom textview xml drawer_logout. if item of drawer_view is not set to null,
         we cannot align the drawer_logout(custom textview) as we want or as per the design.
         (this is bit hacky as we haven't find any way to set custom textview directly into drawer_view
         and drawer_view item gave error when we left the title blank in drawer_view xml. so decided to do this)
         */
        final Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_logout).setTitle(null);

        final CircularProgressBar circularProgressBar = new CircularProgressBar(HomeScreen.this);
        circularProgressBar.setCanceledOnTouchOutside(false);
        circularProgressBar.setCancelable(false);
        circularProgressBar.show();

        btnHomeReadMore = (Button) findViewById(R.id.btn_home_readmore);
        initReadMoreDialog();

        mListener = HomeScreen.this;

        LinearLayout llViewPager = (LinearLayout) findViewById(R.id.ll_viewpager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mViewPagerSlideShow = (ViewPager) findViewById(R.id.viewpager_slideshow);
        mViewPagerSlideShow.setOffscreenPageLimit(mResources.length - 1);

        animZoomIn = AnimationUtils.loadAnimation(HomeScreen.this, R.anim.zoom_in);
        animZoomOut = AnimationUtils.loadAnimation(HomeScreen.this, R.anim.zoom_out);

        /*
        below setCustomLogout is a method which finds and add the logout text to drawer group_logout.
        This works same like messageCounter that is set to Message Inbox option.
        parameters required = (id of drawer item, text, navigationView reference)
         */
        setCustomLogout(R.id.nav_logout, "Logout", navigationView);

        /*
        below navHeader is used as view because this is how we can get the nav_header view from NavigationView
        This code doesn't work if app:headerLayout="@layout/nav_header" is not used in activity of drawer layout.
        Reference of nav_header can only be found with below code
         */
        View navHeader = navigationView.getHeaderView(0);

        ibBackArrow = (ImageButton) navHeader.findViewById(R.id.image_button_back_arrow);

        /*
        below code animates the toolbar when menu is clicked on actionbar. arrow to menu and vise versa
         */
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        viewActionBar = View.inflate(HomeScreen.this, R.layout.toolbar_textview, null);
        textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        setCustomToolBar();


        /*
        below checkUserFirstTime if statement executes only when user is logged in for the first time and to
        open respective screens depending upon attendee or booth owner.
        checkUserFirstTime checks if user logged in for the first time to the application.
        viewTreeObserver is used here because we need to set menu item into the toolbar once the toolbar
        construction is completed else we will get null pointer exception if we try to setMenuItemVisibility.
        if user is booth owner scanning fragment is loaded else
        if user is attendee then home screen is loaded. all these things is done in handler so that the UI
        doesn't lag.
         */
        if (checkUserFirstTime()) {
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ViewTreeObserver vto = toolbar.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            if (userType == 0) {
                                loadScanningFragment();
                                navigationView.setCheckedItem(R.id.nav_scanning);
                                SharedPreferencesClass sPreferenceClass = new SharedPreferencesClass(HomeScreen.this);
                                sPreferenceClass.setUserFirstTime();
                                textviewTitle.setText(getResources().getString(R.string.nav_scanning));
                                setMenuItemVisibility(3);
                            } else if (userType == 1) {
                                menu.findItem(R.id.nav_scanning).setVisible(false);
                                navigationView.setCheckedItem(R.id.nav_home);
                            } else {
                                navigationView.setCheckedItem(R.id.nav_home);
                            }
                        }
                    });
                }
            });
        }


        /*
        getting number of images and creating bubble as per it.
        sets solid bubble on whichever image is selected.
         */
        imageViews = new ImageView[mResources.length];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(getApplicationContext());
            /*
            height and width of bubble
             */
            LinearLayout.LayoutParams layoutParamsIV = new LinearLayout.LayoutParams(10, 10);
            layoutParamsIV.setMargins(5, 10, 5, 10);
            imageViews[i].setLayoutParams(layoutParamsIV);
            if (i == 0) {
                imageViews[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_solid, null));
            } else {
                imageViews[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_holo, null));
            }
            llViewPager.addView(imageViews[i]);
        }


        final HomeImagePagerAdapter mHomeImagePagerAdapter = new HomeImagePagerAdapter(HomeScreen.this, mResources, mListener,
                circularProgressBar);
        /*
        adapter for Image viewpager.
         */
        mViewPagerSlideShow.setAdapter(mHomeImagePagerAdapter);


        animZoomOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mViewPagerSlideShow.getCurrentItem() == mHomeImagePagerAdapter.getCount() - 1) {
                    mViewPagerSlideShow.setCurrentItem(0);
                    nCurrentPage = 0;
                } else
                    mViewPagerSlideShow.setCurrentItem((mViewPagerSlideShow.getCurrentItem()) + 1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animZoomIn.setAnimationListener(new Animation.AnimationListener()

        {

            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                /*
                this will reverse animation
                 */
                animZoomOut.setDuration(3000);
                mViewPagerSlideShow.getChildAt(mViewPagerSlideShow.getCurrentItem()).startAnimation(animZoomOut);
            }
        });

        if (navigationView != null)

        {
            setupDrawerContent(navigationView);
            /*
            setMenuCounter method is used to set number of unread messages beside message inbox of navigation drawer
            (id where the item is present, number of unread messages, navigation view)
             */
            setMenuCounter(R.id.nav_message_inbox, 5, navigationView);
            navigationView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.darkGray, null));
        }

        ibBackArrow.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });

        btnHomeReadMore.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        mViewPagerSlideShow.setPageTransformer(false, new ViewPager.PageTransformer()

        {
            @Override
            public void transformPage(View page, float position) {
                // do transformation here
                ZoomOutPageTransformer zoomOutPageTransformer = new ZoomOutPageTransformer();
                zoomOutPageTransformer.transformPage(page, position);
                vPage = page;
                /*
                auto animation swipe will work one time for all the images.
                 */
                if (position == 0.0 && nCount <= (mResources.length * 2)) {  // page is settled in center
                    animZoomIn.setDuration(4000);
                    Log.e("page in center", "page in center");
                    //animZoomIn.setInterpolator(new AccelerateInterpolator());
                    if (nSharedElementFlag == 0) {
                        page.startAnimation(animZoomIn);
                    }
                    isPageAnimationCleared = false;
                    isPageAnimationReset = false;
                } else {
                    if (!isPageAnimationCleared) {
                        Log.e("clear animation", "clear animation");
                        page.clearAnimation();
                        isPageAnimationCleared = true;
                    } else if (!animZoomIn.hasEnded() || !animZoomOut.hasEnded()) {

                        page.clearAnimation();
                        if (!isPageAnimationReset) {
                            Log.e("reset animation", "reset animation");
                            animZoomIn.cancel();
                            animZoomIn.reset();
                            animZoomOut.cancel();
                            animZoomOut.reset();
                            isPageAnimationReset = true;
                        }
                    }
                }
            }
        });


        nHashSetTabIndex = new HashSet<>();


        mViewPagerSlideShow.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nCount++;
                Log.e("For loop", "From for loop");
                for (int i = 0; i < mResources.length; i++) {
                    if (i == position) {
                        imageViews[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_solid, null));
                    } else {
                        imageViews[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_holo, null));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TextView tvMarquee = (TextView) findViewById(R.id.tv_marquee);
        tvMarquee.setHorizontallyScrolling(true);
        tvMarquee.setSelected(true);


    }


    /*
    setMenuCounter method needs itemId(i,e nav_message_inbox which is in drawer view's item),
    count(number of unread messages to show), Navigation view's context./
    In below method we will first get navigation view's id and put it into parent layout(LinearLayout),
    then using that linearlayout reference to find textview tv_message_counter we set the text.
    Note:
    LinearLayout is used to align the textview so that it appears at the center of the view of
    Message Inbox End center. Without LinearLayout it would not align properly
     */

    private void setMenuCounter(@IdRes int itemId, int count, NavigationView navigationView) {
        LinearLayout linearLayout = (LinearLayout) navigationView.getMenu().findItem(itemId).getActionView();
        TextView view = (TextView) linearLayout.findViewById(R.id.tv_message_counter);
        view.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        view.setText(count > 0 ? String.valueOf(count) : null);
    }

    /*
    this method works similar to the above setMenuCounter method
     */
    private void setCustomLogout(@IdRes int itemId, String sText, NavigationView navigationView) {
        LinearLayout linearLayout = (LinearLayout) navigationView.getMenu().findItem(itemId).getActionView();
        TextView view = (TextView) linearLayout.findViewById(R.id.tv_logout);
        view.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        view.setText(sText);
    }

    //Navigation drawer listener.
    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    Fragment newFragment = null;
                    FragmentTransaction transaction;
                    Bundle bundle;
                    String sBackStack = "";
                    int nMenuVisibility;
                    boolean isFragmentVisible;
                    String backStateName;

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        bundle = new Bundle();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_session:
                                //sets position of tab to 0th index
                                nTabIndex = 0;
                                bundle.putInt("index", nTabIndex);
                                //back stack name
                                sBackStack = "fragment_sessions";
                                /*checks if the fragment is present in backstack, if its not new fragment is created
                                also check if fragment is in visible state. if its visible state nothing is done.
                                 */
                                if ((newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack)) == null) {
                                    isFragmentVisible = false;
                                    newFragment = new HomeScreenFragment();
                                    newFragment.setArguments(bundle);
                                } else {
                                    newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack);
                                    isFragmentVisible = newFragment.isVisible();
                                }
                                isNavHomeChecked = false;
                                //sets toolbar icon visibility
                                nMenuVisibility = 1;
                                //sets title of toolbar
                                textviewTitle.setText(getResources().getString(R.string.event_name));
                                break;
                            case R.id.nav_booths:
                                nTabIndex = 1;
                                bundle.putInt("index", nTabIndex);
                                sBackStack = "fragment_booths";

                                if ((newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack)) == null) {
                                    isFragmentVisible = false;
                                    newFragment = new HomeScreenFragment();
                                    newFragment.setArguments(bundle);
                                } else {
                                    newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack);
                                    isFragmentVisible = newFragment.isVisible();
                                }
                                isNavHomeChecked = false;
                                nMenuVisibility = 1;
                                textviewTitle.setText(getResources().getString(R.string.event_name));
                                break;
                            case R.id.nav_products:
                                nTabIndex = 2;
                                bundle.putInt("index", nTabIndex);
                                sBackStack = "fragment_products";
                                if ((newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack)) == null) {
                                    isFragmentVisible = false;
                                    newFragment = new HomeScreenFragment();
                                    newFragment.setArguments(bundle);
                                } else {
                                    newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack);
                                    isFragmentVisible = newFragment.isVisible();
                                }
                                isNavHomeChecked = false;
                                nMenuVisibility = 1;
                                textviewTitle.setText(getResources().getString(R.string.event_name));
                                break;
                            case R.id.nav_speakers:
                                nTabIndex = 3;
                                bundle.putInt("index", nTabIndex);
                                sBackStack = "fragment_speakers";
                                if ((newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack)) == null) {
                                    isFragmentVisible = false;
                                    newFragment = new HomeScreenFragment();
                                    newFragment.setArguments(bundle);
                                } else {
                                    newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack);
                                    isFragmentVisible = newFragment.isVisible();
                                }
                                isNavHomeChecked = false;
                                nMenuVisibility = 1;
                                textviewTitle.setText(getResources().getString(R.string.event_name));
                                break;
                            case R.id.nav_message_inbox:
                                nTabIndex = 4;
                                sBackStack = "Inbox";

                                if ((newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack)) == null) {
                                    isFragmentVisible = false;
                                    newFragment = new InboxFragment();
                                    newFragment.setArguments(bundle);
                                } else {
                                    newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack);
                                    isFragmentVisible = newFragment.isVisible();
                                }
                                isNavHomeChecked = false;
                                nMenuVisibility = 2;
                                isHomeScreenFragmentLoaded = false;
                                textviewTitle.setText(getResources().getString(R.string.inbox));
                                break;
                            case R.id.nav_reports:
                                nTabIndex = 5;
                                sBackStack = "Reports";
                                if ((newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack)) == null) {
                                    isFragmentVisible = false;
                                    newFragment = new ReportsFragment();
                                    newFragment.setArguments(bundle);
                                } else {
                                    newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack);
                                    isFragmentVisible = newFragment.isVisible();
                                }
                                isNavHomeChecked = false;
                                nMenuVisibility = 2;
                                isHomeScreenFragmentLoaded = false;
                                textviewTitle.setText(getResources().getString(R.string.reports));
                                break;

                            case R.id.nav_scanning:
                                nTabIndex = 6;
                                sBackStack = "Scanning";
                                if ((newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack)) == null) {
                                    isFragmentVisible = false;
                                    newFragment = new ScanningFragment();
                                    newFragment.setArguments(bundle);
                                } else {
                                    newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack);
                                    isFragmentVisible = newFragment.isVisible();
                                }
                                isNavHomeChecked = false;
                                nMenuVisibility = 3;
                                isHomeScreenFragmentLoaded = false;
                                textviewTitle.setText(getResources().getString(R.string.nav_scanning));
                                break;
                            case R.id.nav_rankings:
                                nTabIndex = 7;
                                sBackStack = "Rankings";
                                if ((newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack)) == null) {
                                    isFragmentVisible = false;
                                    newFragment = new RankingsFragment();
                                    newFragment.setArguments(bundle);
                                } else {
                                    newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack);
                                    isFragmentVisible = newFragment.isVisible();
                                }
                                isNavHomeChecked = false;
                                nMenuVisibility = 2;
                                isHomeScreenFragmentLoaded = false;
                                textviewTitle.setText(getResources().getString(R.string.rankings));
                                break;
                            default:
                                //if non of the cases are executed then home screen is loaded.
                                setMenuItemVisibility(0);
                                FragmentManager fm = getSupportFragmentManager();
                                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                    fm.popBackStack();
                                }
                                isNavHomeChecked = true;
                                textviewTitle.setText(getResources().getString(R.string.home));
                                nCount = 0;
                                isHomeScreenFragmentLoaded = false;
                        }


                        /*delayedHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 150);*/
                        mDrawerLayout.closeDrawers();

                        if (newFragment != null) {
                            backStateName = newFragment.getClass().getName();
                        }

                        if (!isNavHomeChecked) {
                            nCount = 10;
                            if (newFragment != null && !newFragment.isVisible() && !isHomeScreenFragmentLoaded) {
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.anim.left_to_right, R.anim.slide_out_up);
                                transaction.replace(R.id.flContent, newFragment, sBackStack);
                                transaction.addToBackStack(null);

                                if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 1) {
                                    getSupportFragmentManager().popBackStackImmediate();
                                }
                                transaction.commit();
                                nHashSetTabIndex.add(bundle.getInt("index"));
                                isNavHomeChecked = false;
                                setMenuItemVisibility(nMenuVisibility);
                                isHomeScreenFragmentLoaded = backStateName.contentEquals("com.sspl.org.tp.HomeScreenFragment");
                            } else if (nTabIndex <= 3) {
                                Intent in = new Intent("android.intent.action.BACKSTACK");
                                in.putExtra("tabIndex", nTabIndex);
                                sendBroadcast(in);
                            }
                            //isHomeScreenFragmentLoaded = backStateName.contentEquals("com.sspl.org.tp.HomeScreenFragment");
                        }
                        /*checks if home screen is checked & new fragment is not null & fragment is not visible then
                        a new fragment is loaded.
                        else
                        if nav home screen is not checked and fragment is not visible then, fragment is brought back
                        from back stack.
                         */

                        /*if (!isNavHomeChecked && newFragment != null && !isFragmentVisible) {
                            nCount = 10;
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.setCustomAnimations(R.anim.left_to_right, R.anim.slide_out_up);
                            transaction.replace(R.id.flContent, newFragment, sBackStack);
                            transaction.addToBackStack(null);

                            if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 1) {
                                getSupportFragmentManager().popBackStackImmediate();
                            }
                            transaction.commit();
                            nHashSetTabIndex.add(bundle.getInt("index"));
                            isNavHomeChecked = false;
                            setMenuItemVisibility(nMenuVisibility);
                        } else {
                            if (!isNavHomeChecked && !isFragmentVisible) {
                                nCount = 10;
                                newFragment = getSupportFragmentManager().findFragmentByTag(sBackStack);
                                String backStateName = newFragment.getClass().getName();
                                getSupportFragmentManager().popBackStack(backStateName, 1);
                            } else{
                                *//*
                                this case will only execute whenever user clicks on navigation drawer first and then swipes the
                                tabs to any other tabs, then tries to click on navigation drawer options, it won't work. so
                                below code sends intent directly to HomeScreenFragment and changes the tab whichever user has
                                clicked on Navigation drawer.
                                executes only when nav home is not checked.
                                 *//*
                                if (!isNavHomeChecked) {
                                    Intent in = new Intent("android.intent.action.BACKSTACK");
                                    in.putExtra("tabIndex", nTabIndex);
                                    sendBroadcast(in);
                                }
                            }
                        }*/

                        return true;
                    }

                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toobar_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //handles open and close of home button of actionbar/toolbar
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            //triggers only when drawer is not open, if not then handles search functionality of actionbar/toolbar
            case R.id.action_search:
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    handleSearch();
                    return true;
                }
                break;
            case R.id.action_filter:
                whereToPlacePopupWindow("FILTER", findViewById(R.id.action_filter));
                FilterPopupWindow filterPopupWindow = new FilterPopupWindow(HomeScreen.this);
                filterPopupWindow.mPopUpWindowFilter.setOutsideTouchable(true);
                filterPopupWindow.mPopUpWindowFilter.setOutsideTouchable(true);

                /*
                below setFocusable and setBackgroundDrawable helps to close the popup window on click outside the popup window.
                 */
                filterPopupWindow.mPopUpWindowFilter.setFocusable(true);
                filterPopupWindow.mPopUpWindowFilter.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                /*
                showAsDropDown displays the popup window below a view.
                *view1 is a view where popup window is displayed as dropdown menu on the screen.
                *nDisplayDDXOffSet is a x offset from the right side where popup window is displayed.
                 (default location of x is 0, where it is displayed to the end of the screen below view.)
                 this has been overridden by manual calculation which is done by Vijay.
                 Since we move the popup window from right. we need negative values. so adding - to the x off set.
                *location[1]*2 gives the location below the view1. as per requirement it works fine.
                 we need a negative value to set this as near as below view1. so -location[1] is set.
                 *refer method whereToPlacePopupWindow for more details and calculations.
                 */
                filterPopupWindow.mPopUpWindowFilter.showAsDropDown(popupView, -nDisplayDDXOffSet, -nOffSetLocation[1] * 2);
                break;
            case R.id.action_sort:
                SortPopupWindow sortPopupMenu = new SortPopupWindow(HomeScreen.this, nSortRadioButtonFlag);
                sortPopupMenu.mPopupWindowSort.setOutsideTouchable(true);
                sortPopupMenu.mPopupWindowSort.setOutsideTouchable(true);
                sortPopupMenu.mPopupWindowSort.setFocusable(true);
                sortPopupMenu.mPopupWindowSort.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                whereToPlacePopupWindow("SORT", findViewById(R.id.action_sort));
                sortPopupMenu.mPopupWindowSort.showAsDropDown(popupView, -nDisplayDDXOffSet, -nOffSetLocation[1] * 2);
                break;
            case R.id.action_scanning_sort:
                SortScanTimePopupWindow sortScanTimePopupWindow = new SortScanTimePopupWindow(HomeScreen.this, nSortTimeRadioButtonFlag);
                sortScanTimePopupWindow.mPopupWindowSortScanTime.setOutsideTouchable(true);
                sortScanTimePopupWindow.mPopupWindowSortScanTime.setOutsideTouchable(true);
                sortScanTimePopupWindow.mPopupWindowSortScanTime.setFocusable(true);
                sortScanTimePopupWindow.mPopupWindowSortScanTime.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                whereToPlacePopupWindow("SORT_SCAN_TIME", findViewById(R.id.action_sort));
                sortScanTimePopupWindow.mPopupWindowSortScanTime.showAsDropDown(popupView, -nDisplayDDXOffSet, -nOffSetLocation[1] * 2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initReadMoreDialog() {
        dialog = new Dialog(HomeScreen.this, R.style.CustomDialogTheme);
        dialog.setContentView(R.layout.dialog_read_more);
        dialog.setCancelable(true);

        TextView tvHeading = (TextView) dialog.findViewById(R.id.tv_readmore_heading);
        TextView tvSubHeading = (TextView) dialog.findViewById(R.id.tv_readmore_sub_heading);
        TextView tvDescription = (TextView) dialog.findViewById(R.id.tv_readmore_description);
        Typeface lightFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/myriad_pro_light.ttf");
        Typeface regularFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/myriad_pro_regular.ttf");
        tvHeading.setTypeface(regularFace);
        tvSubHeading.setTypeface(lightFace);
        tvDescription.setTypeface(lightFace);
    }


    public void onClickButton(View view) {
        Fragment newFragment;
        FragmentTransaction transaction;
        Bundle bundle;
        bundle = new Bundle();
        String sBackStack = "";

        switch (view.getId()) {
            case R.id.btn_sessions:
                bundle.putInt("index", 0);
                sBackStack = "fragment_sessions";
                break;

            case R.id.btn_booths:
                bundle.putInt("index", 1);
                sBackStack = "fragment_booths";
                break;

            case R.id.btn_products:
                bundle.putInt("index", 2);
                sBackStack = "fragment_products";
                break;

            case R.id.btn_speakers:
                bundle.putInt("index", 3);
                sBackStack = "fragment_speakers";
                break;

            case R.id.btn_home_review:
                isReviewSelected = true;
                nCount = 10;
                sBackStack = "fragment_review_Rating";
                newFragment = ReviewRatingFragment.newInstance("", "");
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                transaction.replace(R.id.flContent, newFragment, sBackStack);
                transaction.addToBackStack(null);
                transaction.commit();
                if (isSearchOpened) {
                    handleSearch();
                }
                textviewTitle.setText(getResources().getString(R.string.event_review));
                break;
        }
        if (!isReviewSelected) {
            //this count will stop the animation from next page.
            nCount = 10;
            whereToPlacePopupWindow("HOME_BUTTON", view);
            show(findViewById(R.id.flContent));
            isNavHomeChecked = false;
            newFragment = new HomeScreenFragment();
            newFragment.setArguments(bundle);
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            transaction.replace(R.id.flContent, newFragment, sBackStack);
            transaction.addToBackStack(null);
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getFragments() != null && fm.getFragments().size() > 1) {
                fm.popBackStackImmediate();
            }
            transaction.commit();
            if (isSearchOpened) {
                handleSearch();
            }
            setMenuItemVisibility(1);
            textviewTitle.setText(getResources().getString(R.string.event_review));
        }
        isReviewSelected = false;
    }

    /*
     To reveal a previously invisible view using this effect:
     below method show is used to produce circular animation effect on home screen buttons.
     */
    private void show(final View mParentView) {
        // get the center for the clipping circle
        /*int cx = (mAnimView.getLeft() + mAnimView.getRight()) / 2;
        int cy = (mAnimView.getTop() + mParentView.getBottom()) / 2;*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the final radius for the clipping circle
            int finalRadius = Math.max(mParentView.getWidth(), mParentView.getHeight());

            //create the animator for this view (the start radius is zero)
            Animator anim;
            anim = ViewAnimationUtils.createCircularReveal(mParentView, nDisplayDDXOffSet, nOffSetLocation[1],
                    0, finalRadius);
            anim.setDuration(350);
            mParentView.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    /*
    below method will handle visibility of toolbar icons for various fragment and activity.
    * nCase accepts 0 and 1 value. 0 = actionSearch will be visible, 1 = all icons will be visible,
    * 2 = all icons are invisible.
     */
    public void setMenuItemVisibility(int nCase) {
        /*
        nCheck = lastExecutedSwitchCase, nCase = case that needs to be executed.
        here we check whether the lastExecutedCase is equal to nCase. we don't execute it as it is of no use where its
        already been set.
        if nCheck doesn't match nCase then we assume that user has selected some other action and below code will be executed.
        Case 0: sets search icon visible and rest invisible.
        Case 1: all the toolbar icons will be visible to the user only if home screen is not selected.(because home screen
                needs only search icon to be selected.)
        Case 2: all the toolbar icons will be invisible to the user only if home screen is not selected.
        Thus this reduces number of executions and processing speed.
         */
        if (nCheck != nCase) {
            switch (nCase) {
                case 0:
                    menu.findItem(R.id.action_search).setVisible(true);
                    menu.findItem(R.id.action_sort).setVisible(false);
                    menu.findItem(R.id.action_filter).setVisible(false);
                    menu.findItem(R.id.action_scanning_sort).setVisible(false);
                    nCheck = nCase;
                    break;
                case 1:
                    if (!isNavHomeChecked) {
                        menu.findItem(R.id.action_search).setVisible(true);
                        menu.findItem(R.id.action_sort).setVisible(true);
                        menu.findItem(R.id.action_filter).setVisible(true);
                        menu.findItem(R.id.action_scanning_sort).setVisible(false);
                        nCheck = nCase;
                    }
                    break;
                case 2:
                    if (!isNavHomeChecked) {
                        menu.findItem(R.id.action_search).setVisible(false);
                        menu.findItem(R.id.action_sort).setVisible(false);
                        menu.findItem(R.id.action_filter).setVisible(false);
                        menu.findItem(R.id.action_scanning_sort).setVisible(false);
                        nCheck = nCase;
                    }
                    break;
                case 3:
                    //if(!isNavHomeChecked){
                    menu.findItem(R.id.action_search).setVisible(true);
                    menu.findItem(R.id.action_sort).setVisible(false);
                    menu.findItem(R.id.action_filter).setVisible(false);
                    menu.findItem(R.id.action_scanning_sort).setVisible(true);
                    nCheck = nCase;
                    break;
                //}
            }
        }
    }

    /*
    below is a override method of custom interface created by @Vijay to communicate between fragments and Activity
    Works well.
     */
    @Override
    public void onFragmentMessage(String TAG, Object data, boolean isNavHomeChecked) {
        /*
        below switch statement is executed when we receive a callback from respective fragments to perform some actions
        which can only be done from HomeScreen.
         */
        switch (TAG) {
            case "ReviewRatingFragment":
                setMenuItemVisibility(2);
                break;
            case "checkSearch":
                if (isSearchOpened) {
                    handleSearch();
                }
                textviewTitle.setText(data.toString());
                break;
            case "SharedElement":
                Log.e("clear Shared Element", "Shared Element");
                nSharedElementFlag = 1;
                break;
            case "REQUEST_LOCATION":
                requestLocationPermission();
                break;
            case "REQUEST_BT":
                enableBluetooth();
                break;
            case "START_ANIMATION":
                /*
                START_ANIMATION is triggered when Glide operation is completed.
                below nCount and handler will be used as trigger for animation.
                by default nCount will be 10 which is 3 times greater than the value compared in pageTransformer method above.
                so it wont execute animation until and unless count gets to 0.
                using handler we are creating a fake drag so that pageTransformer method is triggered manually and animation
                starts by itself.
                 */
                nCount = 0;
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        // force transform with a 1 pixel nudge
                        mViewPagerSlideShow.beginFakeDrag();
                        mViewPagerSlideShow.fakeDragBy(0.1f);
                        mViewPagerSlideShow.endFakeDrag();
                    }
                };
                handler.postDelayed(r, 30);
                break;
            case "SORT_SCAN_VALUE":
                nSortTimeRadioButtonFlag = Integer.parseInt(data.toString());
                break;
            case "SORT_VALUE":
                nSortRadioButtonFlag = Integer.parseInt(data.toString());
                break;
            default:
                setMenuItemVisibility(1);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        int size = getSupportFragmentManager().getBackStackEntryCount();
        if (isSearchOpened) {
            handleSearch();
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (!HomeScreenFragment.handleBackPressed(getSupportFragmentManager())) {

            if (size == 1) {
                textviewTitle.setText(getResources().getString(R.string.home)); //sets title when size is 1 which means no fragments
                setMenuItemVisibility(0);  //sets visibility of toolbar menu items to false
                navigationView.setCheckedItem(R.id.nav_home);
                isHomeScreenFragmentLoaded = false;
            }
            super.onBackPressed();
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flContent);
            if (size > 1) {
                String sTag = currentFragment.getTag();
                if (!sTag.contains("_")) {
                    textviewTitle.setText(sTag);
                    setMenuItemVisibility(2);
                } else
                    textviewTitle.setText(getResources().getString(R.string.event_name));
            }
        }
    }


    /*
    handles all search related functionality of toolbar
     */
    protected void handleSearch() {

        if (isSearchOpened) { //test if the search is open
            etSearch.requestFocus();

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

            //add the search icon in the action bar
            //mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_open_search));
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            setCustomToolBar();
            isSearchOpened = false;

            /*
            below if condition will check if home of navigation drawer is clicked. in case if clicked that means
            search is triggered from HomeScreen and we need to set back search icon visibility to true.
            else
            set visibility of all the toolbar icons to true.(which means it is triggered from fragments &
            all the icons needs to be visible once user clicks back from search).
             */
            if (navigationView.getMenu().findItem(R.id.nav_home).isChecked())
                setMenuItemVisibility(0);
            else
                setMenuItemVisibility(1);
        } else { //open the search entry
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            getSupportActionBar().setCustomView(R.layout.search_bar);//add the custom view
            getSupportActionBar().setDisplayShowTitleEnabled(false); //hide the title
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            etSearch = (EditText) getSupportActionBar().getCustomView().findViewById(R.id.et_search); //the text editor
            ibSearchBackArrow = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.ib_search_go_back);

            //this is a listener to do a search when the user clicks on search button
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return actionId == EditorInfo.IME_ACTION_SEARCH;
                    //use below code to do a search and remove return actionID statement.
                    /*if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        //doSearch();
                        return true;
                    }
                    return false;*/
                }
            });

            etSearch.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);


            ibSearchBackArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleSearch();
                }
            });
            //add the close icon
            //mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_search));

            isSearchOpened = true;

            /*
            when user clicks on search, only search icon needs to be present in the toolbar,
            rest will be invisible.
             */
            setMenuItemVisibility(0);
        }
    }

    /*
    handles toolbarCustomViews (Textview)
     */
    private void setCustomToolBar() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar,
                R.string.home, R.string.home
        );

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /*
        below code sets custom textview to the actionbar title
         */
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        //TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        getSupportActionBar().setCustomView(viewActionBar, params);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        mDrawerToggle.syncState();
    }

    private void whereToPlacePopupWindow(String sCase, View mView) {
        switch (sCase) {
            case "FILTER":
                //get the offset of view from left respect to the screen
                popupView = findViewById(R.id.action_filter);
                //get the width of display to do the calculation of where popup window should be displayed
                nUserDisplayWidth = getResources().getDisplayMetrics().widthPixels;

                /*location stores the absolute co ordinates of x and y of particular view on the screen.
                location[0] contains x value
                location[1] contains y value
                below calculation part is created by Vijay
                 */
                nOffSetLocation = new int[2];
                popupView.getLocationInWindow(nOffSetLocation);
                /*
                below code gets the x offset from right of the screen and display the popup window on that offset.
                displayX = width of display (-) location[0] + offset divided by 3 gets us the final location in pixels
                of where the pop up window should be displayed.
                 */
                nDisplayOffSetD3 = (nUserDisplayWidth - nOffSetLocation[0]) / 3;
                 /*
                nDisplayDDXOffSet = display drop down x off set
                display offset divided by 3 to achieve the perfect calculation
                 */
                nDisplayDDXOffSet = (nUserDisplayWidth - nOffSetLocation[0]) + nDisplayOffSetD3;
                break;

            case "SORT":
                popupView = findViewById(R.id.action_sort);
                nUserDisplayWidth = getResources().getDisplayMetrics().widthPixels;

                nOffSetLocation = new int[2];
                popupView.getLocationInWindow(nOffSetLocation);
                nDisplayOffSetD3 = (nUserDisplayWidth - nOffSetLocation[0]) / 2;

                nDisplayDDXOffSet = (nUserDisplayWidth - nOffSetLocation[0]) + nDisplayOffSetD3;
                break;

            case "SORT_SCAN_TIME":
                popupView = findViewById(R.id.action_scanning_sort);
                nUserDisplayWidth = getResources().getDisplayMetrics().widthPixels;

                nOffSetLocation = new int[2];
                popupView.getLocationInWindow(nOffSetLocation);
                nDisplayOffSetD3 = (nUserDisplayWidth - nOffSetLocation[0]) / 2;

                nDisplayDDXOffSet = (nUserDisplayWidth - nOffSetLocation[0]) + nDisplayOffSetD3;
                break;

            case "HOME_BUTTON":
                /*
                this is called from home screen button click
                 */
                nUserDisplayHeight = getResources().getDisplayMetrics().heightPixels; //holds height of screen in pixels

                nOffSetLocation = new int[2];
                mView.getLocationInWindow(nOffSetLocation);
                nDisplayOffSetD3 = (nUserDisplayHeight + nOffSetLocation[1]) / 10;

                nDisplayDDXOffSet = (nOffSetLocation[0] / 2) + nDisplayOffSetD3;
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mViewPagerSlideShow.setCurrentItem(resultCode);
        /*
        case 0: takes care of information between detailActivity and HomeScreen Viewpager.
        case 1: takes care of bluetooth services related to ScanningFragment and HomeScreen.
        case 2: takes care of gps related operations.
         */
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    int result = data.getExtras().getInt("position");
                    mViewPagerSlideShow.setCurrentItem(result);
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    final BluetoothManager bluetoothManager =
                            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
                    if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() &&
                            !isMyServiceRunning(scanning.SimpleService.class)) {
                        Intent in = new Intent(HomeScreen.this, SimpleService.class);
                        startService(in);
                        Log.e("Service", "Service is started");
                    } else {
                        Log.e("Service", "Service is already running");
                    }
                } else {
                    onBackPressed();
                    Log.e("bluetooth", "bluetooth access denied");
                }
                break;
            case 3:
                if (resultCode == Activity.RESULT_OK) {
                    //enableBT();
                    enableBluetooth();
                } else {
                    @SuppressWarnings("unused") AlertDialogs alertDialogs = new AlertDialogs(HomeScreen.this, 2, mListener);
                }
                break;
        }
    }

    /*
    checks if user has logged in for the first time.
     */
    public boolean checkUserFirstTime() {
        SharedPreferences sharedPreferences;
        boolean isUserFirstTime;
        sharedPreferences = HomeScreen.this.getSharedPreferences(StaticReferenceClass.sAPP_PREFERENCES, Context.MODE_PRIVATE); //1
        isUserFirstTime = sharedPreferences.getBoolean(getResources().getString(R.string.is_user_first_time), true); //2
        return isUserFirstTime;
    }

    public void loadScanningFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ScanningFragment();
        transaction.replace(R.id.flContent, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //enables bluetooth without user's consent
    /*public void enableBT() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) HomeScreen.this.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter.enable();
    }*/

    //enables bluetooth dialog to allowing the user to interact
    public void enableBluetooth() {
        if (HomeScreen.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            /*
             * To open the bluetooth
             */
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) HomeScreen.this.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 2);
            }
            //if bluetooth is already switched on it will start service.
            else {
                Intent in = new Intent(HomeScreen.this, SimpleService.class);
                startService(in);
            }
        } else {
            Toast.makeText(HomeScreen.this, "Doesn't Supports BLE",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //asks the user to switch on gps with just ok button in a dialog.
    public void requestLocationPermission() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(HomeScreen.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //enableBT();
                        enableBluetooth();
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(HomeScreen.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int PERMISSION_ALL, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (PERMISSION_ALL) {
            /*
            this is related to app permissions which occurs in marshmallow and above.
            case 1: handles location permissions.
             */
            case 1: {
                // If request is cancelled, the result arrays are empty.
                //isLocationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                //isStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                //if (grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermission();
                    //Do the stuff that requires permission...
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HomeScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        //Show permission explanation dialog...
                        @SuppressWarnings("unused") AlertDialogs alertDialogs = new AlertDialogs(HomeScreen.this, 1, mListener);
                        //Toast.makeText(getActivity(), "Please we need the permission", Toast.LENGTH_SHORT).show();
                    } else {
                        //Never ask again selected, or device policy prohibits the app from having that permission.
                        //So, disable that feature, or fall back to another situation...
                        @SuppressWarnings("unused") AlertDialogs alertDialogs = new AlertDialogs(HomeScreen.this, 1, mListener);
                    }
                }
                //}
            }
            break;

        }
    }

    //this method checks if service is running already
    //refer - http://stackoverflow.com/questions/17588910/check-if-service-is-running-on-android
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*private void startFakeDrag(){
        nCount=10;
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                // force transform with a 1 pixel nudge
                mViewPagerSlideShow.beginFakeDrag();
                mViewPagerSlideShow.fakeDragBy(0.1f);
                mViewPagerSlideShow.endFakeDrag();
            }
        };
        handler.postDelayed(r, 50);
    }*/

    /*private void cancelHomeAnimation() {
        final Handler mHandlerAnimation = new Handler();

        Thread mThread = new Thread() {
            @Override
            public void run() {
                mHandlerAnimation.post(new Runnable() {
                    @Override
                    public void run() {
                        animZoomIn.cancel();
                        animZoomIn.reset();
                        animZoomOut.cancel();
                        animZoomOut.reset();
                    }
                });
            }
        };
        if (!mThread.isAlive()) {
            mThread.run();
        }
    }*/
}
