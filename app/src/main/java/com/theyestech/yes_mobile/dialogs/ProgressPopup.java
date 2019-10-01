package com.theyestech.yes_mobile.dialogs;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressPopup {

    private static ProgressDialog progressDialog = null;

    public static ProgressDialog showProgress(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        return progressDialog;
    }

    public static void hideProgress() {
        progressDialog.hide();
    }

}
