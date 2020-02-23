package com.sspl.org.tp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app_utility.OnFragmentInteractionListener;

/*
 * Created by Vj on 20-Mar-17.
 */

class BoothsRVAdapter extends RecyclerView.Adapter<BoothsRVAdapter.BoothsHolder> {

    private OnFragmentInteractionListener mListener;
    private Context context;
    private int i;
    private FragmentManager supportFragmentManager;


    BoothsRVAdapter(Context context, int i, FragmentManager supportFragmentManager, OnFragmentInteractionListener mListener) {
        this.context = context;
        this.i = i;
        this.supportFragmentManager = supportFragmentManager;
        this.mListener = mListener;
    }

    @Override
    public BoothsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_booths, parent, false);

        return new BoothsRVAdapter.BoothsHolder(view);
    }

    @Override
    public void onBindViewHolder(BoothsRVAdapter.BoothsHolder holder, int position) {
        holder.tvBoothsReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new ReviewRatingFragment();
                //newFragment.setArguments(bundle);
                //FragmentTransaction transaction = HomeScreen.mContext.getSupportFragmentManager().beginTransaction();
                FragmentTransaction transaction = supportFragmentManager.beginTransaction();
                transaction.replace(R.id.flContent, newFragment);
                transaction.addToBackStack("BoothsFragment");
                transaction.commit();
                /*
                checkSearch will perform search related operation and
                sTag is used as object and passed via interface to set title of toolbar
                 */
                String sTag = "Booths Review";
                mListener.onFragmentMessage("checkSearch", sTag, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return i;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class BoothsHolder extends RecyclerView.ViewHolder{
        ImageView mImageView;
        TextView tvBoothsReview;

        BoothsHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.booths_rv_image_view);
            tvBoothsReview = (TextView) itemView.findViewById(R.id.tv_booths_review);
        }
    }
}
