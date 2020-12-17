package com.internationalhelper.internationalhelper;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.RankBy;

import java.io.IOException;

public class NearbySearch {
    private String APIKEY="AIzaSyCYTBfOGSpWZKSmBLfEMiFqmXAKuxKJB9k";
    public PlacesSearchResponse run(){
        PlacesSearchResponse request = new PlacesSearchResponse();
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(APIKEY)
                .build();
        LatLng location = new LatLng(43.6761, 79.4105);

        try {
            request = PlacesApi.nearbySearchQuery(context, location)
                    .radius(5000)
                    .rankby(RankBy.PROMINENCE)
                    .keyword("cruise")
                    .language("en")
                    .type(PlaceType.SCHOOL)
                    .await();
        } catch (ApiException|IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            return request;
        }
    }
}
