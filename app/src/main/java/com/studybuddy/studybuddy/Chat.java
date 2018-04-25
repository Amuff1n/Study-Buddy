package com.studybuddy.studybuddy;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
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

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uid = FirebaseAuth.getInstance().getUid();
        setDisplayName();
        getViews();
        chatListener = getChatListener();
    }

    private String getGroupId() {
        Intent intent = getIntent();
        return intent.getStringExtra("groupId");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            chatListener.remove();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setGroupName(DocumentReference groupRef) {
        Task<DocumentSnapshot> groupTask = groupRef.get();
        groupTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //noinspection ConstantConditions
                getSupportActionBar().setTitle((CharSequence) documentSnapshot.get("class"));
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
                    messageMap.put("message", messageText.trim());
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
                    addMessageBox( "You:", messageString, 1);
                } else {
                    addMessageBox(messageDisplayName + ":", messageString, 2);
                }
            }
        });
    }

    private void addMessageBox(String user, String message, int type) {
        View chatView;
        if (type == 1) {
            chatView = View.inflate(this, R.layout.message_sent, null);
        } else {
            chatView = View.inflate(this, R.layout.message_received, null);
        }
        TextView nameView = chatView.findViewById(R.id.message_user);
        TextView messageView = chatView.findViewById(R.id.message_text);
        nameView.setText(user);
        messageView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.START;
        } else {
            lp2.gravity = Gravity.END;
        }
        chatView.setLayoutParams(lp2);
        layout.addView(chatView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onBackPressed() {
        chatListener.remove();
        super.onBackPressed();
    }
}