package com.theyestech.yes_mobile.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.ConnectionAdapter;
import com.theyestech.yes_mobile.adapters.StudentsAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Student;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ConnectionActivity extends AppCompatActivity {
    private Context context;
    private String role;

    private ImageView imageView35;

    private ConstraintLayout emptyIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_Connection;
    private FloatingActionButton fab_Connection;
    private ConnectionAdapter connectionAdapter;
    private ArrayList<UserEducator> userEducatorArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI(){
        imageView35 = findViewById(R.id.imageView35);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);
        swipeRefreshLayout = findViewById(R.id.swipe_Connections);
        rv_Connection = findViewById(R.id.rv_Connection);
        fab_Connection = findViewById(R.id.fab_Connection);
        imageView35.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emptyIndicator.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserConnection();
            }
        });
        getUserConnection();

    }
    private void getUserConnection(){
        userEducatorArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("user_id", UserEducator.getID(context));
        params.put("user_token", UserEducator.getToken(context));
        Debugger.logD("user_token " + UserEducator.getToken(context));
        Debugger.logD("user_id " + UserEducator.getID(context));

        HttpProvider.post(context, "controller_global/GetUserConnections.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                Debugger.logD("responseBody: " + responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("str: " + str);
                if (str.equals("") || str.equals("[]" )|| str.contains("NO RECORD FOUND"))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {
                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("STUDENTS: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String user_id = jsonObject.getString("user_id");
//                        String user_token = jsonObject.getString("user_token");
//                        String user_email_address = jsonObject.getString("user_email_address");
//                        String user_password = jsonObject.getString("user_password");
//                        String user_firstname = jsonObject.getString("user_firstname");
//                        String user_lastname = jsonObject.getString("user_lastname");
//                        String user_middlename = jsonObject.getString("user_middlename");
//                        String user_suffixes = jsonObject.getString("user_suffixes");
//                        String user_gender = jsonObject.getString("user_gender");
//                        String user_contact_number = jsonObject.getString("user_contact_number");
//                        String user_image = jsonObject.getString("user_image");
//                        String user_activation = jsonObject.getString("user_activation");
//                        String validated = jsonObject.getString("validated");
//
//                        Student student = new Student();
//                        student.setUser_id(user_id);
//                        student.setUser_token(user_token);
//                        student.setUser_email_address(user_email_address);
//                        student.setUser_password(user_password);
//                        student.setUser_firstname(user_firstname);
//                        student.setUser_lastname(user_lastname);
//                        student.setUser_middlename(user_middlename);
//                        student.setUser_suffixes(user_suffixes);
//                        student.setUser_gender(user_gender);
//                        student.setUser_contact_number(user_contact_number);
//                        student.setUser_image(user_image);
//                        student.setUser_activiation(user_activation);
//                        student.setUser_validated(validated);
//
//                        studentArrayList.add(student);
                    }

                    rv_Connection.setLayoutManager(new LinearLayoutManager(context));
                    rv_Connection.setHasFixedSize(true);
                    connectionAdapter = new ConnectionAdapter(context, userEducatorArrayList);
                    connectionAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
//                            student = studentArrayList.get(position);
//                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
//                            intent.putExtra("STUDENT", student);
//                            startActivity(intent);
                        }
                    });

                    rv_Connection.setAdapter(connectionAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }
}
