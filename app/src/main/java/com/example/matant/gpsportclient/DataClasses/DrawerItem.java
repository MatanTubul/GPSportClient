package com.example.matant.gpsportclient.DataClasses;

/**
 * this class demonstrate an individual row from the
 * main menu. each instance is an item in our main menu.
 * Created by matant on 8/24/2015.
 */
public class DrawerItem {

    public int icon;
    public String name;

    // Constructor.
    public DrawerItem(int icon, String name) {

        this.icon = icon;
        this.name = name;
    }
}
