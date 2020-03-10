package com.theyestech.yes_mobile.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.models.Subject;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.UserRole;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class NewTopicActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int VIDEO_PICK_GALLERY_CODE = 2000;

    private String storagePermission[];
    private Uri selectedFile;
    private String selectedFilePath = "";

    private ImageView ivBack, ivImage, ivGallery, ivAttach, ivSend;
    private EditText etDetails;
    private VideoView vvVideo;

    private Subject subject;
    private File myFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);

        subject = getIntent().getParcelableExtra("SUBJECT");

        context = this;
        role = UserRole.getRole(context);

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        initializeUI();
    }

    private void initializeUI() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorPeterriver));
//        }

        ivBack = findViewById(R.id.iv_NewTopicBack);
        ivImage = findViewById(R.id.iv_NewTopicImage);
        ivGallery = findViewById(R.id.iv_NewTopicGallery);
        ivAttach = findViewById(R.id.iv_NewTopicAttach);
        ivSend = findViewById(R.id.iv_NewTopicSend);
        etDetails = findViewById(R.id.et_NewTopicDetails);
        vvVideo = findViewById(R.id.vv_NewTopicVideo);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilePath = "";
                selectAction();
            }
        });

        ivAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDetails.getText().toString().isEmpty()) {
                    Toasty.warning(context, "Please input details.").show();
                } else {
                    if (selectedFilePath.isEmpty()) {
                        Toasty.warning(context, "Please select image or video.").show();
                    } else {
                        myFile = new File(selectedFilePath);
                        saveNewTopic();
                    }
                }
            }
        });
    }

    private void saveNewTopic() {
        ProgressPopup.showProgress(context);

        RequestParams params = new RequestParams();
        params.put("topic_details", etDetails.getText().toString());
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
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD(str);
                if (str.contains("success")) {
                    Toasty.success(context, "Saved.").show();
                    finish();
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

    private void pickVideoGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, VIDEO_PICK_GALLERY_CODE);
    }

    private void selectAction() {
        String items[] = {" Image ", " Video ", " Cancel "};
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Select action");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickImageGallery();
                    }
                }
                if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickVideoGallery();
                    }
                }
                if (which == 2) {
                    dialog.dismiss();
                }
            }
        });
        dialog.create().show();
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                selectedFile = data.getData();
                ivImage.setImageURI(selectedFile);
                ivImage.setVisibility(View.VISIBLE);

                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = context.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedFilePath = cursor.getString(columnIndex);
            } else if (requestCode == VIDEO_PICK_GALLERY_CODE) {
                selectedFile = data.getData();

                Uri selectedVideo = data.getData();

                String[] filePathColumn = {MediaStore.Video.Media.DATA};

                Cursor cursor = context.getContentResolver().query(selectedVideo,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedFilePath = cursor.getString(columnIndex);

                vvVideo.setVideoURI(Uri.parse(selectedFilePath));
                vvVideo.start();
                vvVideo.setMediaController(new MediaController(context));
                vvVideo.setVisibility(View.VISIBLE);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
