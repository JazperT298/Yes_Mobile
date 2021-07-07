package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.utils.UserRole;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class MenuContactUsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView iv_ContactBack, imageViewFb,imageViewTw,imageVieIns;
    private Button btn_message;
    private EditText et_FullName, et_Email, et_Message;
    private TextView tv_copy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_contact_us);

        context = this;

        role = UserRole.getRole(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();
    }

    private void initializeUI(){
        iv_ContactBack = findViewById(R.id.iv_ContactBack);
        imageViewFb = findViewById(R.id.imageViewFb);
        imageViewTw = findViewById(R.id.imageViewTw);
        imageVieIns = findViewById(R.id.imageVieIns);
        btn_message = findViewById(R.id.btn_message);
        et_FullName = findViewById(R.id.et_FullName);
        et_Email = findViewById(R.id.et_Email);
        et_Message = findViewById(R.id.et_Message);
        tv_copy = findViewById(R.id.tv_copy);
        String text = "<font color=#C0C0C0>© Copyright 2019 All Rights Reserved by </font> <font color=#19c880>theyestech.com™</font>";
        tv_copy.setText(Html.fromHtml(text));

        iv_ContactBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageViewFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Yestech-104889414269548/"));
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Yestech-104889414269548/")));
                }
            }
        });
        imageViewTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.error(context, "Maintenance").show();
            }
        });
        imageVieIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/theyestech/"));
                    startActivity(intent);
                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/theyestech/")));
                }
            }
        });

        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_FullName.getText().toString();
                String email = et_Email.getText().toString();
                String message = et_Message.getText().toString();

                if (TextUtils.isEmpty(name)){
                    et_FullName.setError("Enter Your Name");
                    et_FullName.requestFocus();
                    return;
                }

                Boolean onError = false;
                if (!isValidEmail(email)) {
                    onError = true;
                    et_Email.setError("Invalid Email");
                    return;
                }

                if (TextUtils.isEmpty(message)){
                    et_Message.setError("Enter Your Message");
                    et_Message.requestFocus();
                    return;
                }

                Intent sendEmail = new Intent(android.content.Intent.ACTION_SEND);

                /* Fill it with Data */
                sendEmail.setType("plain/text");
                sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"jaspertony.atillo@gmail.com"});
                sendEmail.putExtra(android.content.Intent.EXTRA_TEXT,
                        "name:"+name+'\n'+"Email ID : "+email+'\n'+"Message:"+'\n'+message);

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(sendEmail, "Send mail..."));
                et_FullName.setText("");
                et_Email.setText("");
                et_Message.setText("");
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        //Get a Tracker (should auto-report)

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    // validating email id

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
