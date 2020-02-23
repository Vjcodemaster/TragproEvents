package com.sspl.org.tp;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app_utility.OnFragmentInteractionListener;
import library.ZoomOutPageTransformer;

public class DetailActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private ViewPager mViewPagerImageDetail;
    ArrayList<Integer> nALResources = new ArrayList<>();

    ImageView[] imageViews;
    ImageView ivLeftArrow;
    ImageView ivRightArrow;

    int position;

    View viewActionBar;
    TextView textviewTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator sla = AnimatorInflater.loadStateListAnimator(DetailActivity.this, R.animator.appbar_always_elevated);
            toolbar.setStateListAnimator(sla);
        }
        //viewActionBar = getLayoutInflater().inflate(R.layout.toolbar_textview, null);
        viewActionBar = View.inflate(DetailActivity.this, R.layout.toolbar_textview, null);
        textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(getResources().getString(R.string.home));

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        getSupportActionBar().setCustomView(viewActionBar, params);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        Bundle bundle;
        bundle = getIntent().getExtras();
        position = bundle.getInt("position");
        nALResources = bundle.getIntegerArrayList("id");
        //final boolean isFromUser = bundle.getBoolean("isFromUser");

        TextView tvAboutEvent = (TextView) findViewById(R.id.tvAboutEvent);
        tvAboutEvent.setText(getResources().getString(R.string.description));

        mViewPagerImageDetail = (ViewPager) findViewById(R.id.viewpager_image_detail);
        OnFragmentInteractionListener mListener = DetailActivity.this;

        /*
        adapter for Image viewpager.
         */
        final DetailImagePagerAdapter mDetailImagePagerAdapter = new DetailImagePagerAdapter(DetailActivity.this, nALResources,
                mListener);
        mViewPagerImageDetail.setAdapter(mDetailImagePagerAdapter);
        mViewPagerImageDetail.setCurrentItem(position);

        LinearLayout llViewPager = (LinearLayout) findViewById(R.id.llDetailBubble);

        imageViews = new ImageView[nALResources.size()];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(getApplicationContext());
            /*
            height and width of bubble
             */
            LinearLayout.LayoutParams layoutParamsIV = new LinearLayout.LayoutParams(10, 10);
            layoutParamsIV.setMargins(5, 10, 5, 10);
            imageViews[i].setLayoutParams(layoutParamsIV);
            if (i == position) {
                imageViews[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_solid, null));
                LayerDrawable bgDrawable = (LayerDrawable) imageViews[i].getDrawable();
                final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.shape_bubble_solid_id);
                shape.setColor(ResourcesCompat.getColor(getResources(), R.color.darkBlue, null));
            } else {
                imageViews[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_holo, null));
                LayerDrawable bgDrawable = (LayerDrawable) imageViews[i].getDrawable();
                final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.shape_bubble_holo_id);
                shape.setColor(ResourcesCompat.getColor(getResources(), R.color.whiteBlue, null));
            }
            llViewPager.addView(imageViews[i]);
        }

        mViewPagerImageDetail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < nALResources.size(); i++) {
                    if (i == position) {
                        imageViews[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_solid, null));
                        LayerDrawable bgDrawable = (LayerDrawable) imageViews[i].getDrawable();
                        final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.shape_bubble_solid_id);
                        shape.setColor(ResourcesCompat.getColor(getResources(), R.color.darkBlue, null));

                    } else {
                        imageViews[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_holo, null));
                        LayerDrawable bgDrawable = (LayerDrawable) imageViews[i].getDrawable();
                        final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.shape_bubble_holo_id);
                        shape.setColor(ResourcesCompat.getColor(getResources(), R.color.whiteBlue, null));
                    }
                }
                handleArrow(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPagerImageDetail.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                ZoomOutPageTransformer zoomOutPageTransformer = new ZoomOutPageTransformer();
                zoomOutPageTransformer.transformPage(page, position);
            }
        });

        ivLeftArrow = (ImageView) findViewById(R.id.imageview_detail_leftarrow);
        ivRightArrow = (ImageView) findViewById(R.id.imageview_detail_rightarrow);

        handleArrow(position);

        ivLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerImageDetail.setCurrentItem(mViewPagerImageDetail.getCurrentItem() - 1);
            }
        });

        ivRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerImageDetail.setCurrentItem(mViewPagerImageDetail.getCurrentItem() + 1);
            }
        });

    }

    @Override
    public void onFragmentMessage(String TAG, Object data, boolean isNavHomeChecked) {
        if (TAG.equals("transition")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DetailActivity.this.startPostponedEnterTransition();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //below viewpager code sets position back to the same position of home viewpager for better transition effect
        /*if (mViewPagerImageDetail.getCurrentItem() != position)
            mViewPagerImageDetail.setCurrentItem(position);*/

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_solid, null));
        //imageViews[position].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_solid, null));
        LayerDrawable bgDrawable = (LayerDrawable) imageView.getDrawable();
        final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.shape_bubble_solid_id);
        shape.setColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));

        //imageViews[position].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_holo, null));
        imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bubble_holo, null));
        LayerDrawable bgDrawable1 = (LayerDrawable) imageView.getDrawable();
        final GradientDrawable shape1 = (GradientDrawable) bgDrawable1.findDrawableByLayerId(R.id.shape_bubble_holo_id);
        shape1.setColor(ResourcesCompat.getColor(getResources(), R.color.bubbleGrey, null));
        /*
        below code is for onActivityResult of HomeScreen. Which is a communication system between activities.
        for below code to work, we must start activity with onActivityForResult.
         */
        int currentPosition = mViewPagerImageDetail.getCurrentItem();
        Bundle bundle = new Bundle();
        bundle.putInt("position", currentPosition);
        bundle.putString("DETAIL_ACTIVITY", "DETAIL_ACTIVITY");
        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);
        setResult(RESULT_OK, mIntent);
        //activity results ends

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*
            this is bit hacky as we are using back button of default support actionbar to do a onbackpressed
            to undo the transition from detailActivity to HomeScreen.
            Works fine.
             */
            case android.R.id.home:
                onBackPressed();
                return true;
            //triggers only when drawer is not open, if not then handles search functionality of actionbar/toolbar
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleArrow(int position) {
        if (position == 0) {
            ivLeftArrow.setVisibility(View.GONE);
        } else if (position == nALResources.size() - 1) {
            ivRightArrow.setVisibility(View.GONE);
        } else {
            ivLeftArrow.setVisibility(View.VISIBLE);
            ivRightArrow.setVisibility(View.VISIBLE);
        }
    }
}
