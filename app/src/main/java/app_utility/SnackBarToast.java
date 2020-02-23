package app_utility;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.sspl.org.tp.R;


public class SnackBarToast extends AsyncTask<Integer, Void, String> {

    private Context context;
    private View snackBarView;
    private String snackbarMessage="";
    private Snackbar snackbar;


    public SnackBarToast(Context context, View snackBarView, Snackbar snackbar, String snackbarMessage){
        this.context = context;
        this.snackBarView = snackBarView;
        this.snackbar = snackbar;
        this.snackbarMessage = snackbarMessage;
    }

    //shows snackbar if there is no Internet connection
    private void noInternetConnectivity() {
        snackBarView.setBackgroundColor(Color.RED);
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.white));
        snackbarMessage = context.getResources().getString(R.string.no_internet_connection);
        tv.setText(snackbarMessage);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    private void invalidUserNamePassword() {
        snackBarView.setBackgroundColor(Color.RED);
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.white));
        snackbarMessage = context.getResources().getString(R.string.invalid_username_password);
        tv.setText(snackbarMessage);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    private void userDoesNotExist() {
        snackBarView.setBackgroundColor(Color.RED);
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.white));
        snackbarMessage = context.getResources().getString(R.string.user_does_not_exist);
        tv.setText(snackbarMessage);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    public void userIDPasswordMismatch() {
        snackBarView.setBackgroundColor(Color.RED);
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.white));
        snackbarMessage = context.getResources().getString(R.string.user_id_password_mismatch);
        tv.setText(snackbarMessage);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    private void cannotContactServer() {
        snackBarView.setBackgroundColor(Color.DKGRAY);
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.white));
        snackbarMessage = context.getResources().getString(R.string.unable_contact_server);
        tv.setText(snackbarMessage);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    @Override
    protected String doInBackground(Integer... params) {
        int type = params[0];
        switch (type) {
            case 1:
                noInternetConnectivity();
                break;
            case 2:
                invalidUserNamePassword();
                break;
            case 3:
                userDoesNotExist();
                break;
            case 4:
                cannotContactServer();
                break;
        }
        return null;
    }
}
