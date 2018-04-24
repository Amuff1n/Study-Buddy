package com.studybuddy.studybuddy;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private LinearLayout layout;
    private EditText messageArea;
    private ScrollView scrollView;
    private CollectionReference chatRef;
    private String uid;
    private ListenerRegistration chatListener;
    private String displayName;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String groupId = getGroupId();

        firestore = FirebaseFirestore.getInstance();
        DocumentReference studyGroup = firestore.collection("study_groups").document(groupId);
        chatRef = studyGroup.collection("chat");
        setGroupName(studyGroup);

        uid = FirebaseAuth.getInstance().getUid();
        setDisplayName();
        getViews();
        chatListener = getChatListener();
    }

    private String getGroupId() {
        Intent intent = getIntent();
        return intent.getStringExtra("groupId");
    }

    private void setGroupName(DocumentReference groupRef) {
        Task<DocumentSnapshot> groupTask = groupRef.get();
        final TextView groupName = findViewById(R.id.group_name);
        groupTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                groupName.setText((CharSequence) documentSnapshot.get("class"));
            }
        });
    }


    private void setDisplayName() {
        firestore.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                displayName = documentSnapshot.get("firstName").toString() + " " + documentSnapshot.get("lastName").toString();
            }
        });
    }

    private void getViews() {
        layout = findViewById(R.id.layout1);
        ImageView sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    Map<String, String> messageMap = new HashMap<>();
                    messageMap.put("message", messageText);
                    messageMap.put("user", uid);
                    messageMap.put("displayName", displayName);
                    chatRef.add(messageMap);
                    messageArea.setText("");
                }
            }
        });
    }

    @NonNull
    private ListenerRegistration getChatListener() {
        return chatRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot chatMessages, FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }
                for (DocumentChange chatMessageChange : chatMessages.getDocumentChanges()) {
                    switch (chatMessageChange.getType()) {
                        case ADDED:
                            addMessage(chatMessageChange.getDocument());
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                        default:
                            break;
                    }
                }
            }

            private void addMessage(DocumentSnapshot chatMessage) {
                final String messageString = chatMessage.get("message").toString();
                String messageUid = chatMessage.get("user").toString();
                String messageDisplayName = chatMessage.get("displayName").toString();

                if (messageUid.equals(uid)) {
                    addMessageBox("You:-\n" + messageString, 1);
                } else {
                    addMessageBox(messageDisplayName + ":-\n" + messageString, 2);
                }
            }
        });
    }

    private void addMessageBox(String message, int type) {
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.START;
            textView.setBackgroundResource(R.drawable.common_google_signin_btn_icon_dark_focused);
        } else {
            lp2.gravity = Gravity.END;
            textView.setBackgroundResource(R.drawable.common_google_signin_btn_icon_dark);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        chatListener.remove();
    }
}