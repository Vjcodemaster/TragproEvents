package scanning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sspl.org.tp.R;

import java.util.ArrayList;


public class AllFragment extends Fragment {
    private RecyclerView recyclerView;

    public AllTabRecyclerVAdapter recyclerVAdapter;

    DatabaseHandler db;

    ArrayList<DataBaseHelper> dbData;

    IntentFilter intentFilterData, intentFilterTime;
    BroadcastReceiver broadcastReceiverData, broadcastReceiverTime;

    public AllFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        db = new DatabaseHandler(getActivity());
        dbData = new ArrayList<>(db.getAllTabData());
        recyclerVAdapter = new AllTabRecyclerVAdapter(getActivity(), dbData);


        intentFilterData = new IntentFilter("android.intent.action.RECYCLER_NOTIFY");
        broadcastReceiverData = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                dbData = new ArrayList<>(db.getAllTabData());
                testWithAlDataBase();
            }
        };
        getActivity().registerReceiver(broadcastReceiverData, intentFilterData);

        intentFilterTime = new IntentFilter("android.intent.action.UPDATETIME");
        broadcastReceiverTime = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int position = intent.getExtras().getInt("Index");

                dbData = new ArrayList<>(db.getAllTabData());

                //testWithAlDataBase();
                updateRecyclerViewTime(dbData, position);
            }
        };
        getActivity().registerReceiver(broadcastReceiverTime, intentFilterTime);
    }

    @Override
    public void onStop() {
        if (broadcastReceiverData != null) {
            getActivity().unregisterReceiver(broadcastReceiverData);
            broadcastReceiverData = null;
        }

        if (broadcastReceiverTime != null) {
            getActivity().unregisterReceiver(broadcastReceiverTime);
            broadcastReceiverTime = null;
        }
        super.onStop();
    }


    @Override
    public void onDestroy() {
        if (broadcastReceiverData != null) {
            getActivity().unregisterReceiver(broadcastReceiverData);
            broadcastReceiverData = null;
        }

        if (broadcastReceiverTime != null) {
            getActivity().unregisterReceiver(broadcastReceiverTime);
            broadcastReceiverTime = null;
        }

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiverData, intentFilterData);
        getActivity().registerReceiver(broadcastReceiverTime, intentFilterTime);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.allTab_recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setTag("allTab_recyclerView");
        recyclerView.setAdapter(recyclerVAdapter);
        return view;
    }

    public void testWithAlDataBase() {
        recyclerVAdapter = new AllTabRecyclerVAdapter(getActivity().getApplicationContext(), dbData);
        recyclerView.setAdapter(recyclerVAdapter);
    }

    public void updateRecyclerViewTime(ArrayList<DataBaseHelper> dbData, int position) {
        try {
            recyclerVAdapter = new AllTabRecyclerVAdapter(getActivity(), dbData);
            int pos = position - 1;
            View v = recyclerView.getChildAt(pos);
            if (v.isShown()) {
                TextView t = (TextView) v.findViewById(R.id.all_rc_last_seen);
                String sLastSeen = dbData.get(pos).getLastSeen();
                t.setText(sLastSeen);
            } else {
                recyclerVAdapter.notifyItemChanged(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
