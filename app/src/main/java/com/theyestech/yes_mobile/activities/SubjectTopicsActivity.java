package com.theyestech.yes_mobile.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.TopicsAdapter;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.utils.FilePath;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.models.Topic;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SubjectTopicsActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private ImageView ivBack;
    private TextView tv_Filename;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout emptyIndicator;

    private ArrayList<Topic> topicArrayList = new ArrayList<>();
    private TopicsAdapter topicsAdapter;
    private Topic selectedTopic = new Topic();

    private Subject subject;
    private String topic_title = "";

    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int DOCUMENT_PERMISSION_CODE = 103;
    private static final int DOCUMENT_REQUEST_CODE = 4000;
    private static final int VIDEO_PERMISSION_CODE = 2000;
    private static final int VIDEO_REQUEST_CODE = 3000;

    private String storagePermission[];
    private String cameraPermission[];
    private Uri selectedFile;
    private String selectedFilePath = "";
    private String displayName = "";
    private File myFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_topics);

        subject = getIntent().getParcelableExtra("SUBJECT");

        context = this;
        role = UserRole.getRole(context);

        initializeUI();
    }

    private void initializeUI() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorPeterriver));
//        }

        ivBack = findViewById(R.id.iv_SubjectTopicsBack);
        swipeRefreshLayout = findViewById(R.id.swipe_SubjectTopics);
        recyclerView = findViewById(R.id.rv_SubjectTopics);
        floatingActionButton = findViewById(R.id.fab_SubjectTopicsAdd);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTopicDetails();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, SubjectNewTopicActivity.class);
