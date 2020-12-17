package com.internationalhelper.internationalhelper.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.internationalhelper.internationalhelper.R;

import java.io.IOException;
import java.util.ArrayList;


public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder>  {
    public ArrayList<String> selectedImagesArray;
    ProgressDialog progressDialog;

    public Context activity;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView selectedImages;
        public ViewHolder(View v) {
            super(v);

            selectedImages = v.findViewById(R.id.selectedImages);

        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public SelectedImagesAdapter(Context context,ArrayList<String> PostData) {
        selectedImagesArray = PostData;
        activity =context;
    }
    @Override
    public SelectedImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.selected_images_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.parse(selectedImagesArray.get(position)));
            holder.selectedImages.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Glide.with(activity)
//                .load(postArray.get(position))
//                .into(holder.selectedImages);
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
        return selectedImagesArray.size();
    }


}