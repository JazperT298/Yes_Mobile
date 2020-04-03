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

import com.theyestech.yes_mobile.activities.StartActivity;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.UserRole;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_chat, R.id.navigation_rank, R.id.navigation_notification, R.id.navigation_menu)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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
            if (UserEducator.getToken(context).isEmpty()) {
                Intent intent = new Intent(context, StartActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(context, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void checkStudentSession() {
        if (!UserRole.getRole(context).isEmpty()) {
            if (UserStudent.getToken(context).isEmpty()) {
                Intent intent = new Intent(context, StartActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(context, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
