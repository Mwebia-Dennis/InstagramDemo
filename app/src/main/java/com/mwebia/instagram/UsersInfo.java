package com.mwebia.instagram;


import android.net.Uri;

public class UsersInfo {

    public Uri uri;
    public String name;


    public UsersInfo(Uri uri,String username){
        this.uri = uri;
        this.name = username;
    }


}
