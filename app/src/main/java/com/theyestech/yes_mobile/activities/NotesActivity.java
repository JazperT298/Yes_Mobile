package com.theyestech.yes_mobile.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

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
    private String title = "", url = "";

    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int DOCUMENT_PERMISSION_CODE = 103;
    private static final int DOCUMENT_REQUEST_CODE = 104;

    private String storagePermission[];
    private String cameraPermission[];
    private Uri selectedFile;
    private String selectedFilePath = "";
    private String displayName = "";
    private File myFile;

    private Note note;

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

        HttpProvider.post(context, "controller_educator/GetUserNotes.php", params, new AsyncHttpResponseHandler() {
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

                        note = new Note();
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
                                Debugger.logD("note " + selectedNote.getFile());
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://theyestech.com/notes-files/" + selectedNote.getFile()));
                                    startActivity(intent);
                                } catch(Exception e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://theyestech.com/notes-files/" + selectedNote.getFile())));
                                }
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

        et_AddEditNoteTitle = dialogView.findViewById(R.id.et_AddEditNoteTitle);
        et_AddEditNoteUrl = dialogView.findViewById(R.id.et_AddEditNoteUrl);
        //tvHeader = dialogView.findViewById(R.id.tvHeader);
        btn_AddEditNoteSave = dialogView.findViewById(R.id.btn_AddEditNoteSave);
        btn_ChooseAddNoteFile = dialogView.findViewById(R.id.btn_ChooseAddNoteFile);
        imageView33 = dialogView.findViewById(R.id.imageView33);

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();

        imageView33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
            }
        });

        btn_ChooseAddNoteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAction();
            }
        });

        btn_AddEditNoteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = et_AddEditNoteTitle.getText().toString();
                url = et_AddEditNoteUrl.getText().toString();
//                selectedFilePath = myFile.toString();
//                Debugger.logD("selectedFilePath " + selectedFilePath);
                if (title.isEmpty())
                    Toasty.warning(context, "Please input note title.").show();
                else {
                    try {
                        saveNotes();
                        b.hide();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        b.show();
        Objects.requireNonNull(b.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void saveNotes() throws FileNotFoundException {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("user_id", UserEducator.getID(context));
        params.put("user_token", UserEducator.getToken(context));
        params.put("notes_title", title);
        params.put("notes_url", url);
        params.put("notes_file", myFile);


        HttpProvider.post(context, "controller_global/UploadUserNotes.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                String str = new String(responseBody, StandardCharsets.UTF_8);
                if (str.contains("success")) {
                    Toasty.success(context, "Saved.").show();
                    getAllNotes();
                } else
                    Toasty.warning(context, "Failed").show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ProgressPopup.hideProgress();
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });

    }

    private void selectAction() {
        String[] items = {" Camera ", " Gallery ", " File "};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Choose File");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    askCameraPermissions();
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        selectedFilePath = "";
                        pickImageGallery();
                    }
                }else if (which == 2){
                    Toasty.warning(context, "To be continued... got my brain dam atm").show();
                    //askDocumentPermissions();
                }
            }
        });
        dialog.create().show();
    }

    private void askCameraPermissions(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }else  {
            pickCamera();
        }
    }
    private void askDocumentPermissions(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, DOCUMENT_PERMISSION_CODE);
        }else  {
            pickDocument();
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture,  CAMERA_REQUEST_CODE);//
    }

    private void pickDocument(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("*/*");
        startActivityForResult(intent, DOCUMENT_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickImageGallery();
                    } else {
                        Toasty.error(context, "Permission denied ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length < 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toasty.error(context, "Permission denied ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case DOCUMENT_PERMISSION_CODE:
                if (grantResults.length < 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickDocument();
                    } else {
                        Toasty.error(context, "Permission denied ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                selectedFile = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = context.getContentResolver().query(selectedFile,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedFilePath = cursor.getString(columnIndex);
                myFile = new File(selectedFilePath);
//                displayName = myFile.getName();
//                Debugger.logD("gallery " + displayName );

            }else if (requestCode == CAMERA_REQUEST_CODE){
                Bitmap image = (Bitmap)data.getExtras().get("data");
                //selectedFile = data.getData();
                Debugger.logD("image " + image);
                Random r = new Random();
                int randomNumber = r.nextInt(10000);
                selectedFilePath = String.valueOf(randomNumber);
                File filesDir = getApplicationContext().getFilesDir();
                myFile = new File(filesDir, selectedFilePath + ".jpg");
//                displayName = myFile.getName();
//                Debugger.logD("camera " + displayName );
                OutputStream os;
                try {
                    os = new FileOutputStream(myFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
            }else if (requestCode == DOCUMENT_REQUEST_CODE){

                selectedFile = data.getData();
                String uriString = selectedFile.toString();
                myFile = new File(uriString);
                Debugger.logD("YAWA " + myFile );
                selectedFilePath = myFile.getAbsolutePath();
                Debugger.logD("bwesit " + selectedFilePath );
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getApplicationContext().getContentResolver().query(selectedFile, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }

//                selectedFilePath = data.getData().getPath();
//                Debugger.logD("selectedFilePaths " + selectedFilePath );
                myFile = new File(selectedFilePath + '/' +  displayName);
                Debugger.logD("file " + myFile );
//                //getSelectedFilePath(selectedFile);
//                myFile = new File(selectedFilePath);
//                //Uri.parse(new File("/sdcard/cats.jpg").toString());
//                //selectedFile = Uri.fromFile(new File(selectedFilePath));
//                Debugger.logD("myFiles " + myFile );
//
//                String[] filePathColumn = {MediaStore.MediaColumns.DATA};
//
//                Cursor cursor = context.getContentResolver().query(selectedFile,
//                        filePathColumn, null, null, null);
//                Objects.requireNonNull(cursor).moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                selectedFilePath = cursor.getString(columnIndex);
//                Debugger.logD("selectedFilePath " + selectedFilePath );
//                myFile = new File(selectedFilePath);
//
//                Debugger.logD("SUCCESS " + myFile );
//                cursor.close();
//                selectedFile =  Uri.fromFile(new File(selectedFilePath));
//                Debugger.logD("selectedFile " + selectedFile );
//                String[] filePathColumn = {MediaStore.MediaColumns.DATA};
//
//                Cursor cursor = context.getContentResolver().query(selectedFile,
//                        filePathColumn, null, null, null);
//                Objects.requireNonNull(cursor).moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                selectedFilePath = cursor.getString(columnIndex);
//                Debugger.logD("selectedFilePath " + selectedFilePath );
//                myFile = new File(selectedFilePath);
//
//                Debugger.logD("SUCCESS " + myFile );
//                cursor.close();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
