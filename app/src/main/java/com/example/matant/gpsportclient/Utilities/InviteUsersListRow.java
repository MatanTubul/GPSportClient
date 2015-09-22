package com.example.matant.gpsportclient.Utilities;

/**
 * Created by matant on 9/22/2015.
 */
public class InviteUsersListRow {
    private int imageId,imagestatus;
    private String title;
    private String desc;

    public InviteUsersListRow(int imageId,int status, String title,String desc){
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.imagestatus = status;
    }

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getImageStatus(){
        return this.imagestatus;
    }
    public void setImagestatus(int imgStatus)
    {
        this.imagestatus = imgStatus;
    }

}
