package com.internationalhelper.internationalhelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.internationalhelper.internationalhelper.Adapters.SelectedImagesAdapter;
import com.internationalhelper.internationalhelper.Models.PostImages;
import com.internationalhelper.internationalhelper.Models.PostModel;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UpdatePostACtivity extends AppCompatActivity {
    EditText postTitle,postDescription,postPrice,postLocation;
    //CardView addImage;
    Spinner categorySpinner;
    TextView imageErrorMsg,CatErrorMsg;
    ImageView selectedImage,back_img;
    //Button submitPost;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    FirebaseStorage storage;
    StorageReference storageReference;
    String postPushkey;
    String uploadTime;
    String postCat = null;
    Uri postImageUri = null;
    String postImageLink = null;
    RecyclerView selectedImagesRV;
    private ArrayList<MediaFile> mediaFiles;
    private ArrayList<String> imageArray;
    private ArrayList<PostImages> array;
    private Boolean imageEmpty = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post_a_ctivity);
        postTitle = findViewById(R.id.title);
        postDescription = findViewById(R.id.w_r_u_selling);
        postPrice = findViewById(R.id.price);
        postLocation = findViewById(R.id.add_location);
        back_img=findViewById(R.id.imageViewAPback);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //addImage = findViewById(R.id.add_image);
        categorySpinner = findViewById(R.id.catagory);
        //submitPost = findViewById(R.id.submit_post);
        selectedImage = findViewById(R.id.show_selected_image);
        selectedImagesRV = findViewById(R.id.selectedImagesRV);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressDialog = new ProgressDialog(UpdatePostACtivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading...");
        mediaFiles = new ArrayList<MediaFile>();
        imageArray = new ArrayList<String>();
        array = new ArrayList<PostImages>();
        String[] catArray = {"Laptops", "Mobiles", "Books","Tuition","Renting","Part time jobs"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catArray);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        categorySpinner.setAdapter(dataAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> adapterView, View view,
                    int i, long l) {
                //if(!categorySpinner.getSelectedItem().toString().equals("Select Item")){
                    postCat = categorySpinner.getSelectedItem().toString();
                    Log.d("spinnerItem",postCat);
              //  }


            }

            public void onNothingSelected(
                    AdapterView<?> adapterView) {

            }
        });
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        uploadTime =currentTime;
        Intent intent = getIntent();
        postTitle.setText(intent.getStringExtra("title"));
        postDescription.setText(intent.getStringExtra("description"));
        postPrice.setText(intent.getStringExtra("price"));
        postLocation.setText(intent.getStringExtra("location"));
        postPushkey =intent.getStringExtra("pushkey");
