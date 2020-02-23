package app_utility;

/*
 * Created by Vj on 23-May-17.
 */

import java.util.ArrayList;

public interface ServiceInterface {
    void onServiceCallBack(String TAG, Object data, boolean isCalled, ArrayList<ArrayList<String>> arrayList);

    void onScanningDialogCallBack(String TAG, Object data, boolean isCalled);
}
