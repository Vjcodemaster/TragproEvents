package com.sspl.org.tp;

/*
 * Created by Vj on 04-Apr-17.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import app_utility.OnFragmentInteractionListener;

class DetailImagePagerAdapter extends PagerAdapter {
    private LayoutInflater mLayoutInflater;
    private Activity aActivity;

    private OnFragmentInteractionListener mListener;

    private String[] sImageURL = {"https://s3.amazonaws.com/sohamsaabucket/01-min.jpg", "https://s3.amazonaws.com/sohamsaabucket/02-min.jpg",
            "https://s3.amazonaws.com/sohamsaabucket/03-min.jpg"};

    private ArrayList<Integer> nALResources = new ArrayList<>();
    //private OnFragmentInteractionListener mListener;


    DetailImagePagerAdapter(Activity aActivity, ArrayList<Integer> nALResources,
                            OnFragmentInteractionListener mListener) {
        this.aActivity = aActivity;
        this.nALResources = nALResources;
        this.mListener = mListener;
        mLayoutInflater = (LayoutInflater) aActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return nALResources.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.detail_image_pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageview_detail);

        /*
        here we are using glide library to get the image which is already downloaded and saved in cache memory.
        Works well.
         */
        Glide.with(aActivity)
                .load(sImageURL[position])
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
