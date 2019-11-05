package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.QuizzesAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Quiz;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectQuizzesActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Quiz> quizArrayList = new ArrayList<>();
    private QuizzesAdapter quizzesAdapter;
    private Quiz selectedQuiz = new Quiz();

    private Subject subject;

    private String title, items, time, type;
    private ArrayList<String> sType = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_quizzes);

        subject = getIntent().getParcelableExtra("SUBJECT");
        Debugger.logD("SUBJECT QUIZ: " + subject.getId());

        sType.add("Multiple");
        sType.add("TrueOrFalse");
        sType.add("Enumeration");
        sType.add("Identification");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI() {
        ivBack = findViewById(R.id.iv_SubjectQuizBack);
        swipeRefreshLayout = findViewById(R.id.swipe_Quiz);
        recyclerView = findViewById(R.id.rv_Quizzes);
        floatingActionButton = findViewById(R.id.fab_SubjectQuizAdd);
        emptyIndicator = findViewById(R.id.view_Empty);

        if (!role.equals(UserRole.Educator()))
            floatingActionButton.setVisibility(View.GONE);

        swipeRefreshLayout.setRefreshing(true);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQuizzesDetails();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddQuizDialog();
            }
        });
    }

    private void getQuizzesDetails() {
        quizArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);
        floatingActionButton.setEnabled(false);

        RequestParams params = new RequestParams();
        params.put("teach_id", UserEducator.getID(context));
        params.put("subj_id", subject.getId());

        HttpProvider.post(context, "controller_educator/get_quizzes_subject.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                floatingActionButton.setEnabled(true);
                String str = new String(responseBody, StandardCharsets.UTF_8);

                Debugger.logD(str);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {

                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("QUIZ: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String quiz_id = jsonObject.getString("quiz_id");
                        String subject_id = jsonObject.getString("subject_id");
                        String user_id = jsonObject.getString("user_id");
                        String quiz_title = jsonObject.getString("quiz_title");
                        String quiz_type = jsonObject.getString("quiz_type");
                        String quiz_item = jsonObject.getString("quiz_item");
                        String quiz_time = jsonObject.getString("quiz_time");
                        String quiz_image = jsonObject.getString("image");

                        Quiz quiz = new Quiz();
                        quiz.setQuiz_id(quiz_id);
                        quiz.setSubject_id(subject_id);
                        quiz.setUser_id(user_id);
                        quiz.setQuiz_title(quiz_title);
                        quiz.setQuiz_type(quiz_type);
                        quiz.setQuiz_item(quiz_item);
                        quiz.setQuiz_time(quiz_time);
                        quiz.setQuiz_image(quiz_image.substring(1));

                        quizArrayList.add(quiz);
                    }

                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                    recyclerView.setHasFixedSize(true);
                    quizzesAdapter = new QuizzesAdapter(context, quizArrayList);
                    quizzesAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position) {
                            if (role.equals(UserRole.Educator())) {
                                selectedQuiz = quizArrayList.get(position);
                                Intent intent = new Intent(context, SubjectQuizQuestionsActivity.class);
                                intent.putExtra("QUIZ", selectedQuiz);
                                startActivity(intent);
                            } else {
                                selectedQuiz = quizArrayList.get(position);
                                Intent intent = new Intent(context, SubjectQuizzesTakeActivity.class);
                                intent.putExtra("QUIZ", selectedQuiz);
                                startActivity(intent);
                            }
                        }
                    });

                    recyclerView.setAdapter(quizzesAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                floatingActionButton.setEnabled(true);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void saveQuiz() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("subj_id", subject.getId());
        params.put("quiz_title", title);
        params.put("quiz_type", type);
        params.put("quiz_item", items);
        params.put("quiz_time", time);

        HttpProvider.post(context, "controller_educator/create_quiz.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();

                String str = new String(responseBody, StandardCharsets.UTF_8);

                try {
                    JSONArray jsonArray = new JSONArray(str);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String quiz_id = jsonObject.getString("quiz_id");
                    if (str.contains("success")) {
                        Toasty.success(context, "Saved.").show();
                        selectedQuiz = new Quiz();
                        selectedQuiz.setQuiz_id(quiz_id);
                        selectedQuiz.setQuiz_title(title);
                        selectedQuiz.setQuiz_type(type);
                        selectedQuiz.setQuiz_item(items);
                        selectedQuiz.setQuiz_time(time);
                        Intent intent = new Intent(context, SubjectQuizQuestionsActivity.class);
                        intent.putExtra("QUIZ", selectedQuiz);
                        startActivity(intent);

                    } else
                        Toasty.warning(context, "Failed").show();

                    getQuizzesDetails();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Debugger.logD("ERROR: " + e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void openAddQuizDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_quiz, null);
        final EditText etTitle, etItems, etTime;
        final MaterialSpinner spType;
        final Button btnSave;
        final ImageView ivClose;

        etTitle = dialogView.findViewById(R.id.et_AddQuizTitle);
        etTime = dialogView.findViewById(R.id.et_AddQuizTime);
        etItems = dialogView.findViewById(R.id.et_AddQuizItems);
        spType = dialogView.findViewById(R.id.sp_AddQuizType);
        btnSave = dialogView.findViewById(R.id.btn_AddQuizSave);
        ivClose = dialogView.findViewById(R.id.iv_AddQuizClose);

        type = "Multiple";
        spType.setItems(sType);
        spType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                type = sType.get(position);
            }
        });

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = etTitle.getText().toString();
                items = etItems.getText().toString();
                time = etTime.getText().toString();

                if (title.isEmpty()) {
                    Toasty.warning(context, "Please input quiz title.").show();
                    etTitle.requestFocus();
                } else {
                    if (items.isEmpty()) {
                        Toasty.warning(context, "Please input quiz items.").show();
                        etItems.requestFocus();
                    } else {
                        saveQuiz();
                        b.hide();
                    }
                }
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onResume() {
        super.onResume();

        getQuizzesDetails();
    }
}
