package dialogs;

/*
 * Created by Vj on 08-May-17.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.sspl.org.tp.R;

import app_utility.OnFragmentInteractionListener;

public class AlertDialogs {
    private Activity aActivity;
    private int nCaseToExecute;

    private OnFragmentInteractionListener mListener;

    public AlertDialogs(Activity aActivity, int nCaseToExecute, OnFragmentInteractionListener mListener) {
        this.aActivity = aActivity;
        this.nCaseToExecute = nCaseToExecute;
        this.mListener = mListener;
        showDialog(nCaseToExecute);
    }

    private String showDialog(int nCaseToExecute) {
        switch (nCaseToExecute) {
            case 0:
                showBluetoothDialog();
                break;
            case 1:
                showLocationPermissionExplanation();
                break;
            case 2:
                showEnableDeviceLocation();
                break;
        }
        return null;
    }

    private void showBluetoothDialog() {
        AlertDialog.Builder alertBluetooth = new AlertDialog.Builder(aActivity);
        alertBluetooth.setMessage(aActivity.getResources().getString(R.string.bluetooth_message))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentBluetooth = new Intent(Intent.ACTION_MAIN, null);
                        intentBluetooth.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn = new ComponentName("com.android.settings",
                                "com.android.settings.bluetooth.BluetoothSettings");
                        intentBluetooth.setComponent(cn);
                        intentBluetooth.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        aActivity.startActivity(intentBluetooth);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        aActivity.onBackPressed();
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void showLocationPermissionExplanation() {
        AlertDialog.Builder alertBluetooth = new AlertDialog.Builder(aActivity);
        alertBluetooth.setMessage(aActivity.getResources().getString(R.string.location_explanation))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", aActivity.getPackageName(), null);
                        intent.setData(uri);
                        aActivity.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        aActivity.onBackPressed();
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void showEnableDeviceLocation() {
        AlertDialog.Builder alertBluetooth = new AlertDialog.Builder(aActivity);
        alertBluetooth.setMessage(aActivity.getResources().getString(R.string.device_location_message))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onFragmentMessage("REQUEST_LOCATION", aActivity.getString(R.string.app_name), false);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        aActivity.onBackPressed();
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
