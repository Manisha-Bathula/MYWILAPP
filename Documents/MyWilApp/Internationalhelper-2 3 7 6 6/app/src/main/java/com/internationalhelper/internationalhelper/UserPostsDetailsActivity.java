package com.internationalhelper.internationalhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.internationalhelper.internationalhelper.Adapters.PostAdapter;
import com.internationalhelper.internationalhelper.Adapters.UsersPostsDetailAdapter;
import com.internationalhelper.internationalhelper.Models.PostImages;
import com.internationalhelper.internationalhelper.Models.PostModel;

import java.util.ArrayList;

public class UserPostsDetailsActivity extends AppCompatActivity {
     //TextView title,des,price;
    RecyclerView images;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    private UsersPostsDetailAdapter adapter;
    private Button calldetails;
    private Button smsdetails;
    ArrayList<String> list = new ArrayList<String>();
    TextView titledetails,catagorydetails,descriptiondetails,pricedetails,locationdetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_posts_details);
//        title = findViewById(R.id.title);
       images = findViewById(R.id.images);
        titledetails = findViewById(R.id.titledetails);
        catagorydetails = findViewById(R.id.catagorydetails);
        descriptiondetails = findViewById(R.id.descriptiondetails);
        pricedetails =findViewById(R.id.pricedetails);
        locationdetails = findViewById(R.id.locationdetails);
        calldetails = findViewById(R.id.calldetails);
        smsdetails = findViewById(R.id.smsdetails);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(UserPostsDetailsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading...");
//        des =findViewById(R.id.des);
//        price = findViewById(R.id.price);
        Intent intent= getIntent();
        String pushkey =intent.getStringExtra("pushkey");
        String title=intent.getStringExtra("title");
        String description=intent.getStringExtra("description");
        String price=intent.getStringExtra("price");
        String location=intent.getStringExtra("location");
        String category=intent.getStringExtra("category");
        titledetails.setText(title);
        catagorydetails.setText("Category: "+category);
        pricedetails.setText("Price: "+price + "CAD");
        descriptiondetails.setText(description);
        locationdetails.setText(location);
        getUsersPostDetails(pushkey);

        calldetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallButton();

            }
        });

        smsdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsButton();
            }
        });
    }

    //Call method

    public void CallButton() {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: 5556"));
        startActivity(intent);
    }

    //Sms method
    public void SmsButton(){
        //call Intent to send sms
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "5556", null));
        intent.putExtra("sms_body", " ");
        startActivity(intent);

    }
    private void getUsersPostDetails(String pushkey){
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
                    if (postSnapshot.getKey().equals(pushkey)){
                        PostModel post = postSnapshot.getValue(PostModel.class);
                        postArray.add(post);
                    }

//                    title.setText(post.post_title);
//                    des.setText(post.post_description);
//                    price.setText(post.post_price);
                }
            for(PostModel item: postArray){

                for (PostImages items : item.imageUrl){
                    list.add(items.imageUrl);
                    Log.d("image", items.imageUrl);
                   }

            }
            //images.setLayoutManager(new LinearLayoutManager(UserPostsDetailsActivity.this));


             images.setLayoutManager(new GridLayoutManager(UserPostsDetailsActivity.this, 2, LinearLayoutManager.VERTICAL, false));
              // adapter = new UsersPostsDetailAdapter(UserPostsDetailsActivity.this,array);
                //adapter = new UsersPostsDetailAdapter(UserPostsDetailsActivity.this,postArray);
                adapter = new UsersPostsDetailAdapter(UserPostsDetailsActivity.this,list);
                images.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(UserPostsDetailsActivity.this,  String.valueOf(databaseError.toException()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}