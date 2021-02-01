package com.theyestech.yes_mobile.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.adapters.NewsfeedAdapter;
import com.theyestech.yes_mobile.adapters.NotesAdapter;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Newsfeed;
import com.theyestech.yes_mobile.models.Note;
import com.theyestech.yes_mobile.models.UserEducator;
import com.theyestech.yes_mobile.models.UserStudent;
import com.theyestech.yes_mobile.utils.GlideOptions;
import com.theyestech.yes_mobile.utils.OkayClosePopup;
import com.theyestech.yes_mobile.utils.ProgressPopup;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.UserRole;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class NewNewsfeedActivity extends AppCompatActivity {

    private Context context;
    private String role;
    private String userToken;
    private String userId;

    private ImageView ivBack;
    private TextView tv_Filename;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyIndicator;
    private FloatingActionButton floatingActionButton;

    private ArrayList<Newsfeed> newsfeedArrayList = new ArrayList<>();
    private NewsfeedAdapter newsfeedAdapter;
    private Newsfeed selectedNewsFeed = new Newsfeed();
    private String details = "";


    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;

    private static final int VIDEO_PERMISSION_CODE = 2000;
    private static final int VIDEO_REQUEST_CODE = 3000;

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

    private Newsfeed newsfeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_newsfeed);

        context = this;
        role = UserRole.getRole(context);
        Debugger.logD("role " + role);

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        initializeUI();

        if (role.equals(UserRole.Educator())) {
            if (UserEducator.getFirstname(context) == null) {
            ShowEducatorIntro("New Newsfeed", "Add text, images and videos to newsfeed", R.id.fab_NewsFeed, 1);
            }
        }
        else {
            if (UserStudent.getFirstname(context) == null) {
            ShowEducatorIntro("New Newsfeed", "Add text, images and videos to newsfeed", R.id.fab_NewsFeed, 1);
            }
        }
    }

    private void initializeUI () {
        ivBack = findViewById(R.id.iv_NewsFeedBack);
        swipeRefreshLayout = findViewById(R.id.swipe_NewsFeed);
        recyclerView = findViewById(R.id.rv_NewsFeed);
        emptyIndicator = findViewById(R.id.view_EmptyRecord);
        floatingActionButton = findViewById(R.id.fab_NewsFeed);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (role.equals(UserRole.Educator())) {
            userId = UserEducator.getID(context);
            userToken = UserEducator.getToken(context);
            getEducatorNewsfeedDetails();
        } else {
            userId = UserStudent.getID(context);
            userToken = UserStudent.getToken(context);
            getStudentNewsfeedDetails();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (role.equals(UserRole.Educator())) {
                    userId = UserEducator.getID(context);
                    userToken = UserEducator.getToken(context);
                    getEducatorNewsfeedDetails();
                } else {
                    userId = UserStudent.getID(context);
                    userToken = UserStudent.getToken(context);
                    getStudentNewsfeedDetails();
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNewsFeedDialog();
            }
        });
    }
    public void getEducatorNewsfeedDetails() {
        newsfeedArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("teach_token", UserEducator.getToken(context));
        Debugger.logD("teach_token2 " + UserEducator.getToken(context));

        HttpProvider.post(context, "controller_educator/get_post.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                Debugger.logD("responseBody " + responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("str " + str);
                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("NEWSFEED: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nf_id = jsonObject.getString("nf_id");
                        String nf_token = jsonObject.getString("nf_token");
                        String nf_user_token = jsonObject.getString("nf_user_token");
                        String nf_details = jsonObject.getString("nf_details");
                        String nf_files = jsonObject.getString("nf_files");
                        String nf_filetype = jsonObject.getString("nf_filetype");
                        String nf_date = jsonObject.getString("nf_date");
                        String nf_fullname = jsonObject.getString("nf_fullname");
                        String nf_image = jsonObject.getString("nf_image");

                        Newsfeed newsfeed = new Newsfeed();
                        newsfeed.setNf_id(nf_id);
                        newsfeed.setNf_token(nf_token);
                        newsfeed.setNf_user_token(nf_user_token);
                        newsfeed.setNf_details(nf_details);
                        newsfeed.setNf_files(nf_files);
                        newsfeed.setNf_filetype(nf_filetype);
                        newsfeed.setNf_date(nf_date);
                        newsfeed.setNf_fullname(nf_fullname);
                        newsfeed.setNf_image(nf_image);

                        newsfeedArrayList.add(newsfeed);
                    }
                    Collections.reverse(newsfeedArrayList);

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    newsfeedAdapter = new NewsfeedAdapter(context, newsfeedArrayList, role);
                    newsfeedAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedNewsFeed = newsfeedArrayList.get(position);
                            if(fromButton == 1){
                                getNewsfeedByToken();
                            }
                        }
                    });

                    recyclerView.setAdapter(newsfeedAdapter);

                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD("e " +e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    public void getStudentNewsfeedDetails() {
        newsfeedArrayList.clear();

        swipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("teach_token", UserStudent.getToken(context));
        Debugger.logD("teach_token2 " + UserStudent.getToken(context));

        HttpProvider.post(context, "controller_educator/get_post.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeRefreshLayout.setRefreshing(false);
                Debugger.logD("responseBody " + responseBody);
                String str = new String(responseBody, StandardCharsets.UTF_8);
                Debugger.logD("str " + str);
                if (str.equals(""))
                    emptyIndicator.setVisibility(View.VISIBLE);
                try {
                    JSONArray jsonArray = new JSONArray(str);
                    Debugger.logD("NEWSFEED: " + jsonArray);
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nf_id = jsonObject.getString("nf_id");
                        String nf_token = jsonObject.getString("nf_token");
                        String nf_user_token = jsonObject.getString("nf_user_token");
                        String nf_details = jsonObject.getString("nf_details");
                        String nf_files = jsonObject.getString("nf_files");
                        String nf_filetype = jsonObject.getString("nf_filetype");
                        String nf_date = jsonObject.getString("nf_date");
                        String nf_fullname = jsonObject.getString("nf_fullname");
                        String nf_image = jsonObject.getString("nf_image");

                        Newsfeed newsfeed = new Newsfeed();
                        newsfeed.setNf_id(nf_id);
                        newsfeed.setNf_token(nf_token);
                        newsfeed.setNf_user_token(nf_user_token);
                        newsfeed.setNf_details(nf_details);
                        newsfeed.setNf_files(nf_files);
                        newsfeed.setNf_filetype(nf_filetype);
                        newsfeed.setNf_date(nf_date);
                        newsfeed.setNf_fullname(nf_fullname);
                        newsfeed.setNf_image(nf_image);

                        newsfeedArrayList.add(newsfeed);
                    }
                    Collections.reverse(newsfeedArrayList);

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    newsfeedAdapter = new NewsfeedAdapter(context, newsfeedArrayList, role);
                    newsfeedAdapter.setClickListener(new OnClickRecyclerView() {
                        @Override
                        public void onItemClick(View view, int position, int fromButton) {
                            selectedNewsFeed = newsfeedArrayList.get(position);
                            if(fromButton == 1){
                                getNewsfeedByToken();
                            }
                        }
                    });

                    recyclerView.setAdapter(newsfeedAdapter);

                    emptyIndicator.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Debugger.logD("e " +e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                swipeRefreshLayout.setRefreshing(false);
                OkayClosePopup.showDialog(context, "No internet connect. Please try again.", "Close");
            }
        });
    }

    private void openAddNewsFeedDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_newsfeed, null);
        final EditText et_AddEditDetails;
        final Button btn_ChooseAddNewsFeedFile,btn_AddEditNewsFeedSave;
        final ImageView imageView33;

        et_AddEditDetails = dialogView.findViewById(R.id.et_AddEditDetails);
        //tvHeader = dialogView.findViewById(R.id.tvHeader);
        btn_ChooseAddNewsFeedFile = dialogView.findViewById(R.id.btn_ChooseAddNewsFeedFile);
        btn_AddEditNewsFeedSave = dialogView.findViewById(R.id.btn_AddEditNewsFeedSave);
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

        btn_ChooseAddNewsFeedFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActions();
            }
        });

        btn_AddEditNewsFeedSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details = et_AddEditDetails.getText().toString();

                if (details.isEmpty())
                    Toasty.warning(context, "Please input news feed details.").show();
                else {
                    try {
                        saveNewNewsfeed();
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

    private void saveNewNewsfeed() throws FileNotFoundException {
        ProgressPopup.showProgress(context);

        Debugger.logD("details " + details);
        Debugger.logD("myFile " + myFile);

        RequestParams params = new RequestParams();
        params.put("nf_details", details);
        if (myFile != null){
            params.put("nf_file", myFile);
        }else{
            params.put("nf_file", "");
        }


        HttpProvider.post(context, "controller_educator/upload_post.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ProgressPopup.hideProgress();
                Debugger.logD("responseBody " +responseBody);
                String str = new String(responseBody, StandardCharsets.US_ASCII);
                Debugger.logD("TEST " +str);
                if (str.contains("success")) {
                    Toasty.success(context, "Saved.").show();
                    getEducatorNewsfeedDetails();
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

    private void getNewsfeedByToken(){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
        dialog.setContentView(R.layout.dialog_view_newsfeed_bytoken);
        final ImageView close,iv_user_image, iv_image,iv_type;
        final VideoView vv_video;
        final TextView tv_user_name, tv_date,tv_details;
        final ConstraintLayout constraint_comments, constraint_yes;
        close = dialog.findViewById(R.id.close);
        iv_user_image = dialog.findViewById(R.id.iv_user_image);
        iv_image = dialog.findViewById(R.id.iv_image);
        iv_type = dialog.findViewById(R.id.iv_type);
        vv_video = dialog.findViewById(R.id.vv_video);
        tv_user_name = dialog.findViewById(R.id.tv_user_name);
        tv_date = dialog.findViewById(R.id.tv_date);
        tv_details = dialog.findViewById(R.id.tv_details);
        constraint_comments = dialog.findViewById(R.id.constraint_comments);
        constraint_yes = dialog.findViewById(R.id.constraint_yes);

        tv_user_name.setText(selectedNewsFeed.getNf_fullname());
        tv_date.setText(selectedNewsFeed.getNf_date());
        tv_details.setText(selectedNewsFeed.getNf_details());

        constraint_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsfeedCommentActivity.class);
                intent.putExtra("NEWSFEED_ID", selectedNewsFeed.getNf_id());
                intent.putExtra("NEWSFEED_TOKEN", selectedNewsFeed.getNf_token());
                context.startActivity(intent);
            }
        });
        if (selectedNewsFeed.getNf_filetype().equalsIgnoreCase("image")){
            //iv_ListrowSubjectTopicsImage.setImageURI(Uri.parse(HttpProvider.getTopicDir() + file));
            //iv_ListrowSubjectTopicsImage.setVisibility(View.VISIBLE);
            Debugger.logD("as " + HttpProvider.getNewsfeedDir() + selectedNewsFeed.getNf_files());
            Glide.with(context)
                    .load(HttpProvider.getNewsfeedDir() + selectedNewsFeed.getNf_files())
                    .into(iv_image);
            iv_image.setVisibility(View.VISIBLE);
        }else{
            vv_video.setVideoURI(Uri.parse(HttpProvider.getNewsfeedDir() + selectedNewsFeed.getNf_files()));
            vv_video.setMediaController(new MediaController(context));
            vv_video.start();
            iv_image.setVisibility(View.GONE);
            vv_video.setVisibility(View.VISIBLE);
            iv_type.setImageResource(R.drawable.ic_video);
        }
        Glide.with(context)
                .load(HttpProvider.getProfileDir() + selectedNewsFeed.getNf_image())
                .apply(GlideOptions.getOptions())
                .into(iv_user_image);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void ShowEducatorIntro(String title, String text, int viewId, final int type) {

        new GuideView.Builder(context)
                .setTitle(title)
                .setContentText(text)
                .setTargetView(findViewById(viewId))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.targetView) //optional - default dismissible by TargetView
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                    }
                })
                .build()
                .show();
    }

    private void selectActions() {
        String[] items = {" Camera ", " Gallery ", " Videos "};
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
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        askVideoPermissions();
                    }
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

    private void selectAction() {
        String items[] = {" Image ", " Video ", " Cancel "};
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context);
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
                        //pickVideoGallery();
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
                displayName = myFile.getName();
                tv_Filename.setText(displayName);
                Debugger.logD("gallery " + displayName );

            }else if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
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
            } else if (requestCode == VIDEO_REQUEST_CODE) {
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
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
