package com.studybuddy.studybuddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 3/20/2018.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<GroupListItem> list;
    private Context context;

    public GroupListAdapter(List<GroupListItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupListItem groupListItem = list.get(position);
        //TODO this is causing the app to crash... not sure why
        holder.setTextViewHeader(groupListItem.getHeader());
        holder.setTextViewText(groupListItem.getText());
        holder.setIndex(groupListItem.getIndex());
        holder.setUserIndex(groupListItem.getUserIndex());
        holder.setJoining(groupListItem.getJoining());
        holder.setGroupId(groupListItem.getGroupId());
        //System.out.println(groupListItem.getHeader());
        //System.out.println(groupListItem.getText());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewHeader;
        private TextView textViewText;
        private String groupId;
        private double index;
        private double userIndex;
        private Boolean joining = false;
        private ImageButton joinGroupButton;
        private ImageButton leaveGroupButton;
        private FirebaseAuth mAuth;
        private FirebaseFirestore db;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.header);
            textViewText = itemView.findViewById(R.id.text);
            joinGroupButton = itemView.findViewById(R.id.join_group_button);
            leaveGroupButton = itemView.findViewById(R.id.leave_group_button);

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            //TODO 'joining' isn't getting defined somewhere so this crashes without manually defining
            if (joining) {
                leaveGroupButton.setVisibility(itemView.GONE);
                joinGroupButton.setVisibility(itemView.VISIBLE);
            }
            else {
                joinGroupButton.setVisibility(itemView.GONE);
                leaveGroupButton.setVisibility(itemView.VISIBLE);
            }

            joinGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference groupDoc = db.collection("study_groups").document(groupId);
                    Map<String, Object> join = new HashMap<>();
                    join.put("user" + index, mAuth.getUid());
                    join.put("index", index + 1.0);

                    groupDoc.set(join, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Google Activity", "Successfully joined user to group");
                            Toast.makeText(context, "Joined group!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Google Activity", "Error joining group");
                            Toast.makeText(context, "Error joining group",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            leaveGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference groupDoc = db.collection("study_groups").document(groupId);
                    Map<String, Object> leave = new HashMap<>();
                    if (userIndex == 0.0) {
                        leave.put("user", FieldValue.delete());

                    }
                    else {
                        leave.put("user" + userIndex, FieldValue.delete());
                    }
                    leave.put("index", index - 1.0);
                    //TODO check if index == 0, if so, delete group
                    groupDoc.update(leave).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Google Activity", "User successfully left group");
                            Toast.makeText(context, "Left group",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Google Activity", "Error leaving group");
                            Toast.makeText(context, "Error leaving group",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            //TODO FORCE REFRESH OF VIEWING GROUPS SCREEN
        }
        public void setTextViewHeader(String text) {
            textViewHeader.setText(text);
        }

        public void setTextViewText(String text){
            textViewText.setText(text);
        }

        public void setIndex(double num) {
            index = num;
        }

        public void setUserIndex(double num){
            userIndex = num;
        }

        public void setJoining(boolean join) {
            joining = join;
        }

        public void setGroupId(String text) {
            groupId = text;
        }

    }
}
