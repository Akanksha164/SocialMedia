package com.example.instagram.ui.dashboard;

import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private String ImageUrl;
    private String caption;

    public DashboardViewModel() {

    }

    public DashboardViewModel(String ImageUrl,String caption) {
        this.ImageUrl = ImageUrl;
        this.caption = caption;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}