package com.example.matant.gpsportclient.DataClasses;

import android.graphics.Bitmap;

/**
 * Created by matant on 9/22/2015.
 */
public class InviteUsersListRow {
    private int imageId,imagestatus;
    private Bitmap imgProfile;
    private  String title;
    private  String desc,id,gender,age;

    public InviteUsersListRow(int status, String title,String desc,Bitmap i,String userid,String gen,String age){
        //this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.imagestatus = status;
        this.imgProfile = i;
        this.id = userid;
        this.gender = gen;
        this.age = age;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
