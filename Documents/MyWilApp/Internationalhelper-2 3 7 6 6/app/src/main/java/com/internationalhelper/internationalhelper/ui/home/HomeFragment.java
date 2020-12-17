package com.internationalhelper.internationalhelper.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.internationalhelper.internationalhelper.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    GridLayout gridLayout;
    private ImageView imageinternationalcenter;
    private ImageView studentservices;
    private ImageView canadaimage;
    private ImageView covid19image;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
       // final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
               // textView.setText(s);
            }
        });
          imageinternationalcenter = root.findViewById(R.id.imageinternationalcenter );
          studentservices = root.findViewById(R.id.studentservices);
        canadaimage= root.findViewById(R.id.canadaimage);
        covid19image=root.findViewById(R.id.covid19image);

        imageinternationalcenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebURL("https://www.georgebrown.ca/international");
            }
        });

        studentservices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebURL("https://www.georgebrown.ca/current-students");
            }
        });
        canadaimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebURL("https://www.canada.ca/en/immigration-refugees-citizenship/services/study-canada.html");
            }
        });
        covid19image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebURL("https://www.canada.ca/en/immigration-refugees-citizenship/services/coronavirus-covid19.html");
            }
        });

        return root;
    }


    public void openWebURL( String inURL ) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );

        startActivity( browse );
    }


}