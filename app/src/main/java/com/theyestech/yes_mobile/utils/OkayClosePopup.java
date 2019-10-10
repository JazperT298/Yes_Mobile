package com.theyestech.yes_mobile.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;

public class OkayClosePopup {

    public static void showDialog(Context context, String message, String label) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dialog_okay_close, null);
        final TextView tvMessage;
        final Button btnOkay;

        tvMessage = dialogView.findViewById(R.id.tv_DialogOkayMessage);
        btnOkay = dialogView.findViewById(R.id.btn_DialogOkayOkay);

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);

        tvMessage.setText(message);
        btnOkay.setText(label);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
            }
        });

        b.show();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
