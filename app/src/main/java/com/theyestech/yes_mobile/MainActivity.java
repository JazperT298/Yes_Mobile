package com.theyestech.yes_mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.activities.StartActivity;
import com.theyestech.yes_mobile.dialogs.OkayClosePopup;
import com.theyestech.yes_mobile.dialogs.ProgressPopup;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_chat, R.id.navigation_rank, R.id.navigation_notification, R.id.navigation_menu)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        context = this;
        role = UserRole.getRole(context);

        checkUserRole();
    }

    private void checkUserRole() {
        if (role.isEmpty()) {
            Intent intent = new Intent(context, StartActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (role.equals(UserRole.Educator())) {
                checkEducatorSession();
            } else {
                checkStudentSession();
            }
        }
    }

    public void checkEducatorSession() {
        if (!UserRole.getRole(context).isEmpty()) {
            if (UserEducator.getToken(context) == null) {
                Intent intent = new Intent(context, StartActivity.class);
                startActivity(intent);
                finish();
            } else {
                loginEducator();
            }
        } else {
            Intent intent = new Intent(context, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void checkStudentSession() {

    }

    private void loginStudent() {

    }

    private void loginEducator() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("login_e_email_address", UserEducator.getEmail(context));
        params.put("login_e_password", UserEducator.getPassword(context));

        HttpProvider.post(context, "controller_educator/login_as_educator_class.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                try {
                    String str = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String result = jsonObject.getString("result");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

}
