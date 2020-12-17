package com.internationalhelper.internationalhelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
EditText edtname,edtemail,edtpass;
Button btnRegister;
FirebaseFirestore db;
ProgressBar progressBar;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtname=findViewById(R.id.edt_name);
        edtemail=findViewById(R.id.edt_email);
        edtpass=findViewById(R.id.edt_pass);
        btnRegister=findViewById(R.id.btn_register);
        progressBar=findViewById(R.id.progressbarregister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=edtname.getText().toString().trim();
                String email=edtemail.getText().toString().trim();
                String pass=edtpass.getText().toString().trim();
                if (name.equals("") || email.equals("") || pass.equals("")){
                    Toast.makeText(RegisterActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                }
//                else if (pass.length()<6){
//                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 characters ", Toast.LENGTH_SHORT).show();
//                }
                else {
                    SignUp(name,email,pass);
                }
            }
        });
        mAuth= FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

    }
    private void SignUp(final String name, final String email, final String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("jeno", "createUserWithEmail:success");
                            String userID = mAuth.getCurrentUser().getUid();


                            Log.i("jeno","user Id=>"+userID);
                            AddRegisterData(userID, name,email,pass);

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w("vic", "createUserWithEmail:failure", task.getException().getMessage()+"");
//                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                               // Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                edtpass.setError(e.getMessage());
                                edtpass.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                edtemail.setError(e.getMessage());
                                edtemail.requestFocus();
                            } catch(FirebaseAuthUserCollisionException e) {
                                edtemail.setError(e.getMessage());
                                edtemail.requestFocus();
                            } catch(Exception e) {
                                Log.e("jeno", e.getMessage());
                            }

                            //updateUI(null);
                        }

                    }
                });
    }
    private void AddRegisterData(String userid,String  name, String email, final String pass) {
        final Map<String, Object> User = new HashMap<>();
        User.put("Userid",userid);
        User.put("Name",name);
        User.put("Email",email);
        User.put("Phone","");
        User.put("Password", pass);
        User.put("Propic", "0");
        progressBar.setVisibility(View.VISIBLE);
                                db.collection("User")
                                        .add(User)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                progressBar.setVisibility(View.GONE);
                                                Log.i("jeno", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                Log.i("jeno", "Error adding document", e);
                                                Toast.makeText(RegisterActivity.this, "Registeration Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });


    }
}