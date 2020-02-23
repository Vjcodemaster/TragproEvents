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
 * Created by Vj on 16-Mar-17.
 */

class SessionsRVAdapter extends RecyclerView.Adapter<SessionsRVAdapter.SessionsHolder> {

    private OnFragmentInteractionListener mListener;
    public Context context;
    private int i;
    private FragmentManager supportFragmentManager;


    SessionsRVAdapter(Context context, int i, FragmentManager supportFragmentManager, OnFragmentInteractionListener mListener) {
        this.context = context;
        this.i = i;
        this.supportFragmentManager = supportFragmentManager;
        this.mListener = mListener;
    }

    @Override
    public SessionsHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_sessions, parent, false);

        return new SessionsHolder(view);
    }

    @Override
    public void onBindViewHolder(SessionsHolder holder, int position) {

        holder.tvSessionsReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new ReviewRatingFragment();
                //newFragment.setArguments(bundle);
                //FragmentTransaction transaction = HomeScreen.mContext.getSupportFragmentManager().beginTransaction();
                FragmentTransaction transaction = supportFragmentManager.beginTransaction();
                transaction.replace(R.id.flContent, newFragment);
                transaction.addToBackStack("SessionFragment");
                transaction.commit();
                String sTag = "Sessions Review";
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


    static class SessionsHolder extends RecyclerView.ViewHolder {
       ImageView mImageView;
        TextView tvSessionsReview;


        SessionsHolder(View itemView) {
            super(itemView);
            tvSessionsReview = (TextView) itemView.findViewById(R.id.tv_sessions_review);
            mImageView = (ImageView) itemView.findViewById(R.id.sessions_rv_image_view);
        }
    }
}
