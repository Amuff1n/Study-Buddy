package com.studybuddy.studybuddy;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Chat extends AppCompatActivity {

    private LinearLayout layout;
    private EditText messageArea;
    private ScrollView scrollView;
    private CollectionReference chatRef;
    private String uid;
    private ListenerRegistration listenerRegistration;
    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        String groupId = intent.getStringExtra("groupId");

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference groupRef = db.collection("study_groups").document(groupId);
        chatRef = groupRef.collection("chat");
        Task<DocumentSnapshot> groupTask = groupRef.get();
        final TextView groupName = findViewById(R.id.group_name);
        groupTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                groupName.setText((CharSequence) documentSnapshot.get("class"));
            }
        });
        uid = FirebaseAuth.getInstance().getUid();

         db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 displayName = documentSnapshot.get("firstName").toString() + " " + documentSnapshot.get("lastName").toString();
             }
         });

        layout = findViewById(R.id.layout1);
//        layout_2 = findViewById(R.id.layout2);
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

        listenerRegistration = chatRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }
                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
                    switch (documentChange.getType()) {
                        case ADDED:
                            final String messageString = documentChange.getDocument().get("message").toString();
                            String messageUid = documentChange.getDocument().get("user").toString();
                            String messageDisplayName = documentChange.getDocument().get("displayName").toString();

                            if (messageUid.equals(uid)) {
                                addMessageBox("You:-\n" + messageString, 1);
                            } else {
                                addMessageBox(messageDisplayName + ":-\n" + messageString, 2);
                            }
                            break;
                        default:
                            break;
                    }
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
        listenerRegistration.remove();
    }
}