package com.theyestech.yes_mobile.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.utils.UserRole;

import java.io.ByteArrayOutputStream;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class NewTopicActivity extends AppCompatActivity {

    private Context context;

    private String role;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;

    private String cameraPermission[];
    private String storagePermission[];
    private Uri image_uri;

    private List<Bitmap> mL;
    private List<Uri> mL1;
    private Uri selectImageUrl;
    private Bitmap imageBitmap;
    private String mCurrentPhotoPath;
    private String picturePath;

    private ImageView ivBack, ivImage, ivGallery, ivAttach, ivSend;
    private EditText etDetails;
    private VideoView vvVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);

        context = this;
        role = UserRole.getRole(context);

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        initializeUI();
    }

    private void initializeUI() {
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
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickGallery();
                }
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

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toasty.error(context, "Permission denied ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_GALLERY_CODE) {
            selectImageUrl = data.getData();
            ivImage.setImageURI(selectImageUrl);

            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
