package scanning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class UIBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String in = intent.getAction();
        switch(in)
        {
            case "android.intent.action.RECYCLER_NOTIFY":
               /* Intent mainActivityIn = new Intent(context.getApplicationContext(), com.vj.beaconservice.ScanningFragment.class);
                mainActivityIn.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIn);*/
                //ScanningFragment.getInstance().isFromNotification = true;

                context.sendBroadcast(new Intent("UPDATE_RECYCLER"));
                break;
            case "android.intent.action.RECYCLER_NOTIFY_DATACHANGED":

                break;
        }

    }
}
