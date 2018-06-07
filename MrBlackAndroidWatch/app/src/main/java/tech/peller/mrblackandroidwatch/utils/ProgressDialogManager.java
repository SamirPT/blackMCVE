package tech.peller.mrblackandroidwatch.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;

import tech.peller.mrblackandroidwatch.R;

/**
 * Created by Sam (samir@peller.tech) on 08.11.2016
 */

public class ProgressDialogManager {
    private static ProgressDialog progressDialog;

    public ProgressDialogManager() {}

    public static void startProgress(Activity context) {
        if(progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
            progressDialog.setCancelable(false);
            if (progressDialog != null && progressDialog.getWindow() != null) {
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            progressDialog.setContentView(R.layout.progress_dialog);
        }
    }

    public static void stopProgress() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
