package com.example.matant.gpsportclient.Utilities;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by matant on 9/22/2015.
 */
public class InviteUsersListRow {
    private int imageId,imagestatus;
    private Bitmap imgProfile;
    private  String title;
    private  String desc;

    public InviteUsersListRow(int status, String title,String desc,Bitmap i){
        //this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.imagestatus = status;
        this.imgProfile = i;
    }

    public  int getImageId() {
        return this.imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public  String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public  String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public  int getImageStatus(){
        return this.imagestatus;
    }
    public void setImagestatus(int imgStatus)
    {
        this.imagestatus = imgStatus;
    }

    public Bitmap getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(Bitmap imgProfile) {
        this.imgProfile = imgProfile;
    }
}
