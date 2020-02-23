package com.sspl.org.tp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app_utility.OnFragmentInteractionListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link app_utility.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReviewRatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewRatingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button btnWriteReview, btnReviewSettings;

    RatingBar ratingBarUser;

    TextView tvUserReviewText, tvUserName;

    Dialog dialog;

    private ReviewRatingRVAdapter reviewRatingRVAdapter;

    RecyclerView recyclerView;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private OnFragmentInteractionListener mListener;

    private float fUserRating = 3;

    ArrayList<String> alUserReview;

    private ArrayList<Float> alUserRating;

    String sUserReview = "";

    boolean isEditModeOn = false;

    public ReviewRatingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewRatingFragment.
     */
    public static ReviewRatingFragment newInstance(String param1, String param2) {
        ReviewRatingFragment fragment = new ReviewRatingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alUserReview = new ArrayList<>();
        alUserRating = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            alUserReview.add(getResources().getString(R.string.description));
        }
        alUserRating.add(3.5f);
        alUserRating.add(4f);
        alUserRating.add(4f);
        alUserRating.add(4.5f);
        alUserRating.add(5f);
        alUserRating.add(3f);
        //reviewRatingRVAdapter = new ReviewRatingRVAdapter(getActivity(), 6);
        reviewRatingRVAdapter = new ReviewRatingRVAdapter(getActivity(), alUserReview, alUserRating);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.review_rating, container, false);

        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        //collapsingToolbarLayout.setTitle(getActivity().getResources().getString(R.string.products));
        //dynamicToolbarColor();
        calculateDominantColor();
        /*
        below mListener code sends info to the homescreen activity, which can be used to perform certain tasks
        mListener is a interface & onFragmentMessage is a method of its class.
         */
        mListener.onFragmentMessage("ReviewRatingFragment", getString(R.string.app_name), false);

        ratingBarUser = (RatingBar) view.findViewById(R.id.rating_bar_user);
        final LinearLayout llPost = (LinearLayout) view.findViewById(R.id.ll_post);
        final Button btnPostRR = (Button) view.findViewById(R.id.btn_post_rr);
        btnWriteReview = (Button) view.findViewById(R.id.btn_write_review);
        btnReviewSettings = (Button) view.findViewById(R.id.btn_rr_review_settings);
        tvUserReviewText = (TextView) view.findViewById(R.id.tv_rr_user_review);
        tvUserName = (TextView) view.findViewById(R.id.tv_rr_user_name);

        btnWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        btnPostRR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_review_rating);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(reviewRatingRVAdapter);

        /*
        below observer will edit the height of linearlayout which holds post button, so that it aligns the view as
        per the design requirement which should be beside ratingBar.
        Since it was not achievable in xml, the height of linearlayout is compared with height of ratingBar programatically
        and the same is set to the linearlayout.
        TreeObserver will wait until the layout design is completed and then edit the height of layout.
         */
        /*final ViewTreeObserver observer = llPost.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (ratingBarUser.getHeight() > llPost.getHeight()) {
                    ViewGroup.LayoutParams params = llPost.getLayoutParams();
                    params.height = ratingBarUser.getHeight();
                    llPost.setLayoutParams(params);
                }
                //removes listener
                llPost.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });*/
        initWriteReviewDialog();

        btnReviewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        //listener for rating bar
        ratingBarUser.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                fUserRating = rating;
                RatingBar rbDialog = (RatingBar) dialog.findViewById(R.id.rb_dialog);
                rbDialog.setRating(rating);
                dialog.show();
                //Toast.makeText(getActivity(), Float.toString(fUserRating), Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return view;


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void initWriteReviewDialog() {
        dialog = new Dialog(getActivity(), R.style.CustomDialogTheme90);
        dialog.setContentView(R.layout.dialog_write_review);
        dialog.setCancelable(true);

        TextView tvHeading = (TextView) dialog.findViewById(R.id.tv_write_review_heading);
        final EditText etReview = (EditText) dialog.findViewById(R.id.et_write_review);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_write_review_cancel);
        Button btnPost = (Button) dialog.findViewById(R.id.btn_post);
        RatingBar rbDialog = (RatingBar) dialog.findViewById(R.id.rb_dialog);


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkPostEligibility(etReview);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etReview.getText().clear();
                dialog.cancel();
            }
        });

        rbDialog.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                fUserRating = rating;
            }
        });

        Typeface lightFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/myriad_pro_light.ttf");
        Typeface regularFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/myriad_pro_regular.ttf");
        tvHeading.setTypeface(regularFace);
        etReview.setTypeface(lightFace);
    }

    /*
    checkPostEligibility checks if user has entered atleast 10 characters in edittext
    if condition meets then it adds the user review and rating to the 0th index of both arraylist.
    so that the info will be viewed on top.
    then we will set the adapter.
    then scroll recycler view to the position 0, where the new item is inserted.
    notify will give animation effect like adding the new element to recyclerview.
     */
    public void checkPostEligibility(EditText etReview) {
        sUserReview = etReview.getText().toString();
        if (sUserReview.length() < 10) {
            etReview.getText().clear();
            Toast.makeText(getActivity(), "Minimum of 10 Characters required to post.", Toast.LENGTH_SHORT).show();
        } else if (isEditModeOn) {
            etReview.getText().clear();
            alUserReview.remove(0);
            alUserRating.remove(0);
            alUserReview.add(0, sUserReview);
            alUserRating.add(0, fUserRating);
            reviewRatingRVAdapter = new ReviewRatingRVAdapter(getActivity(), alUserReview, alUserRating);
            recyclerView.setAdapter(reviewRatingRVAdapter);
            recyclerView.scrollToPosition(0);
            reviewRatingRVAdapter.notifyItemInserted(0);
            //tvUserName.setText("Vijay E H");
            tvUserReviewText.setText(sUserReview);
            ratingBarUser.setRating(fUserRating);
            isEditModeOn = false;
        } else {
            etReview.getText().clear();
            alUserReview.add(0, sUserReview);
            alUserRating.add(0, fUserRating);
            reviewRatingRVAdapter = new ReviewRatingRVAdapter(getActivity(), alUserReview, alUserRating);
            recyclerView.setAdapter(reviewRatingRVAdapter);
            recyclerView.scrollToPosition(0);
            reviewRatingRVAdapter.notifyItemInserted(0);
            //tvUserName.setText("Vijay E H");
            tvUserReviewText.setText(sUserReview);
            btnReviewSettings.setVisibility(View.VISIBLE);
            ratingBarUser.setRating(fUserRating);
            ratingBarUser.setIsIndicator(true);
        }
    }

    /*
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }*/
    /*private void dynamicToolbarColor() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imageslide);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(R.attr.cardBackgroundColor));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(R.attr.colorPrimaryDark));
            }
        });
    }*/

    /*
    below method gets color of image from middle of image pixel and sets to collapsingToolbarLayout
     */

    private void calculateDominantColor() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imageslide);
        int x = bitmap.getWidth() / 2;
        int y = bitmap.getHeight() / 2;
        int pixel = bitmap.getPixel(x, y);

        /*int alphaValue = Color.alpha(pixel);
        int redValue = Color.red(pixel);
        int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);

        int color = (redValue & 0xff) << 16 | (blueValue & 0xff) << 16 | (greenValue & 0xff);*/
        //int color = (redValue & 0xff) << 16 | (blueValue & 0xff) << 16 | (greenValue & 0xff);
        //Toast.makeText(getActivity(), ""+ pixel+"", Toast.LENGTH_SHORT).show();
        collapsingToolbarLayout.setContentScrimColor(pixel);
        collapsingToolbarLayout.setStatusBarScrimColor(pixel);
    }

    /*
    showPopup is related to review rating fragment 3 dots button.
    on click of 3 dots a menu xml is displayed and 2 listeners has been placed one for edit and one for delete.
     */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater().inflate(R.menu.review_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_rr_edit:
                        EditText etEditReview = (EditText) dialog.findViewById(R.id.et_write_review);
                        etEditReview.setText(sUserReview);
                        dialog.show();
                        isEditModeOn = true;
                        Toast.makeText(getActivity(), "edit", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_rr_delete:
                        alUserReview.remove(0);
                        alUserRating.remove(0);
                        reviewRatingRVAdapter = new ReviewRatingRVAdapter(getActivity(), alUserReview, alUserRating);
                        recyclerView.setAdapter(reviewRatingRVAdapter);
                        recyclerView.scrollToPosition(0);
                        reviewRatingRVAdapter.notifyItemRemoved(0);
                        ratingBarUser.setRating(0);
                        ratingBarUser.setIsIndicator(false);
                        tvUserReviewText.setText("");
                        btnReviewSettings.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "delete", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }
}
