package com.internationalhelper.internationalhelper.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.internationalhelper.internationalhelper.Models.PostImages;
import com.internationalhelper.internationalhelper.Models.PostModel;
import com.internationalhelper.internationalhelper.R;
import com.internationalhelper.internationalhelper.UpdatePostACtivity;
import com.internationalhelper.internationalhelper.UserPostsDetailsActivity;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;



public class UsersPostsDetailAdapter extends RecyclerView.Adapter<UsersPostsDetailAdapter.ViewHolder>  {
    public ArrayList<String> postArray;
   // public ArrayList<PostModel> postArray;
    ProgressDialog progressDialog;

    public Context activity;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {



        public ImageView users_post_images;
        public TextView titledetails;
        public TextView catagorydetails ;
        public TextView descriptiondetails;
        public TextView pricedetails;
        public TextView locationdetails;

        public ViewHolder(View v) {
            super(v);

            users_post_images = v.findViewById(R.id.users_post_images);
//            titledetails = v.findViewById(R.id.titledetails);
//            catagorydetails = v.findViewById(R.id.catagorydetails);
//            descriptiondetails = v.findViewById(R.id.descriptiondetails);
//            pricedetails = v.findViewById(R.id.pricedetails);
//            locationdetails = v.findViewById(R.id.locationdetails);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public UsersPostsDetailAdapter(Context context, ArrayList<String> PostData) {
        postArray = PostData;
        activity =context;
    }
    @Override
    public UsersPostsDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.user_posts_items_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//
//        Glide.with(activity)
//                .load(Uri.parse(String.valueOf(postArray.get(position).imageUrl.get(position).imageUrl)))
//              .into(holder.users_post_images);
               Glide.with(activity)
                .load(Uri.parse(String.valueOf(postArray.get(position))))
              .into(holder.users_post_images);
 //               holder.users_post_images.setImageResource(Integer.parseInt(postArray.get(position)));
//                holder.titledetails.setText(postArray.get(position).post_title);
//                holder.catagorydetails.setText(postArray.get(position).post_category);
//                holder.descriptiondetails.setText(postArray.get(position).post_description);
//                holder.pricedetails.setText(postArray.get(position).post_price);
//                holder.locationdetails.setText(postArray.get(position).post_location);


                //setTExt(postarray.push(String))
//        for(PostModel item : postArray) {
//            for (PostImages items : item.imageUrl){
//                Log.d("Image",items.imageUrl);
//                Glide.with(activity)
//                        .load(Uri.parse(items.imageUrl))
//                        .into(holder.users_post_images);
//            }
//
//        }

    }

    @Override
    public int getItemCount() {
        return postArray.size();
    }


}