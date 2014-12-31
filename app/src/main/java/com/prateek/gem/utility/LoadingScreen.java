package com.prateek.gem.utility;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.internal.co;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.widgets.MyProgressDialog;

/**
 * Created by prateek on 12/12/14.
 */
public class LoadingScreen {

    private static MyProgressDialog progressDialog = null;

    /**
     * method to show dialog
     * @param context
     * @param dialogText
     */
    public static void showLoading(Context context, String dialogText) {
        if(context == null) {
            DebugLogger.message("Can not show the progress dialog");
            return;
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                DebugLogger.message("Can not show the progress dialog");
                return;
            }
        }

        dismissProgressDialog();

        if(progressDialog == null) {
            progressDialog = new MyProgressDialog(context,dialogText);
        }

        DebugLogger.message("Showing Progress Dialog");
        progressDialog.show();

    }

    /**
     * Method is to update the indicator message
     *
     * @param message
     */
    public static void updateIndicatorMessage(String message) {
        if (progressDialog != null) {
            DebugLogger.message("Updating indicator message::"+message);
            progressDialog.setMessage(message);
        }
    }

    /**
     * method to dismiss dialog
     */
    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
                DebugLogger.error("LoadingScreen :: dismissProgressDialog :: e : " + e);
            }
            progressDialog = null;
        }
    }
}
