package scanning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.sspl.org.tp.R;

import java.util.ArrayList;
import java.util.Collections;

public class RecentTabRecyclerVAdapter extends RecyclerView.Adapter<RecentTabRecyclerVAdapter.RecentTabHolder> {

    private Context context;

    private ArrayList<String> alName = new ArrayList<>();
    private ArrayList<String> alNumber = new ArrayList<>();
    private ArrayList<String> alEmail = new ArrayList<>();
    private ArrayList<String> alDesignation = new ArrayList<>();
    private ArrayList<String> alDate = new ArrayList<>();
    private ArrayList<String> alTime = new ArrayList<>();
    private ArrayList<String> alLastSeenTime = new ArrayList<>();

        private ArrayList<Integer> alTagID = new ArrayList<>();

    private ArrayList<String> alBeaconInfo = new ArrayList<>();

    private static ArrayList<Integer> nALSelectedItemIndex = new ArrayList<>();

    RecentTabRecyclerVAdapter(Context context, ArrayList<String> alBeaconInfo) {
        this.context = context;
        this.alBeaconInfo = alBeaconInfo;
        if (alBeaconInfo.size() >= 1) {
            for (int i = 0; i < alBeaconInfo.size(); i++) {
                String[] alBeaconStringArray = alBeaconInfo.get(i).split(",");
                alTagID.add(Integer.parseInt(alBeaconStringArray[0]));
                alName.add(alBeaconStringArray[1]);
                alDesignation.add(alBeaconStringArray[2]);
                alNumber.add(alBeaconStringArray[3]);
                alEmail.add(alBeaconStringArray[4]);
                alDate.add(alBeaconStringArray[5]);
                alTime.add(alBeaconStringArray[6]);
                alLastSeenTime.add(alBeaconStringArray[7]);
            }
        }

    }


