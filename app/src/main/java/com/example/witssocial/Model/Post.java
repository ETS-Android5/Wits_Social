package com.example.witssocial.Model;

import com.google.firebase.database.FirebaseDatabase;

public class Post {
    private String image;
    private String Caption;
    private String username;
    private static String postid;
    private  String profilePicture;

    public Post(String image, String caption, String username, String profilePicture) {
        this.image = image;
        this.Caption = caption;
        this.username = username;
        this.postid = postid;
        this.profilePicture = profilePicture;
    }

    public Post(){}

    public String getImage() {
        return image;
    }



    public String getCaption() {
        return Caption;
    }



    public String getUsername() {
        return username;
    }

    public static String getPostid() {
        return FirebaseDatabase.getInstance().getReference("User_post").getKey();
    }

    public String getProfilePicture(){return profilePicture;};


}