//                intent.putExtra("SUBJECT", subject);
//                startActivity(intent);
                openAddTopicDialog();
            }
        });
    }

    private void deleteTopic(String topic_id) {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("topic_id", topic_id);
        params.put("user_id", UserEducator.getID(context));

        ProgressPopup.hideProgress();

        HttpProvider.post(context, "controller_educator/DeleteTopicById.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                Debugger.logD("responseBody " + responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);

                Debugger.logD("str " + str);

                if (str.contains("success")) {
                    Toasty.success(context, "Deleted").show();
                    getTopicDetails();
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

    private void openDeleteNoteDialog(String topic_id) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_delete_colored)
                .setMessage("Are you sure you want to delete ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTopic(topic_id);
                    }
                })
                .setNegativeButton("NO", null)
                .create();
        dialog.show();
    }

    private void getTopicDetails() {
        topicArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("subj_id", subject.getId());

        HttpProvider.post(context, "controller_educator/get_topics.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                String str = new String(responseBody, StandardCharsets.UTF_8);

                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);

                try {

                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("TOPIC: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String topic_id = jsonObject.getString("topic_id");
                        String topic_subj_id = jsonObject.getString("topic_subj_id");
                        String topic_file = jsonObject.getString("topic_file");
                        String topic_filetype = jsonObject.getString("topic_filetype");
                        String topic_details = jsonObject.getString("topic_details");

                        Topic topic = new Topic();
                        topic.setTopic_id(topic_id);
                        topic.setTopic_subj_id(topic_subj_id);
                        topic.setTopic_details(topic_details);
                        topic.setTopic_filetype(topic_filetype);
                        topic.setTopic_file(topic_file);

                        topicArrayList.add(topic);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    topicsAdapter = new TopicsAdapter(context, topicArrayList);
                    topicsAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedTopic = topicArrayList.get(position);
//                            Intent intent = new Intent(context, SubjectDetailsActivity.class);
//                            intent.putExtra("TOPIC", topic);
//                            startActivity(intent);
                            if(fromButton == 1){

                            }else if (fromButton == 2){
                                Intent intent = new Intent(context, SubjectTopicsCommentActivity.class);
                                intent.putExtra("TOPIC_ID", selectedTopic.getTopic_id());
                                context.startActivity(intent);
                            } else if (fromButton == 3){
                                PopupMenu popup = new PopupMenu(context, view);
                                popup.getMenuInflater().inflate(R.menu.delete_pop_up, popup.getMenu());

                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    public boolean onMenuItemClick(MenuItem item) {
                                        openDeleteNoteDialog(selectedTopic.getTopic_id());
                                        return true;
                                    }
                                });
                                popup.show();//showing pop
                            }
                        }
                    });

                    recyclerView.setAdapter(topicsAdapter);
                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void openAddTopicDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_topic, null);
        final EditText et_AddEditTopic;
        final Button btn_ChooseAddTopicFile,btn_AddEditTopicSave;
        final ImageView imageView33;

        et_AddEditTopic = dialogView.findViewById(R.id.et_AddEditTopic);
        //tvHeader = dialogView.findViewById(R.id.tvHeader);
        btn_AddEditTopicSave = dialogView.findViewById(R.id.btn_AddEditTopicSave);
        btn_ChooseAddTopicFile = dialogView.findViewById(R.id.btn_ChooseAddTopicFile);
        imageView33 = dialogView.findViewById(R.id.imageView33);
        tv_Filename = dialogView.findViewById(R.id.tv_Filename);
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();

        imageView33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
            }
        });

        btn_ChooseAddTopicFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAction();
            }
        });
        btn_AddEditTopicSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic_title = et_AddEditTopic.getText().toString();
                if (topic_title.isEmpty())
                    Toasty.warning(context, "Please input topic title.").show();
                else if (myFile == null){
                    Toasty.warning(context, "Please provide a file.").show();
                }else{
                    try {
                        saveTopic();
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

    private void saveTopic() throws FileNotFoundException {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("topic_details", topic_title);
        params.put("subj_id", subject.getId());

        if (myFile != null) {
            try {
                params.put("topic_file", myFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Debugger.logD(e.toString());
            }
        } else
            params.put("topic_file", "");

        HttpProvider.post(context, "controller_educator/upload_topic.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                Debugger.logD("responseBody " +responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("TEST " +str);
                if (str.contains("success")) {
                    Toasty.success(context, "Saved.").show();
                    getTopicDetails();
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
        String[] items = {" Camera ", " Gallery ", " Video ", " File "};
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
                    askVideoPermissions();
                    //askDocumentPermissions();
                }else if (which == 3){
                    askDocumentPermissions();
                    //Toasty.warning(context, "To be continued... got my brain dam atm").show();
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

    private void askVideoPermissions(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, VIDEO_PERMISSION_CODE);
        }else  {
            pickVideo();
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

    private void pickVideo(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent,"Select Video"),VIDEO_REQUEST_CODE);
    }

    private void pickDocument(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
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
            case VIDEO_PERMISSION_CODE:
                if (grantResults.length < 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickVideo();
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
                Debugger.logD("selectedFile " + selectedFile );
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = context.getContentResolver().query(selectedFile,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedFilePath = cursor.getString(columnIndex);
                myFile = new File(selectedFilePath);
                displayName = myFile.getName();
                tv_Filename.setText(displayName);
                Debugger.logD("gallery " + displayName );

            }else if (requestCode == CAMERA_REQUEST_CODE){
                Bitmap image = (Bitmap)data.getExtras().get("data");
                //selectedFile = data.getData();
                Debugger.logD("image " + image);
                Random r = new Random();
                int randomNumber = r.nextInt(10000);
                selectedFilePath = String.valueOf(randomNumber);
                File filesDir = getApplicationContext().getFilesDir();
                myFile = new File(filesDir, selectedFilePath + ".jpg");
                displayName = myFile.getName();
                tv_Filename.setText(displayName);
                Debugger.logD("camera " + displayName );
                OutputStream os;
                try {
                    os = new FileOutputStream(myFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
            }else if (requestCode == VIDEO_REQUEST_CODE){
                selectedFile = data.getData();

                String[] filePathColumn = {MediaStore.Video.Media.DATA};

                Cursor cursor = context.getContentResolver().query(selectedFile,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedFilePath = cursor.getString(columnIndex);

                myFile = new File(selectedFilePath);
                tv_Filename.setText(myFile.getName());
                Debugger.logD("file " + myFile.getName() );

            }
            else if (requestCode == DOCUMENT_REQUEST_CODE){
                if (data == null) {
                    return;
                }
                Uri uri = data.getData();
                String paths = FilePath.getFilePath(context, uri);
                Debugger.logD("File Path : " + paths);
                if (paths != null) {
                    tv_Filename.setText("" + new File(paths).getName());
                }
                selectedFilePath = paths;
                myFile = new File(selectedFilePath);
                Debugger.logD("File Path : " + myFile);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getTopicDetails();
    }
}
