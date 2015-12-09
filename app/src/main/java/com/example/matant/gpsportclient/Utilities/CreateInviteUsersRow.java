package com.example.matant.gpsportclient.Utilities;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.matant.gpsportclient.R;

/**
 * class that describe item in the Invited Users list.
 * Created by matant on 10/12/2015.
 */
public class CreateInviteUsersRow {

    private String name,mobile,id,gender,age;
    private int status;
    private int imgViewUserError;

    public CreateInviteUsersRow(String n,String m ,int s,String userid,String gen,String age){
        this.name = n;
        this.mobile = m;
        this.status = s;
        this.id = userid;
        this.gender = gen;
        this.age = age;

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public int getImgViewUserError() {
        return imgViewUserError;
    }

    public void setImgViewUserError(int imgViewUserError) {
        this.imgViewUserError = imgViewUserError;
    }
}
