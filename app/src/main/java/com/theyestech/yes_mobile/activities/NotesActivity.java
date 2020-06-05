package com.theyestech.yes_mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.theyestech.yes_mobile.adapters.NotesAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Note;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class NotesActivity extends AppCompatActivity {

    private Context context;
    private String role;
    private String userToken;
    private String userId;

    private ImageView ivBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;
    private FloatingActionButton floatingActionButton;

    private ArrayList<Note> noteArrayList = new ArrayList<>();
    private NotesAdapter notesAdapter;
    private Note selectedNote = new Note();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorAmethyst));
//        }

        ivBack = findViewById(R.id.iv_NotesBack);
        swipeRefreshLayout = findViewById(R.id.swipe_Notes);
        recyclerView = findViewById(R.id.rv_Notes);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);
        floatingActionButton = findViewById(R.id.fab_Notes);

        if (role.equals(UserRole.Educator())) {
            userId = UserEducator.getID(context);
            userToken = UserEducator.getToken(context);
        } else {
            userId = UserStudent.getID(context);
            userToken = UserStudent.getToken(context);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllNotes();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNoteDialog();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAllNotes();
    }

    private void getAllNotes() {
        noteArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("user_token", userToken);
        params.put("user_id", userId);

        HttpProvider.post(context, "controller_global/GetUserNotes.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);

                String str = new String(responseBody, StandardCharsets.UTF_8);

                Debugger.logD(str);

                if (str.equals("") || str.contains("No notes available"))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {
                    JSONArray jsonArray = new JSONArray(str);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String notes_id = jsonObject.getString("notes_id");
                        String notes_userId = jsonObject.getString("notes_userId");
                        String notes_title = jsonObject.getString("notes_title");
                        String notes_url = jsonObject.getString("notes_url");
                        String notes_file = jsonObject.getString("notes_file");
                        String notes_type = jsonObject.getString("notes_type");
                        String result = jsonObject.getString("result");

                        Note note = new Note();
                        note.setId(notes_id);
                        note.setUserId(notes_userId);
                        note.setTitle(notes_title);
                        note.setUrl(notes_url);
                        note.setFile(notes_file);
                        note.setType(notes_type);
                        note.setResult(result);

                        noteArrayList.add(note);
                    }

                    Collections.reverse(noteArrayList);

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    notesAdapter = new NotesAdapter(context, noteArrayList);
                    notesAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedNote = noteArrayList.get(position);

                            if (fromButton == 1) {

                            } else if (fromButton == 2) {
                                openDeleteNoteDialog();
                            }
                        }
                    });

                    recyclerView.setAdapter(notesAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void deleteNote() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("notes_id", selectedNote.getId());
        params.put("user_id", userId);
        params.put("user_token", userToken);

        HttpProvider.post(context, "controller_global/DeleteUserNotes.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);

                Debugger.logD(str);

                if (str.contains("success")) {
                    Toasty.success(context, "Deleted").show();
                } else
                    Toasty.warning(context, "Failed").show();

                getAllNotes();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void openAddNoteDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_note, null);
        final EditText et_AddEditNoteTitle, et_AddEditNoteUrl;
        final TextView tvHeader;
        final Button btn_AddEditNoteSave,btn_ChooseAddNoteFile;
        final ImageView imageView33;

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();


        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void openDeleteNoteDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_delete_colored)
                .setMessage("Are you sure you want to delete \n" + selectedNote.getTitle() + "?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote();
                    }
                })
                .setNegativeButton("NO", null)
                .create();
        dialog.show();
    }
}
