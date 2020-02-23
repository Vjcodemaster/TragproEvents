package receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import scanning.SimpleService;

public class BootCompleteReceiver extends BroadcastReceiver {

    @SuppressLint("NewApi") @Override
    public void onReceive(Context context, Intent intent) {
    	if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
    	{
        Intent service = new Intent(context, SimpleService.class);
        context.startService(service);   
    	}
    }
}