    @Override
    public RecentTabHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_recent, parent, false);

        /*accessing floating action button of MainActivity by just using tag that is set to the fab button
        accessing the fab using View mainView
        */
        View mainView = parent.getRootView().findViewWithTag("fab_mainActivity");
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                *nALSelectedItemIndex contains all the selected index that needs to be removed.
                *nALIndexToRemove is again initialized so that we will get all the selected index numbers. store in it &
                    then send it to collection and deleting.
                 */
                //ArrayList<Integer> nALIndexToRemove = new ArrayList<>();
                ArrayList<Integer> nALTagID = new ArrayList<>();
                for (int i = 0; i < nALSelectedItemIndex.size(); i++) {
                    //nALIndexToRemove.add(nALSelectedItemIndex.get(i));
                    nALTagID.add(alTagID.get(nALSelectedItemIndex.get(i)));
                }

                /*all the data of nALIndexToRemove will be arranged in descending order so that we wont get
                        index out of bound exception & data will be deleted from the higher to lower index.
                 *for loop deletes all the index position one by one and notify it to adapter view notifyItemRemoved()
                        and certain elements needs to be cleared from simple service too. So we are calling a method
                        of Simple Service which deletes required elements from the arraylist and Hashset, so that
                        next time if the same beacon detects it will be again listed in the recent menu.
                 */
                Collections.sort(nALSelectedItemIndex, Collections.reverseOrder());

                // ArrayList<Integer> alIndexToRemove = new ArrayList<>();
                for (int j = 0; j < nALSelectedItemIndex.size(); j++) {
                    int indexToRemove = nALSelectedItemIndex.get(j);

                    alBeaconInfo.remove(indexToRemove);
                    //alIndexToRemove.add(indexToRemove);
                    notifyItemRemoved(indexToRemove);
                }
                SimpleService.refofservice.deleteFromRecent(nALTagID);
                nALTagID.clear();
                nALSelectedItemIndex.clear();
                //nALIndexToRemove.clear();
            }
        });
        /*
        setting tag of this view so that this can be accessible from anywhere of this app
        this has been accessed in MainActivity by just using tag
        */
        view.setTag("recycler_view_recent");
        return new RecentTabHolder(view);
    }

    @Override
    public void onBindViewHolder(RecentTabHolder holder, final int position) {
        /*
        below drawable and bitmap is used to derive the size of drawable of TextView of recyclerView
         */
        Drawable drawableEmail = ResourcesCompat.getDrawable(context.getResources(), R.drawable.tp_mail_icon, null);
        Bitmap bitmapEmail = null;
        if (drawableEmail != null) {
            bitmapEmail = ((BitmapDrawable) drawableEmail).getBitmap();
        }
        Drawable drawableEmailBitmap = new BitmapDrawable(context.getApplicationContext().getResources(), Bitmap.createScaledBitmap(bitmapEmail, 40, 40, true));
        //setting the above drawable bitmap to the textview
        holder.tvRVEmail.setCompoundDrawablesWithIntrinsicBounds(drawableEmailBitmap, null, null, null);
        holder.tvRVEmail.setText(alEmail.get(position));

        Drawable drawableLastSeen = ResourcesCompat.getDrawable(context.getResources(), R.drawable.tp_time_icon, null);
        Bitmap bitmapLastSeen = null;
        if (drawableLastSeen != null) {
            bitmapLastSeen = ((BitmapDrawable) drawableLastSeen).getBitmap();
        }
        Drawable drawableLastSeenBitmap = new BitmapDrawable(context.getApplicationContext().getResources(), Bitmap.createScaledBitmap(bitmapLastSeen, 40, 40, true));
        //setting the above drawable bitmap to the textview
        holder.tvRVLastSeenTime.setCompoundDrawablesWithIntrinsicBounds(drawableLastSeenBitmap, null, null, null);

        Drawable drawableNumber = ResourcesCompat.getDrawable(context.getResources(), R.drawable.tp_call_icon, null);
        Bitmap bitmapNumber = null;
        if (drawableNumber != null) {
            bitmapNumber = ((BitmapDrawable) drawableNumber).getBitmap();
        }
        Drawable drawableNumberBitmap = new BitmapDrawable(context.getApplicationContext().getResources(), Bitmap.createScaledBitmap(bitmapNumber, 40, 40, true));
        //setting the above drawable bitmap to the textview
        holder.tvRVNumber.setCompoundDrawablesWithIntrinsicBounds(drawableNumberBitmap, null, null, null);
        holder.mImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.vj, null));

        holder.tvRVNumber.setText(alNumber.get(position));

        holder.tvRvName.setText(alName.get(position));

        holder.tvRvDesignation.setText(alDesignation.get(position));

        /*Glide.with(holder.mImageView.getContext())
                .load(Cheeses.getRandomCheeseDrawable())
                .fitCenter()
                .into(holder.mImageView);*/
        //holder.mImageView.setImageDrawable(context.getApplicationContext().getResources().getDrawable(R.drawable.vj));


        holder.tvRVDate.setText(alDate.get(position));
        holder.tvRVTime.setText(alTime.get(position));

        if (alLastSeenTime != null && alLastSeenTime.size() >= 1 && alLastSeenTime.get(position) != null
                && !alLastSeenTime.get(position).equals("null"))
            holder.tvRVLastSeenTime.setText(alLastSeenTime.get(position));

        /*below code sets the background of view to the selected color
          when new data is added to the list onLongPress will lose the selected item. So it is stored to hashset and
          retrieved here to set background onLongPress.
         */
        if (nALSelectedItemIndex.contains(holder.getAdapterPosition())) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardview_light_background));
        }
    }

    @Override
    public int getItemCount() {
        return alBeaconInfo.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class RecentTabHolder extends RecyclerView.ViewHolder {
        TextView tvRVEmail;
        TextView tvRVLastSeenTime;
        TextView tvRVDate;
        TextView tvRVTime;
        TextView tvRVNumber;
        ImageView mImageView;
        TextView tvRvName;
        TextView tvRvDesignation;

        RecentTabHolder(View itemView) {
            super(itemView);
            tvRVEmail = (TextView) itemView.findViewById(R.id.recent_rc_email);
            tvRVLastSeenTime = (TextView) itemView.findViewById(R.id.recent_rc_last_seen);
            tvRVNumber = (TextView) itemView.findViewById(R.id.recent_rc_number);
            tvRvName = (TextView) itemView.findViewById(R.id.recent_rc_name);
            tvRvDesignation = (TextView) itemView.findViewById(R.id.recent_rc_designation);
            tvRVDate = (TextView) itemView.findViewById(R.id.recent_rc_date);
            tvRVTime = (TextView) itemView.findViewById(R.id.recent_rc_time);

            mImageView = (ImageView) itemView.findViewById(R.id.recent_rc_avatar);

        }
    }

    /*
    using interface ClickListener, we will be able to perform tasks like single click and long press selection
    of Recyclerview elements.
     */
    interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    /*
    below RecyclerTouchListener class is created to listen to longPress and singleTap
     */

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;


        RecyclerTouchListener(final Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    /*
                    below code works same as whatsApp list selection
                    below code works only when at least one onLongPress is triggered and the position is added to the hashset from onLongPress listener
                     */
                    //adds the view of the recyclerView
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    //gets the index of recyclerView
                    int index = recycleView.getChildAdapterPosition(child);
                    /*
                    below if statement checks for 2 condition.
                    below code executes when there is atleast one value in hashset and the index shouldn't be already added to the hashset
                    it sets the background to grey(which means selected) and the same will be added to hashset
                    else statement executes when the first if statement doesn't meet the conditions
                    which sets the background to white (which means unselected)
                     */
                    if (nALSelectedItemIndex.size() >= 1 && !nALSelectedItemIndex.contains(index)) {
                        child.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey));
                        nALSelectedItemIndex.add(index);
                    } else {
                        child.setBackgroundColor(ContextCompat.getColor(context, R.color.cardview_light_background));
                        if (nALSelectedItemIndex.size() >= 1) {
                            /*
                            index only holds the current position. so before we remove anything from arraylist. We will first check if we have the index number
                            in the arraylist and incase if it is present, we will add the index of the selected position into nRemoveIndex and then delete
                            that position
                             */
                            int nRemoveIndex = nALSelectedItemIndex.indexOf(index);
                            nALSelectedItemIndex.remove(nRemoveIndex);
                        }
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                        int index = recycleView.getChildAdapterPosition(child);
                        /*
                        below if statement checks for one condition.
                        if hashset doesn't contain the position already the background is set to grey(which means selected) as well as added to the hashset
                        else
                        it removes the position from the hashset and sets the background to white(which means unselected)
                         */
                        if (!nALSelectedItemIndex.contains(index)) {
                            nALSelectedItemIndex.add(index);
                            child.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey));
                        } else {
                            if (nALSelectedItemIndex.size() >= 1 & nALSelectedItemIndex.contains(index)) {
                                child.setBackgroundColor(ContextCompat.getColor(context, R.color.cardview_light_background));
                                int nRemoveIndex = nALSelectedItemIndex.indexOf(index);
                                nALSelectedItemIndex.remove(nRemoveIndex);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}
