package com.example.instagram.ui.search;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {
    private String userName;
    private Uri Imageurl;
    private String Uid;

    public SearchViewModel() {
       
    }

    public SearchViewModel(String userName, Uri Imageurl, String Uid) {
        this.userName = userName;
        this.Imageurl = Imageurl;
        this.Uid = Uid;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Uri getImageurl() {
        return Imageurl;
    }

    public void setImageurl(Uri Imageurl) {
        this.Imageurl = Imageurl;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String Uid) {
        this.Uid = Uid;
    }

}