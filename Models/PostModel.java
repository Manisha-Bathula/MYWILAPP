package com.internationalhelper.internationalhelper.Models;


public class PostModel {
    public  String post_title;
    public  String post_category;
    public  String post_description;
    public  String post_location;
    public  String post_price;
    public  String post_image;
    public  String post_pushkey;
    public  String upload_time;
    public  String user_id;
    PostModel(){};
      public PostModel(String post_title, String post_category, String post_description, String post_location, String post_price, String post_image,String post_pushkey,String upload_time,String user_id){
        this.post_title = post_title;
        this.post_category = post_category;
        this.post_description = post_description;
        this.post_location = post_location;
        this.post_price = post_price;
        this.post_image = post_image;
        this.post_pushkey =post_pushkey;
        this.upload_time =upload_time;
        this.user_id = user_id;
    }

}
