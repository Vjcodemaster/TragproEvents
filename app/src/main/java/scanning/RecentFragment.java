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

import com.sspl.org.tp.R;

import java.util.ArrayList;


public class RecentFragment extends Fragment {

    private RecyclerView recyclerView;

    public RecentTabRecyclerVAdapter recyclerVAdapter;


    ArrayList<String> alBeaconInfo = new ArrayList<>();

    IntentFilter intentFilterData, intentFilterTime;
    BroadcastReceiver broadcastReceiverData, broadcastReceiverTime;

    int nScrollIndex = 0;

    DatabaseHandler db;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*db = new DatabaseHandler(getActivity());


        intentFilterData = new IntentFilter("android.intent.action.DataBaseHelper.RECYCLER_NOTIFY");
        broadcastReceiverData = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                alBeaconInfo = intent.getExtras().getStringArrayList("alBeaconInfo");
                testWithAlDataBase(alBeaconInfo);
            }
        };
        getActivity().registerReceiver(broadcastReceiverData, intentFilterData);

        intentFilterTime = new IntentFilter("android.intent.action.DataBaseHelper.UPDATETIME");
        broadcastReceiverTime = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int position = intent.getExtras().getInt("Index");
                String sLastSeen = intent.getExtras().getString("LastSeen");
                alBeaconInfo = (intent.getExtras().getStringArrayList("alBeaconInfo"));

                //testWithAlDataBase();
                updateRecyclerViewTime(alBeaconInfo, position, sLastSeen);
            }
        };
        getActivity().registerReceiver(broadcastReceiverTime, intentFilterTime);*/
    }

    @Override
    public void onStart(){
        super.onStart();
        db = new DatabaseHandler(getActivity());


        intentFilterData = new IntentFilter("android.intent.action.DataBaseHelper.RECYCLER_NOTIFY");
        broadcastReceiverData = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                alBeaconInfo = intent.getExtras().getStringArrayList("alBeaconInfo");
                testWithAlDataBase(alBeaconInfo);
            }
        };
        getActivity().registerReceiver(broadcastReceiverData, intentFilterData);

        intentFilterTime = new IntentFilter("android.intent.action.DataBaseHelper.UPDATETIME");
        broadcastReceiverTime = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int position = intent.getExtras().getInt("Index");
                String sLastSeen = intent.getExtras().getString("LastSeen");

                alBeaconInfo.clear();
                alBeaconInfo = (intent.getExtras().getStringArrayList("alBeaconInfo"));

                //testWithAlDataBase();
                updateRecyclerViewTime(position, sLastSeen, alBeaconInfo);
            }
        };
        getActivity().registerReceiver(broadcastReceiverTime, intentFilterTime);
    }

    public RecentFragment() {

    }

    @Override
    public void onStop(){
         /*
        unregistering receivers to avoid memory leak
         */
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
        /*
        unregistering receivers to avoid memory leak
         */
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
        /*
        registering back receivers to start receiving broadcast
         */
        getActivity().registerReceiver(broadcastReceiverData, intentFilterData);
        getActivity().registerReceiver(broadcastReceiverTime, intentFilterTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);



        recyclerView = (RecyclerView) view.findViewById(R.id.recentTab_recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setTag("recentTab_recyclerView");

        recyclerView.addOnItemTouchListener(new RecentTabRecyclerVAdapter.RecyclerTouchListener(getActivity(),
                recyclerView, new RecentTabRecyclerVAdapter.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                nScrollIndex = position;
                //Toast.makeText(getActivity(), "Single Click on position        :" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                nScrollIndex = position;
                //Toast.makeText(getActivity(), "Long press on position :" + position, Toast.LENGTH_SHORT).show();
            }
        }));

        return view;
        //return view;
    }


    public void updateRecyclerViewTime(int position, String sLastSeen, final ArrayList<String> alBeaconInfo) {
        try {
            /*recyclerVAdapter = new RecentTabRecyclerVAdapter(getActivity().getApplicationContext(), dataStorage.alFirstName,
                    dataStorage.alMobileNumber, dataStorage.alEmail, dataStorage.alDesignation);*/
            //recyclerVAdapter = new RecentTabRecyclerVAdapter(getActivity().getApplicationContext(), alName, alLastSeenTime, alPositionTime);
            //recyclerVAdapter = new RecentTabRecyclerVAdapter(getActivity(), alBeaconInfo);
            //int pos = position - 1;
            /*View v = recyclerView.getChildAt(position);
            if (v != null && v.isShown()) {
                TextView t = (TextView) v.findViewById(R.id.recent_rc_last_seen);
                //String sLastSeen = dbData.get(pos).getLastSeen();
                t.setText(sLastSeen);
            }else {
                recyclerVAdapter.notifyItemChanged(position);
            }*/
            recyclerVAdapter = new RecentTabRecyclerVAdapter(getActivity(), alBeaconInfo);
            recyclerView.setAdapter(recyclerVAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testWithAlDataBase(ArrayList<String> alBeaconInfo) {
        try {
            if (alBeaconInfo.size() >= 1) {
                recyclerVAdapter = new RecentTabRecyclerVAdapter(getActivity().getApplicationContext(), alBeaconInfo);
                recyclerView.setAdapter(recyclerVAdapter);
                if (nScrollIndex != 0 && nScrollIndex != -1) {
                    recyclerView.smoothScrollToPosition(nScrollIndex - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
