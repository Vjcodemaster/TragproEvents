package com.sspl.org.tp;

/*
 * Created by Vj on 21-Mar-17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

class ReviewRatingRVAdapter extends RecyclerView.Adapter<ReviewRatingRVAdapter.ReviewRatingHolder> {

    private Context context;
    //private int i;
    private ArrayList<String> alUserReview;
    private ArrayList<Float> alUserRating;


    ReviewRatingRVAdapter(Context context, ArrayList<String> alUserReview, ArrayList<Float> alUserRating) {
        this.context = context;
        this.alUserReview = alUserReview;
        this.alUserRating = alUserRating;
    }

    @Override
    public ReviewRatingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_review_rating, parent, false);

        return new ReviewRatingRVAdapter.ReviewRatingHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewRatingRVAdapter.ReviewRatingHolder holder, int position) {

        holder.tvRvDescription.setText(alUserReview.get(position));
        holder.rbRating.setRating(alUserRating.get(position));
    }

    @Override
    public int getItemCount() {
        return alUserReview.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ReviewRatingHolder extends RecyclerView.ViewHolder {
        //ImageView mImageView;
        TextView tvRvDescription;
        RatingBar rbRating;

        ReviewRatingHolder(View itemView) {
            super(itemView);
            tvRvDescription = (TextView) itemView.findViewById(R.id.tv_rv_review);
            rbRating = (RatingBar) itemView.findViewById(R.id.rb_rv_review_rating);
            //mImageView = (ImageView) itemView.findViewById(R.id.products_rv_image_view);
        }
    }
}
