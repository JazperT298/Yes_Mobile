package com.theyestech.yes_mobile.dialogs;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressPopup {

    private static ProgressDialog progressDialog = null;

    public static ProgressDialog showProgress(Context context, String title, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

        return progressDialog;
    }

    public static void hideProgress() {
        progressDialog.hide();
    }

}
