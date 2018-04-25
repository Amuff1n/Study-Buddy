package com.studybuddy.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.ArrayList;
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
        holder.setTextViewHeader(groupListItem.getHeader());
        holder.setTextViewText(groupListItem.getText());
        holder.setTextViewIndex(groupListItem.getIndex());
        holder.setIndex(groupListItem.getIndex());
        holder.setUserIndex(groupListItem.getUserIndex());
        holder.setMaxUserIndex(groupListItem.getMaxUserIndex());
        holder.setGroupId(groupListItem.getGroupId());

        //Toggle options based on group membership
        if (groupListItem.isInGroup()) {
            holder.joinGroupButton.setVisibility(View.GONE);
            holder.leaveGroupButton.setVisibility(View.VISIBLE);
            holder.chatButton.setVisibility(View.VISIBLE);
            holder.constraintLayout.setBackgroundColor(Color.rgb(30,237,71));
        }
        else {
            holder.leaveGroupButton.setVisibility(View.GONE);
            holder.joinGroupButton.setVisibility(View.VISIBLE);
            holder.chatButton.setVisibility(View.GONE);
            holder.constraintLayout.setBackgroundColor(Color.WHITE); //rgb(205,234,255)
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItems(List<GroupListItem> items){
        list = new ArrayList<>(items);
    }

    public GroupListItem removeItem(int position){
        final GroupListItem item = list.remove(position);
        notifyItemRemoved(position);
        return item;
    }

    public void addItem(int position, GroupListItem item){
        list.add(position,item);
        notifyItemInserted(position);
    }

    public void moveItem(int initPosition, int destination){
        final GroupListItem item = list.remove(initPosition);
        list.add(destination,item);
        notifyItemMoved(initPosition,destination);
    }

    public void animateTo(List<GroupListItem> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<GroupListItem> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final GroupListItem model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<GroupListItem> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final GroupListItem model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<GroupListItem> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final GroupListItem model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageButton chatButton;
        private TextView textViewHeader;
        private TextView textViewText;
        private TextView textViewIndex;
        private String groupId;
        private int index;
        private int userIndex;
        private int maxUserIndex;
        private ImageButton joinGroupButton;
        private ImageButton leaveGroupButton;
        private ConstraintLayout constraintLayout;
        private FirebaseAuth mAuth;
        private FirebaseFirestore db;

        public ViewHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.background);
            textViewHeader = itemView.findViewById(R.id.header);
            textViewText = itemView.findViewById(R.id.text);
            textViewIndex = itemView.findViewById(R.id.index);
            joinGroupButton = itemView.findViewById(R.id.join_group_button);
            leaveGroupButton = itemView.findViewById(R.id.leave_group_button);
            chatButton = itemView.findViewById(R.id.chat_button);

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            addButtonListeners();
        }

        public void setTextViewHeader(String text) {
            textViewHeader.setText(text);
        }

        public void setTextViewText(String text){
            textViewText.setText(text);
        }

        public void setTextViewIndex(int num) {
            textViewIndex.setText(String.valueOf(num));
        }

        public void setIndex(int num) {
            index = num;
        }

        public void setUserIndex(int num){
            userIndex = num;
        }

        public void setMaxUserIndex(int num) {
            maxUserIndex = num;
        }

        public void setGroupId(String text) {
            groupId = text;
        }

        //The following 3 methods are helper methods to add clickable join/leave group buttons
        private void addButtonListeners(){
            joinGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinGroup();
                }
            });

            leaveGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    leaveGroup();
                }
            });

            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChat();
                }
            });
        }

        private void openChat() {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra("groupId", groupId);
            context.startActivity(intent);
        }

        private void joinGroup() {
            DocumentReference groupDoc = db.collection("study_groups").document(groupId);
            Map<String, Object> newMember = new HashMap<>();
            //Add key "user+index" and increment index
            newMember.put("user" + maxUserIndex, mAuth.getUid());
            newMember.put("index", index + 1);
            newMember.put("maxUserIndex", maxUserIndex + 1);

            groupDoc.set(newMember, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Google Activity", "Success. User joined group.");
                    Toast.makeText(context, "Cool, you just joined a group!",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Google Activity", "Error. Could not add user to group.");
                    Toast.makeText(context, "Hmm there was a problem. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            });
            ((Home)context).refreshRecyclerView(); //calls refresh method in Home
        }

        private void leaveGroup() {
            DocumentReference groupDoc = db.collection("study_groups").document(groupId);
            Map<String, Object> leave = new HashMap<>();
            //first user has key 'user'
            //identified by kinda using a sentinel value userIndex == 0
            if (userIndex == 0) {
                leave.put("user", FieldValue.delete());
            }
            //otherwise we have to remove ''user' + userIndex'
            else {
                leave.put("user" + userIndex, FieldValue.delete());
            }
            leave.put("index", index - 1);
            //if new index is 0, no users in group so delete it
            if (index - 1 == 0) {
                groupDoc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Google Activity", "Group successfully deleted");
                        Toast.makeText(context, "Group deleted!",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Google Activity", "Error deleting group");
                        Toast.makeText(context, "Error deleting group",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
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
            ((Home)context).refreshRecyclerView();
        }
    }
}
