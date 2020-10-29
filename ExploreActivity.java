package com.internationalhelper.internationalhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
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

public class ExploreActivity extends AppCompatActivity {
     RecyclerView postRecyclerView;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        postRecyclerView = findViewById(R.id.post_recylerview);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(ExploreActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading...");
        getSupportActionBar().setTitle("");
        fatchPostData();
    }
    void fatchPostData(){
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressDialog.show();
        //mDatabase.child("user_posts").child(currentuser);

        Query ref =mDatabase.child("user_posts");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                ArrayList<PostModel> postArray = new ArrayList<PostModel>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    PostModel post = postSnapshot.getValue(PostModel.class);
                    postArray.add(post);
                }
                postRecyclerView.setLayoutManager(new LinearLayoutManager(ExploreActivity.this));
                adapter = new PostAdapter(ExploreActivity.this,0, postArray);
                postRecyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(ExploreActivity.this,  String.valueOf(databaseError.toException()), Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

}