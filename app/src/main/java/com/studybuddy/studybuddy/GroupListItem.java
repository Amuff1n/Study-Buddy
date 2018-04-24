package com.studybuddy.studybuddy;

/**
 * Created by Alex on 3/20/2018.
 * This class contains the data fo the Groups Recyclerview
 */

public class GroupListItem {
    private String header;
    private String text;
    private int index;
    private int userIndex;
    private int maxUserIndex;
    private boolean isInGroup; //true if uid not associated with group,
    private String groupId;
    private String Class;
    private String Location_Name;

    public GroupListItem(String header, String text, String aClass,
                         String location_Name, Double index, int userIndex, Double maxUserIndex,
                         boolean isInGroup, String groupId) {
        this.header = header;
        this.text = text;
        Class = aClass;
        Location_Name = location_Name;
        this.index = index.intValue(); //Firestore uses double, have to cast to int
        this.userIndex = userIndex;
        this.maxUserIndex = maxUserIndex.intValue();
        this.isInGroup = isInGroup;
        this.groupId = groupId;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return index;
    }

    public int getUserIndex() {
        return userIndex;
    }

    public int getMaxUserIndex() {
        return maxUserIndex;
    }

    public boolean isInGroup() {
        return isInGroup;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getClassName() {
        return Class;
    }

    public String getLocation_Name() {
        return Location_Name;
    }
}
