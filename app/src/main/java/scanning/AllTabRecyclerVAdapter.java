package scanning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.sspl.org.tp.R;

import java.util.ArrayList;


class AllTabRecyclerVAdapter extends RecyclerView.Adapter<AllTabRecyclerVAdapter.AllTabHolder> {
    private Context context;

    private ArrayList<String> alName = new ArrayList<>();
    private ArrayList<String> alNumber = new ArrayList<>();
    private ArrayList<String> alEmail = new ArrayList<>();
    private ArrayList<String> alDesignation = new ArrayList<>();
    private ArrayList<String> alDate = new ArrayList<>();
    private ArrayList<String> alTime = new ArrayList<>();
    private ArrayList<String> alLastSeenTime = new ArrayList<>();

    /*public AllTabRecyclerVAdapter(){

    }

    public AllTabRecyclerVAdapter(Context context, int position, ArrayList<DataBaseHelper> alDataBase) {
        this.context = context;
        this.position = position;
        this.alDataBase = alDataBase;
        if (alDataBase.size() >= 1) {
            for (int i = 0; i < alDataBase.size(); i++) {
                alName.add(alDataBase.get(i).getName());
                alNumber.add(alDataBase.get(i).getPhoneNumber());
                alEmail.add(alDataBase.get(i).getEmailID());
                alDesignation.add(alDataBase.get(i).getDesignation());
                alDate.add(alDataBase.get(i).getDate());
                alTime.add(alDataBase.get(i).getTime());
                alLastSeenTime.add(alDataBase.get(i).getLastSeen());
                //String sTagId = alDataBase.get(i).getTagID();
                //String time = alDataBase.get(i).getTime();
                //notifyItemChanged(i);
            }
        }
        notifyItemChanged(position);
    }*/

    AllTabRecyclerVAdapter(Context context, ArrayList<DataBaseHelper> alDataBase) {
        this.context = context;
        if (alDataBase.size() >= 1) {
            for (int i = 0; i < alDataBase.size(); i++) {
                alName.add(alDataBase.get(i).getName());
                alNumber.add(alDataBase.get(i).getPhoneNumber());
                alEmail.add(alDataBase.get(i).getEmailID());
                alDesignation.add(alDataBase.get(i).getDesignation());
                alDate.add(alDataBase.get(i).getDate());
                alTime.add(alDataBase.get(i).getTime());
                alLastSeenTime.add(alDataBase.get(i).getLastSeen());
            }
        }
    }

    @Override
    public AllTabRecyclerVAdapter.AllTabHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_all, parent, false);

        view.setTag("all_view_recent");
        return new AllTabHolder(view);
    }


    @Override
    public void onBindViewHolder(AllTabHolder holder, int position) {
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
        if (alEmail.size() >= 1) {
            holder.tvRVEmail.setText(alEmail.get(position));
        }

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

        if (alNumber.size() >= 1) {
            holder.tvRVNumber.setText(alNumber.get(position));

            holder.tvRvName.setText(alName.get(position));

            holder.tvRvDesignation.setText(alDesignation.get(position));

            holder.tvRVDate.setText(alDate.get(position));
            holder.tvRVTime.setText(alTime.get(position));
        }

            holder.tvRVLastSeenTime.setText(alLastSeenTime.get(position));
    }

    @Override
    public int getItemCount() {
        return alName.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    static class AllTabHolder extends RecyclerView.ViewHolder {
        TextView tvRVEmail;
        TextView tvRVLastSeenTime;
        TextView tvRVDate;
        TextView tvRVTime;
        TextView tvRVNumber;
        ImageView mImageView;
        TextView tvRvName;
        TextView tvRvDesignation;

        AllTabHolder(View itemView) {
            super(itemView);
            tvRVEmail = (TextView) itemView.findViewById(R.id.all_rc_email);
            tvRVLastSeenTime = (TextView) itemView.findViewById(R.id.all_rc_last_seen);
            tvRVNumber = (TextView) itemView.findViewById(R.id.all_rc_number);
            tvRvName = (TextView) itemView.findViewById(R.id.all_rc_name);
            tvRvDesignation = (TextView) itemView.findViewById(R.id.all_rc_designation);
            tvRVDate = (TextView) itemView.findViewById(R.id.all_rc_date);
            tvRVTime = (TextView) itemView.findViewById(R.id.all_rc_time);

            mImageView = (ImageView) itemView.findViewById(R.id.all_rc_avatar);
        }
    }
}
