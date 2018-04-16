package com.studybuddy.studybuddy;

/**
 * Created by Alex on 3/20/2018.
 * This class contains the data fo the Groups Recyclerview
 */

public class GroupListItem {
    private String header;
    private String text;
    private String Class;
    private String Location_Name;

    public GroupListItem(String header, String text, String aClass, String location_Name) {
        this.header = header;
        this.text = text;
        Class = aClass;
        Location_Name = location_Name;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }

    public String getClassName() {
        return Class;
    }

    public String getLocation_Name() {
        return Location_Name;
    }
}
