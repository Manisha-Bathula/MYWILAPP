package com.internationalhelper.internationalhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.florent37.shapeofview.ShapeOfView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
EditText edtName,edtPhone,edtEmail,edtPassword;
ShapeOfView sofsave;
ImageView ivproPic,ivcamera,ivBack,ivBigPropic;
String TAG = " camera";
File photoFile;
final int GET_PHOTO_FROM_GALLERY_REQUEST_CODE = 2500;//any number this can be bit different than camera
    final int TAKE_PHOTO_ACTIVITY_REQUEST_CODE = 1600;// any number can be
    String photoFilename = " photos";// any name this can be
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser user;
    private Uri filePath;
    String propic="";
    String d_id="",passwords="",emails="",names="",phones="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        edtName=findViewById(R.id.edtPName);
        edtPhone=findViewById(R.id.edtPPhone);
        edtEmail=findViewById(R.id.edtPEmail);
        edtPassword=findViewById(R.id.edtPPassword);
        sofsave=findViewById(R.id.ShapeOfViewPSave);
        ivproPic=findViewById(R.id.imageViewPropic);
        ivcamera=findViewById(R.id.imageViewPcamera);
        ivBack=findViewById(R.id.imageViewPback);
        ivBigPropic=findViewById(R.id.imageViewBigpropic);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),homeMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        profiledata();
        ivcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePhoto();
            }
        });
        sofsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=edtName.getText().toString().trim();
                String email=edtEmail.getText().toString().trim();
                String phone=edtPhone.getText().toString().trim();
                String pass=edtPassword.getText().toString().trim();
                if (name.equals("") || email.equals("") || phone.equals("") || pass.equals("")){
                    Toast.makeText(ProfileActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    if (filePath!=null){
                        updateprofile(name,email,phone,pass,filePath);
                    }else{
                        updateprofile(name,email,phone,pass,null);
                    }

                }
            }
        });

    }

    private void updateprofile(final String name, final String email, final String phone,final String pass, Uri s) {
        if (s==null){
            final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Updating...");
            progressDialog.show();
            final Map<String, Object> User = new HashMap<>();
            User.put("Name",name);
            User.put("Email",email);
            User.put("Password",pass);
            User.put("Phone", phone);
            User.put("Propic", propic);
            // Add a new document with a generated ID
            if (!email.equals(emails) || !pass.equals(passwords)) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(emails, passwords);

// Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "User re-authenticated.");
                                user.updateEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    user.updatePassword(pass)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        db.collection("User").document(d_id).
                                                                                update(User).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
//                    Intent i=new Intent(ProfileActivity.this,ProfileActivity.class);
//                    startActivity(i);
                                                                                // finish();
                                                                                progressDialog.dismiss();
                                                                                   profiledatatwo();
                                                                                Toast.makeText(ProfileActivity.this, "profile Updated", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    } else {
                                                                        progressDialog.dismiss();
                                                                        try {
                                                                            throw task.getException();
                                                                        } catch (FirebaseAuthWeakPasswordException e) {
                                                                            // Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            edtPassword.setError(e.getMessage());
                                                                            edtPassword.requestFocus();
                                                                        } catch (Exception e) {
                                                                            Log.e("jeno", e.getMessage());
                                                                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                    Log.d(TAG, "User email address updated.");
                                                } else {
                                                    progressDialog.dismiss();
                                                    try {
                                                        throw task.getException();
                                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                                        edtEmail.setError(e.getMessage());
                                                        edtEmail.requestFocus();
                                                    } catch (FirebaseAuthUserCollisionException e) {
                                                        edtEmail.setError(e.getMessage());
                                                        edtEmail.requestFocus();
                                                    } catch (Exception e) {
                                                        Log.e("jeno", e.getMessage());
                                                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                            }
                        });

            }else {
                db.collection("User").document(d_id).
                        update(User).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                    Intent i=new Intent(ProfileActivity.this,ProfileActivity.class);
//                    startActivity(i);
                        // finish();
                        progressDialog.dismiss();
                      //  profiledata();
                        Toast.makeText(ProfileActivity.this, "profile Updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }else{

            final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Please Wait");
            progressDialog.show();
            final String id = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + id);
            Log.i("jeno","photouri=>"+s);
            ref.putFile(s)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Create a new user with a first and last name
                            final Map<String, Object> User = new HashMap<>();
                            User.put("Name",name);
                            User.put("Email",email);
                            User.put("Phone", phone);
                            User.put("Password",pass);
                            User.put("Propic", id);
                            if (!email.equals(emails) || !pass.equals(passwords)) {
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(emails, passwords);

// Prompt the user to re-provide their sign-in credentials
                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d(TAG, "User re-authenticated.");
                                                user.updateEmail(email)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    user.updatePassword(pass)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        db.collection("User").document(d_id).
                                                                                                update(User).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
//                    Intent i=new Intent(ProfileActivity.this,ProfileActivity.class);
//                    startActivity(i);
                                                                                                // finish();
                                                                                                progressDialog.dismiss();
                                                                                                profiledata();
                                                                                                Toast.makeText(ProfileActivity.this, "profile Updated", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        progressDialog.dismiss();
                                                                                        try {
                                                                                            throw task.getException();
                                                                                        } catch (FirebaseAuthWeakPasswordException e) {
                                                                                            // Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                            edtPassword.setError(e.getMessage());
                                                                                            edtPassword.requestFocus();
                                                                                        } catch (Exception e) {
                                                                                            Log.e("jeno", e.getMessage());
                                                                                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });

                                                                    Log.d(TAG, "User email address updated.");
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    try {
                                                                        throw task.getException();
                                                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                                                        edtEmail.setError(e.getMessage());
                                                                        edtEmail.requestFocus();
                                                                    } catch (FirebaseAuthUserCollisionException e) {
                                                                        edtEmail.setError(e.getMessage());
                                                                        edtEmail.requestFocus();
                                                                    } catch (Exception e) {
                                                                        Log.e("jeno", e.getMessage());
                                                                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        });

                            }else {
                                db.collection("User").document(d_id).
                                        update(User).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                    Intent i=new Intent(ProfileActivity.this,ProfileActivity.class);
//                    startActivity(i);
                                        // finish();
                                        progressDialog.dismiss();
                                          profiledata();
                                        Toast.makeText(ProfileActivity.this, "profile Updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            // Add a new document with a generated ID



                            //Toast.makeText(AddSubscriptionActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage((int) progress + "%"+" Uploading..." );
                        }
                    });
            Log.i("jeno", "ref" + ref.toString());
            Log.i("jeno", "id" + id);
        }
    }

    private void profiledata() {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
       String u_id = mAuth.getCurrentUser().getUid();
        db.collection("User")
                .whereEqualTo("Userid",u_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("jeno=> ", document.getId() + " => " + document.getData());

                                edtEmail.setText(document.get("Email").toString());
                                edtName.setText(document.get("Name").toString());
                                edtPhone.setText(document.get("Phone").toString());
                                edtPassword.setText(document.get("Password").toString());
                                propic=document.get("Propic").toString();
                                passwords=document.get("Password").toString();
                                emails=document.get("Email").toString();
                                names=document.get("Name").toString();
                                phones=document.get("Phone").toString();
                                d_id=document.getId().toString();
                                if (propic.equals("0")){
                                    progressDialog.dismiss();
                                }else {

                                    storageReference = storage.getReference();
                                    StorageReference islandRef = storageReference.child("images/"+propic);
                                    islandRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            // Use the bytes to display the image
                                            progressDialog.dismiss();
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            ivproPic.setImageBitmap(bitmap);
                                            ivBigPropic.setImageBitmap(bitmap);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                            progressDialog.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Cant load profile pic", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }

                        } else {
                            Log.i("jeno", "Error getting documents.", task.getException());
                        }
                    }
                });

    }
    private void profiledatatwo() {

        String u_id = mAuth.getCurrentUser().getUid();
        db.collection("User")
                .whereEqualTo("Userid",u_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("jeno=> ", document.getId() + " => " + document.getData());

                                passwords=document.get("Password").toString();
                                emails=document.get("Email").toString();
                            }

                        } else {
                            Log.i("jeno", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void TakePhoto(){
        new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Profile Picture")
                .setMessage("Select a image from gallery or camera ?")
                .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        Log.d(TAG, "Path to photo gallery: " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, GET_PHOTO_FROM_GALLERY_REQUEST_CODE);
                        }
                    }
                })
                .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // create a file handle for the phoot that will be taken
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        String format = simpleDateFormat. format(new Date());
                        photoFile = getFileForPhoto(photoFilename+"_"+format+".jpg");
                        Log.i("vic", String.valueOf(photoFile));
                        Uri fileProvider = FileProvider.getUriForFile(ProfileActivity.this, "com.internationalhelper.internationalhelper", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, TAKE_PHOTO_ACTIVITY_REQUEST_CODE);
                        }
                    }
                })

                .show();

    }
    public File getFileForPhoto(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (mediaStorageDir.exists() == false && mediaStorageDir.mkdirs() == false) {
            Log.d(TAG, "Cannot create directory for storing photos");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }


    // Callback from the MediaPicker and Camera activities
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "reuest code " + requestCode);
        // check if it was the camera app that just closed
        if (requestCode == TAKE_PHOTO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // the photo is saved to file
                filePath=Uri.fromFile(new File(photoFile.getAbsolutePath()));
                Bitmap image = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                // make the image fit into the imageview
                ivproPic.setVisibility(View.VISIBLE);
                ivproPic.setImageBitmap(image);

            } else {
                Toast t = Toast.makeText(this, "Not able to take photo", Toast.LENGTH_SHORT);
                t.show();
            }
        } else if (requestCode == GET_PHOTO_FROM_GALLERY_REQUEST_CODE) {
            // check if we got a picture from the gallery
            if (data != null) {
                Uri photoURI = data.getData();
                filePath=data.getData();
                Log.d(TAG, "Path to the photo the user selected: " + photoURI.toString());
                try {
                    ivproPic.setVisibility(View.VISIBLE);
                    Bitmap selectedPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    ivproPic.setImageBitmap(selectedPhoto);
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "FileNotFoundException: Unable to open photo gallery file");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d(TAG, "IOException: Unable to open photo gallery file");
                    e.printStackTrace();
                }
            }
        }
    }
}