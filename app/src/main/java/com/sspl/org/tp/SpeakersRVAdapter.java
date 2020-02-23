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

class SpeakersRVAdapter extends RecyclerView.Adapter<SpeakersRVAdapter.SpeakersHolder> {

    private OnFragmentInteractionListener mListener;
    private Context context;
    private int i;
    private FragmentManager supportFragmentManager;


    SpeakersRVAdapter(Context context, int i, FragmentManager supportFragmentManager, OnFragmentInteractionListener mListener) {
        this.context = context;
        this.i = i;
        this.supportFragmentManager = supportFragmentManager;
        this.mListener = mListener;
    }

    @Override
    public SpeakersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_speakers, parent, false);

        return new SpeakersRVAdapter.SpeakersHolder(view);
    }

    @Override
    public void onBindViewHolder(SpeakersRVAdapter.SpeakersHolder holder, int position) {
        holder.tvSpeakersReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new ReviewRatingFragment();
                FragmentTransaction transaction = supportFragmentManager.beginTransaction();
                transaction.replace(R.id.flContent, newFragment);
                transaction.addToBackStack("SpeakersFragment");
                transaction.commit();
                String sTag = "Speakers Review";
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

    static class SpeakersHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView tvSpeakersReview;

        SpeakersHolder(View itemView) {
            super(itemView);
            tvSpeakersReview = (TextView) itemView.findViewById(R.id.tv_speakers_review);
            mImageView = (ImageView) itemView.findViewById(R.id.speakers_rv_image_view);
        }
    }
}
