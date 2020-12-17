package com.internationalhelper.internationalhelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    TextView tvRegister;
    EditText edtemail,edtpassword;
    Button btnLogin;


    FirebaseFirestore db;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(),homeMainActivity.class);
            startActivity(intent);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Create request() using google sign in
        createRequest();

        //Refrences
       tvRegister=findViewById(R.id.txt_register) ;
        edtemail=findViewById(R.id.edt_lemail);
        edtpassword=findViewById(R.id.edt_lpass);
        btnLogin=findViewById(R.id.btn_login);
        progressBar=findViewById(R.id.progressbarlogin);

        //Google sign in button
        findViewById(R.id.google_signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //database
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        //Firestore
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=edtemail.getText().toString().trim();
                String pass=edtpassword.getText().toString().trim();
                if (email.equals("")||pass.equals("")){
                    Toast.makeText(LoginActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    // getdata(email,pass);
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("vic", "signInWithEmail:success");
                                        Intent intent =new Intent(LoginActivity.this, homeMainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        // If sign in fails, display a message to the user.
                                        Log.w("vic", "signInWithEmail:failure", task.getException());
//
                                        try {
                                            throw task.getException();
                                        } catch(FirebaseAuthWeakPasswordException e) {
                                            // Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            edtpassword.setError(e.getMessage());
                                            edtpassword.requestFocus();
                                        } catch(FirebaseAuthInvalidCredentialsException e) {
                                            edtemail.setError(e.getMessage());
                                            edtemail.requestFocus();
                                        } catch(FirebaseAuthUserCollisionException e) {
                                            edtemail.setError(e.getMessage());
                                            edtemail.requestFocus();
                                        } catch(Exception e) {
                                            Log.e("jeno", e.getMessage());
                                            Toast.makeText(LoginActivity.this, e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        //updateUI(null);
                                    }

                                }
                            });
                }
            }
        });

    }

    // This code for lgin and registration
    private void getdata(String email, final String pass) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("User")
                .whereEqualTo("Email",email)
                //.whereEqualTo("Password",pass)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("jeno", "task"+task.getResult());
                        String name="";
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            //  for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!task.getResult().isEmpty()) {
                                // Log.d("jeno", "DocumentSnapshot data: " + document.getData());
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String pass1=document.get("Password").toString();
                                    if (pass1.equals(pass)){
                                        Intent intent = new Intent(LoginActivity.this,homeMainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(LoginActivity.this, "Succcessfully Logged In", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Log.d("jeno", "User not found");
                                Toast.makeText(LoginActivity.this, "user not found, please register", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.d("jeno", "Error getting documents: ", task.getException());

                        }
                    }
                });
    }



    //send the request for google sign in
    private void createRequest() {

        //Configure google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail() // specific user
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        //this is help to sign in
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("google sucess", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken()); // token id
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("google failed", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("gsucess", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(),homeMainActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("gfailed", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();


                        }

                        // ...
                    }
                });
    }
}