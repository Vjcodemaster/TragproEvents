package com.sspl.org.tp;

/*
 * Created by Vj on 30-Mar-17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import app_utility.CircularProgressBar;
import app_utility.OnFragmentInteractionListener;

class HomeImagePagerAdapter extends PagerAdapter {

    private Activity aActivity;
    private LayoutInflater mLayoutInflater;
    private int[] mResources;
    private OnFragmentInteractionListener mListener;
    private CircularProgressBar circularProgressBar;

    private String[] sImageURL = {"https://s3.amazonaws.com/sohamsaabucket/01-min.jpg", "https://s3.amazonaws.com/sohamsaabucket/02-min.jpg",
            "https://s3.amazonaws.com/sohamsaabucket/03-min.jpg"};

    HomeImagePagerAdapter(Activity aActivity, int[] mResources, OnFragmentInteractionListener mListener, CircularProgressBar circularProgressBar) {
        this.aActivity = aActivity;
        this.mResources = mResources;
        this.mListener = mListener;
        this.circularProgressBar = circularProgressBar;
        mLayoutInflater = (LayoutInflater) aActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mResources.length;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.home_image_pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        Glide.with(aActivity)
                .load(sImageURL[position])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        circularProgressBar.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mListener.onFragmentMessage("START_ANIMATION", aActivity.getString(R.string.app_name), false);
                        circularProgressBar.dismiss();
                        return false;
                    }
                })
                .into(imageView);


        imageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                mListener.onFragmentMessage("SharedElement", aActivity.getString(R.string.app_name), false);
                // create the transition animation - the images in the layouts
                // of both activities are defined with android:transitionName="robot"
                // start the new activity

                Intent intent = new Intent(aActivity, DetailActivity.class);
                // Pass data object in the bundle and populate details activity.

                ArrayList<Integer> nALResources = new ArrayList<>();

                for (int mResource : mResources) {
                    nALResources.add(mResource);
                }

                intent.putIntegerArrayListExtra("id", nALResources);


                ViewPager mViewPager = (ViewPager) container.getRootView().findViewById(R.id.viewpager_slideshow);

                intent.putExtra("position", mViewPager.getCurrentItem());
                intent.putExtra("isFromUser", true);

                TextView tvDescription = (TextView) container.getRootView().findViewById(R.id.tv_about_event);
                LinearLayout llHomeEventHeading = (LinearLayout) container.getRootView().findViewById(R.id.llHomeTransitionGroup);
                LinearLayout llViewPagerBubble = (LinearLayout) container.getRootView().findViewById(R.id.ll_viewpager);

                Pair<View, String> pViewPager = Pair.create((View) mViewPager, aActivity.getResources().getString(R.string.viewpager_transition));
                Pair<View, String> pDescription = Pair.create((View) tvDescription, aActivity.getResources().getString(R.string.tv_description_transition));
                Pair<View, String> pEventHeading = Pair.create((View) llHomeEventHeading, aActivity.getResources().getString(R.string.ll_event_heading_transition));
                Pair<View, String> pViewPagerBubble = Pair.create((View) llViewPagerBubble, aActivity.getResources().getString(R.string.ll_bubble_transition));
                @SuppressWarnings("unchecked")
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(aActivity, pViewPager, pDescription,
                        pEventHeading, pViewPagerBubble);
                //aActivity.startActivity(intent, options.toBundle());

                /*
                below code startActivityForResult is used when we need a communication between activities. works fine
                 */
                aActivity.startActivityForResult(intent, 0, options.toBundle());
                //ZoomImageAnimation zoomImageAnimation = new ZoomImageAnimation(imageView, R.drawable.imageslide);
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
