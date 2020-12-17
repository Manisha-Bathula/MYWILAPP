package com.internationalhelper.internationalhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.internationalhelper.internationalhelper.Adapters.PostAdapter;
import com.internationalhelper.internationalhelper.Models.PostModel;

import java.util.ArrayList;

public class ManageActivity extends AppCompatActivity {
    RecyclerView postRecyclerView;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    private PostAdapter adapter;
    ImageView back_img;
    EditText editText_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        postRecyclerView = findViewById(R.id.post_recylerview);
        back_img=findViewById(R.id.btn_back);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(ManageActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading...");
        editText_search=findViewById(R.id.edittextsearch);
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(editText_search.getText().toString());
            }
        });

       // getSupportActionBar().setTitle("");
        fatchPostData();
    }
    void fatchPostData(){
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressDialog.show();
        Query ref =mDatabase.child("user_posts").orderByChild("user_id").equalTo(currentuser);
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                ArrayList<PostModel> postArray = new ArrayList<PostModel>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    PostModel post = postSnapshot.getValue(PostModel.class);
                    postArray.add(post);
                }
                postRecyclerView.setLayoutManager(new LinearLayoutManager(ManageActivity.this));
                 adapter = new PostAdapter(ManageActivity.this, 1,postArray);
                postRecyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(ManageActivity.this,  String.valueOf(databaseError.toException()), Toast.LENGTH_SHORT).show();
            }
        });

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search_menu, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        return true;
//    }
}