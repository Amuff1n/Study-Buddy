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
    private boolean joining; //true if uid not associated with group, i.e. can be joined, false otherwise
    private String groupId;

    public GroupListItem(String header, String text, Double index, int userIndex, boolean joining, String groupId) {
        this.header = header;
        this.text = text;
        this.index = index.intValue(); //Firestore numbers are doubles, have to cast to int
        this.userIndex = userIndex;
        this.joining = joining;
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

    public boolean getJoining() {
        return joining;
    }

    public String getGroupId() {
        return groupId;
    }
}
