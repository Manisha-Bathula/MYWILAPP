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
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.internationalhelper.internationalhelper.ExploreActivity;
import com.internationalhelper.internationalhelper.Models.PostModel;
import com.internationalhelper.internationalhelper.PostActivity;
import com.internationalhelper.internationalhelper.R;
import com.internationalhelper.internationalhelper.UpdatePostACtivity;
import com.internationalhelper.internationalhelper.UserPostsDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements getFilterItem {
    public ArrayList<PostModel> postArray;
    public ArrayList<PostModel> searchList;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;

    public Context activity;
   public int isUpdatePost;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView postDescription,postTitle,postTime,postPrice;
        public ImageView postImage,postDetails,trash,edit;
        public ViewHolder(View v) {
            super(v);
            layout = v;
            postDescription = v.findViewById(R.id.post_des);
            postTitle = v.findViewById(R.id.post_title);
            postTime = v.findViewById(R.id.post_time);
            postImage = v.findViewById(R.id.post_image);
            postDetails = v.findViewById(R.id.post_details);
            postPrice = v.findViewById(R.id.price);
            trash = v.findViewById(R.id.trash);
            edit = v.findViewById(R.id.edit);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(Context context, int isUpdate,ArrayList<PostModel> PostData) {
        postArray = PostData;
        activity =context;
        isUpdatePost =isUpdate;
        searchList = new ArrayList<PostModel>(PostData);
    }
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.post_items_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading...");
        holder.postDescription.setText(postArray.get(position).post_description);
        holder.postTitle.setText(postArray.get(position).post_title);
        holder.postTime.setText(postArray.get(position).upload_time);
        holder.postPrice.setText(postArray.get(position).post_price+" CAD");
        if(isUpdatePost == 1){
            holder.trash.setVisibility(VISIBLE);
            holder.edit.setVisibility(VISIBLE);
        }

        Glide.with(activity)
                .load(Uri.parse( postArray.get(position).imageUrl.get(0).imageUrl))
                .into(holder.postImage);
        holder.postDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//           if(isUpdatePost == 1){
//               Intent intent = new Intent(activity, UpdatePostACtivity.class);
//               intent.putExtra("pushkey",postArray.get(position).post_pushkey);
//               intent.putExtra("title",postArray.get(position).post_title);
//               intent.putExtra("description",postArray.get(position).post_description);
//               intent.putExtra("price",postArray.get(position).post_price);
//               intent.putExtra("location",postArray.get(position).post_location);
//               intent.putExtra("category",postArray.get(position).post_category);
//               intent.putExtra("image", postArray.get(position).imageUrl.get(0).imageUrl);
//               activity.startActivity(intent);
//           }else{
               Intent intent = new Intent(activity, UserPostsDetailsActivity.class);
               intent.putExtra("pushkey",postArray.get(position).post_pushkey);
                intent.putExtra("title",postArray.get(position).post_title);
                intent.putExtra("description",postArray.get(position).post_description);
                intent.putExtra("price",postArray.get(position).post_price);
                intent.putExtra("location",postArray.get(position).post_location);
                intent.putExtra("category",postArray.get(position).post_category);
                activity.startActivity(intent);
           //}
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, UpdatePostACtivity.class);
                intent.putExtra("pushkey",postArray.get(position).post_pushkey);
                intent.putExtra("title",postArray.get(position).post_title);
                intent.putExtra("description",postArray.get(position).post_description);
                intent.putExtra("price",postArray.get(position).post_price);
                intent.putExtra("location",postArray.get(position).post_location);
                intent.putExtra("category",postArray.get(position).post_category);
                intent.putExtra("image", postArray.get(position).imageUrl.get(0).imageUrl);
                activity.startActivity(intent);

            }
        });
        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isUpdatePost == 1){
                    progressDialog.show();

                    mDatabase.child("user_posts").child(postArray.get(position).post_pushkey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();

                            //Toast.makeText(activity, "Post has been created", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }


    @Override
    public int getItemCount() {
        return postArray.size();
    }


    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<PostModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(searchList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (PostModel item : searchList) {
                    if (item.post_title.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            postArray.clear();
            postArray.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
