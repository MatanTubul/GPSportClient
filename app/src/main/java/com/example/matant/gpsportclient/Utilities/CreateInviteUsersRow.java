package com.example.matant.gpsportclient.Utilities;

import android.widget.TextView;

/**
 * class that describe item in the Invited Users list.
 * Created by matant on 10/12/2015.
 */
public class CreateInviteUsersRow {

    private String name,mobile,id;
    private int status;

    public CreateInviteUsersRow(String n,String m ,int s,String userid){
        this.name = n;
        this.mobile = m;
        this.status = s;
        this.id = userid;
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
}