//        Glide.with(this)
//                .load(intent.getStringExtra("image"))
//                .into(selectedImage);
    }

    public void updatePost(View view) {
        if(postDescription.getText().toString().isEmpty() || postLocation.getText().toString().isEmpty() || postPrice.getText().toString().isEmpty() || postTitle.getText().toString().isEmpty()){
            Toast.makeText(UpdatePostACtivity.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();

        }else if(!imageEmpty){
            progressDialog.show();
            mDatabase.child("user_posts").child(postPushkey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   // if(postImageUri != null){
                        updateImage();
//                    }else{
//
//                        mDatabase.child("user_posts").child(postPushkey).child("post_category").setValue(postCat);
//                        mDatabase.child("user_posts").child(postPushkey).child("post_description").setValue(postDescription.getText().toString());
//                        mDatabase.child("user_posts").child(postPushkey).child("post_location").setValue(postLocation.getText().toString());
//                        mDatabase.child("user_posts").child(postPushkey).child("post_price").setValue(postPrice.getText().toString());
//                        mDatabase.child("user_posts").child(postPushkey).child("upload_time").setValue(uploadTime);
//                        mDatabase.child("user_posts").child(postPushkey).child("post_title").setValue(postTitle.getText().toString());
//
//                        progressDialog.dismiss();
//                        Toast.makeText(UpdatePostACtivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
//
//                    }
                }
            });



        }
        else{
//            if(postImageUri != null){
//                updateImage();
//            }else{

                mDatabase.child("user_posts").child(postPushkey).child("post_category").setValue(postCat);
                mDatabase.child("user_posts").child(postPushkey).child("post_description").setValue(postDescription.getText().toString());
                mDatabase.child("user_posts").child(postPushkey).child("post_location").setValue(postLocation.getText().toString());
                mDatabase.child("user_posts").child(postPushkey).child("post_price").setValue(postPrice.getText().toString());
                mDatabase.child("user_posts").child(postPushkey).child("upload_time").setValue(uploadTime);
                mDatabase.child("user_posts").child(postPushkey).child("post_title").setValue(postTitle.getText().toString());

                progressDialog.dismiss();
                Toast.makeText(UpdatePostACtivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();

          //  }
        }




    }
    void updateImage(){
        for(MediaFile item : mediaFiles) {
            final StorageReference ref = storageReference.child("images/" +item.getUri().getLastPathSegment());
            ref.putFile(item.getUri())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {

                                    postImageLink = String.valueOf(downloadUrl);
                                   // postImageLink = String.valueOf(downloadUrl);
                                    // createPost();
                                    PostImages obj  = new PostImages();
                                    obj.imageUrl  = postImageLink.toString();

                                    array.add(obj);
                                    if (array.size() == mediaFiles.size()){
                                        createPost();
                                    }


                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(UpdatePostACtivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
    void createPost(){
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = mDatabase.child("posts").push().getKey();
        PostModel user_posts = new PostModel(postTitle.getText().toString(),
                postCat,
                postDescription.getText().toString(),
                postLocation.getText().toString(),
                postPrice.getText().toString(),
                array,
                key,
                uploadTime,
                currentuser
        );

        mDatabase.child("user_posts").child(key).setValue(user_posts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                postCat = null;
//                postTitle.getText().clear();
//                postDescription.getText().clear();
//                postPrice.getText().clear();
//                postLocation.getText().clear();
//                postImageLink = null;
//                array.clear();
//                imageEmpty = true;
//                selectedImage.setImageResource(R.drawable.ic_photo);
                progressDialog.dismiss();
                Toast.makeText(UpdatePostACtivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdatePostACtivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void startAllPicker() {

        Intent intent = new Intent(this, FilePickerActivity.class);
        intent.putExtra(
                FilePickerActivity.CONFIGS,
                new Configurations.Builder()
                        .setCheckPermission(true)
                        .setSelectedMediaFiles(mediaFiles)
                        .setShowFiles(false)
                        .setShowImages(true)
                        .setShowVideos(false)
                        .setShowAudios(false)
                        .enableImageCapture(true)
                        .setMaxSelection(10)
                        .setSkipZeroSizeFiles(true)
                        .build()
        );
        startActivityForResult(intent, 1001);



    }
    public void selectImage(View view) {
        startAllPicker();
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri filePath = data.getData();
//            Log.d("imageUri", String.valueOf(filePath));
//            postImageUri =filePath;
//        }
        if (requestCode == 1001){
            if(data != null) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                Bitmap bitmap = null;
                mediaFiles.clear();
                mediaFiles.addAll( data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
                if (mediaFiles.size() > 0){
                    imageEmpty = false;
                }

                imageArray.clear();

                for(MediaFile item : mediaFiles) {
                    imageArray.add(item.getUri().toString());
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), item.getUri());
//                        selectedImage.setImageBitmap(bitmap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                }
                selectedImagesRV.setVisibility(View.VISIBLE);
                selectedImagesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                SelectedImagesAdapter adapter = new SelectedImagesAdapter(UpdatePostACtivity.this, imageArray);
                selectedImagesRV.setAdapter(adapter);

            }
        }
    }

}