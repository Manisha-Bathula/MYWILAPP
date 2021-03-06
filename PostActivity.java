package com.internationalhelper.internationalhelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.internationalhelper.internationalhelper.Models.PostModel;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {
     EditText postTitle,postDescription,postPrice,postLocation;
     CardView addImage;
     Spinner categorySpinner;
     TextView imageErrorMsg,CatErrorMsg;
     ImageView showSelectedImage;
     Button  submitPost;
     String postCat = null;
     Uri postImageUri = null;
    String postImageLink = null;
    String uploadTime;
     private DatabaseReference mDatabase;
     ProgressDialog progressDialog;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postTitle = findViewById(R.id.title);
        postDescription = findViewById(R.id.w_r_u_selling);
        postPrice = findViewById(R.id.price);
        postLocation = findViewById(R.id.add_location);
        addImage = findViewById(R.id.add_image);
        categorySpinner = findViewById(R.id.catagory);
        submitPost = findViewById(R.id.submit_post);
        showSelectedImage = findViewById(R.id.show_selected_image);

        imageErrorMsg = findViewById(R.id.image_error_msg);
        CatErrorMsg = findViewById(R.id.cat_error_msg);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading...");
        getSupportActionBar().setTitle("Create Post");
        String[] catArray = {"Select Item", "Laptops", "Mobiles", "Books","Tuition","Renting","Part time jobs"};
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        uploadTime =currentTime;
        // setting data to adapter
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catArray);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        categorySpinner.setAdapter(dataAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> adapterView, View view,
                            int i, long l) {
                        if(!categorySpinner.getSelectedItem().toString().equals("Select Item")){
                            postCat = categorySpinner.getSelectedItem().toString();
                            Log.d("spinnerItem",postCat);
                        }


                    }

                    public void onNothingSelected(
                            AdapterView<?> adapterView) {

                    }
                });


    }

    public void addPost(View view) {
        if(postTitle.getText().toString().isEmpty()){
            postTitle.setError("Please enter title");
            postTitle.requestFocus();
        }
        else if(postCat == null){
            CatErrorMsg.setVisibility(view.VISIBLE);
        }
        else if(postDescription.getText().toString().isEmpty()){
            postDescription.setError("Please enter description");
            postDescription.requestFocus();
        }
        else if(postPrice.getText().toString().isEmpty()){
            postPrice.setError("Please enter price");
            postPrice.requestFocus();
        }
        else if(postLocation.getText().toString().isEmpty()){
            postLocation.setError("Please enter location");
            postLocation.requestFocus();
        }
        else if(postImageUri == null){
            imageErrorMsg.setVisibility(view.VISIBLE);
        }
        else{
            imageErrorMsg.setVisibility(view.INVISIBLE);
            uploadImage();
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
                postImageLink,
                key,
                uploadTime,
                currentuser
                );

        mDatabase.child("user_posts").child(key).setValue(user_posts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                postCat = null;
                postTitle.getText().clear();
                postDescription.getText().clear();
                postPrice.getText().clear();
                postLocation.getText().clear();
                postImageLink = null;
                showSelectedImage.setImageResource(R.drawable.ic_photo);
                progressDialog.dismiss();
                Toast.makeText(PostActivity.this, "Post has been created", Toast.LENGTH_SHORT).show();

            }
             })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void uploadImage() {


        progressDialog.show();
        if(postImageUri != null)
        {
            final StorageReference ref = storageReference.child("images/" + "");
            ref.putFile(postImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
                                    postImageLink = String.valueOf(downloadUrl);
                                    createPost();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(PostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
                Log.d("imageUri", String.valueOf(filePath));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            showSelectedImage.setImageBitmap(bitmap);
            postImageUri =filePath;
        }
    }
}