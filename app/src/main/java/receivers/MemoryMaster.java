package receivers;

/*
 * Created by Vj on 08-May-17.
 */

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.util.Log;

/*
This class helps to identify whether app is in background.
call back is registered in ScanningFragment for bluetooth is switched on or not purpose.
refer: http://stackoverflow.com/questions/4414171/how-to-detect-when-an-android-app-goes-to-the-background-and-come-back-to-the-fo
or
from downloaded web page.
 */

public class MemoryMaster implements ComponentCallbacks2{
    @Override
    public void onTrimMemory(int level) {
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // We're in the Background
            Log.e("background", "App is in Background");
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
}
