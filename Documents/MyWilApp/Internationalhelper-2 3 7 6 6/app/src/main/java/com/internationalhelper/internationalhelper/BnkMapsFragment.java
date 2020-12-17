package com.internationalhelper.internationalhelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BnkMapsFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    private double currentlat = 43.6752, currentlong = -79.3944;
    GoogleMap map;
    private String APIKEY = "AIzaSyA_skmrdkKZJW3RyYUsupkQiOppOrQtPQE";


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bnk_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.nav_Bnk);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(callback);
//        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        getCurrentLocation();
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
//                    currentlat = location.getLatitude();
//                    currentlong = location.getLongitude();
                    supportMapFragment =
                            (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.nav_Bnk);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(BnkMapsFragment.this);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(currentlat, currentlong);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);

        Places.initialize(getActivity(), APIKEY);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + currentlat + "," + currentlong + "&radius=5000" + "&types=" + "banks|ATM" + "&keyword=money" +
                "&sensor=true" + "&key=" + APIKEY;
//        https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=48.86459,2.36867&radius=500&rankby=prominence&types=food|restaurant&keyword=burger&key=YOUR-KEY
        new getServiceCanadaPlaces().execute(url);

    }

    private class getServiceCanadaPlaces extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadurl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);

        }
    }

    private String downloadurl(String string) throws IOException {

        URL url = new URL(string);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.connect();

        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();

        String line = "";
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        String data = builder.toString();
        reader.close();

        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {

            JsonParser jsonParser = new JsonParser();
            JSONObject object = null;
            List<HashMap<String, String>> mapList = null;
            try {
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            map.clear();

            for (int i = 0; i < hashMaps.size(); i++) {

                HashMap<String, String> hashmaplist = hashMaps.get(i);
                double lat = Double.parseDouble(hashmaplist.get("lat"));
                double lng = Double.parseDouble(hashmaplist.get("lng"));

                String name = hashmaplist.get("name");

                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                map.addMarker(options);

            }
        }
    }

    public class JsonParser {

        private HashMap<String, String> parseJsonObject(JSONObject object) {
//            JSONObject object = null;
            HashMap<String, String> datalist = new HashMap<>();
            try {
                String name = object.getString("name");
                String latitude = object.getJSONObject("geometry").getJSONObject("location")
                        .getString("lat");
                String longitude = object.getJSONObject("geometry").getJSONObject("location").getString("lng");
                datalist.put("name", name);
                datalist.put("lat", latitude);
                datalist.put("lng", longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return datalist;
        }

        private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray) {

            List<HashMap<String, String>> datalist = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {

                try {
                    HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                    datalist.add(data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return datalist;
        }


        public List<HashMap<String, String>> parseResult(JSONObject object) {

            JSONArray jsonArray = null;
            try {
                jsonArray = object.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return parseJsonArray(jsonArray);
        }
    }
}
