package com.theyestech.yes_mobile.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theyestech.yes_mobile.HttpProvider;
import com.theyestech.yes_mobile.R;
import com.theyestech.yes_mobile.interfaces.OnClickRecyclerView;
import com.theyestech.yes_mobile.models.Files;
import com.theyestech.yes_mobile.utils.Debugger;
import com.theyestech.yes_mobile.utils.GlideOptions;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.theyestech.yes_mobile.utils.Debugger.TAG;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Files> mFiles;
    private LayoutInflater layoutInflater;
    private OnClickRecyclerView onClickRecyclerView;
    FirebaseUser fuser;
    String fileType;
    public FilesAdapter(Context mContext, ArrayList<Files> files) {
        this.mFiles = files;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public FilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.listrow_files, parent, false);
        FilesAdapter.ViewHolder viewHolder = new FilesAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Files files = mFiles.get(position);

        switch (files.getFileType()) {
            case "pdf":
               fileType = "Pdf";
                break;
            case "word":
                fileType = "Word";
                break;
            case "excel":
                fileType = "Excel";
                break;
            case "powerpoint":
                fileType = "Powerpoint";
                break;
            case "video":
                fileType = "Video";
                break;
            case "image":
                fileType = "Image";

                break;
        }

        if(files.getSender().equals(fuser.getUid())){
            holder.username.setText("You shared a " + fileType + " to " + files.getReceivername());
            Glide.with(mContext)
                    .load(HttpProvider.getProfileDir() + files.getSenderphoto())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                    .apply(GlideOptions.getOptions())
                    .into(holder.profile_image);
        }else{
            holder.username.setText(files.getSendername() + " Shared a " + fileType + " to you.");
            Glide.with(mContext)
                    .load(HttpProvider.getProfileDir() + files.getSenderphoto())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_user_colored))
                    .apply(GlideOptions.getOptions())
                    .into(holder.profile_image);
        }
        Date date = new Date(files.getDateposted().getTime());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        holder.body.setText( dateFormat.format(date));


        holder.moreHoriz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog(files.getSender());
            }
        });
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Debugger.logD("ANIMAL " +files.getFileName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://yestechfreeium.s3-ap-southeast-1.amazonaws.com" + files.getFileName()));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profile_image;
        public TextView username;
        public TextView body;
        public ImageView moreHoriz;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            body = itemView.findViewById(R.id.body);
            moreHoriz = itemView.findViewById(R.id.img_more);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
    public void setClickListener(OnClickRecyclerView onClickRecyclerView) {
        this.onClickRecyclerView = onClickRecyclerView;
    }

    private void openDeleteDialog(String sender) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_delete_colored)
                .setMessage("Remove this notification?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFunction(sender);
                    }
                })
                .setNegativeButton("NO", null)
                .create();
        dialog.show();
    }

    private void deleteFunction(String sender) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Files").orderByChild("sender").equalTo(sender);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }


}

