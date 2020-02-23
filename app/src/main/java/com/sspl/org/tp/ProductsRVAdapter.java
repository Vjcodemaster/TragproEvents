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

class ProductsRVAdapter extends RecyclerView.Adapter<ProductsRVAdapter.ProductsHolder> {

    private OnFragmentInteractionListener mListener;
    private Context context;
    private int i;
    private FragmentManager supportFragmentManager;


    ProductsRVAdapter(Context context, int i, FragmentManager supportFragmentManager, OnFragmentInteractionListener mListener) {
        this.context = context;
        this.i = i;
        this.supportFragmentManager = supportFragmentManager;
        this.mListener = mListener;
    }

    @Override
    public ProductsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_products, parent, false);

        return new ProductsRVAdapter.ProductsHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductsRVAdapter.ProductsHolder holder, int position) {
        holder.tvProductsReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new ReviewRatingFragment();
                FragmentTransaction transaction = supportFragmentManager.beginTransaction();
                transaction.replace(R.id.flContent, newFragment);
                transaction.addToBackStack("ProductsFragment");
                transaction.commit();
                String sTag = "Products Review";
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

    static class ProductsHolder extends RecyclerView.ViewHolder{
        ImageView mImageView;
        TextView tvProductsReview;

        ProductsHolder(View itemView) {
            super(itemView);
            tvProductsReview = (TextView) itemView.findViewById(R.id.tv_products_review);
            mImageView = (ImageView) itemView.findViewById(R.id.products_rv_image_view);
        }
    }
}
