package com.example.medicalclinic;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_requests);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button goBackButton = findViewById(R.id.goback);
        Button acceptButton = findViewById(R.id.accept);
        Button declineButton = findViewById(R.id.decline);
        TextView requestText = findViewById(R.id.email);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            db.collection("requests")
                    .whereEqualTo("emailTo", currentUser.getEmail())
                    .whereEqualTo("status", "pending")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> requests = task.getResult().getDocuments();
                            if (requests.size() == 0) {
                                requestText.setText("No requests found.");
                                acceptButton.setVisibility(View.INVISIBLE);
                                declineButton.setVisibility(View.INVISIBLE);
                            }
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                String from_email = document.getString("emailFrom");
                                requestText.setText(from_email);
                                acceptButton.setVisibility(View.VISIBLE);
                                declineButton.setVisibility(View.VISIBLE);
                                String requestId = document.getId();

                                acceptButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db.collection("requests").document(requestId)
                                                .update("status", "accepted")
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d(TAG, "Status updated successfully.");
                                                    finish();
                                                    startActivity(getIntent());
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Error updating status: ", e);
                                                });
                                    }
                                });

                                declineButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db.collection("requests").document(requestId)
                                                .update("status", "declined")
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d(TAG, "Status updated successfully.");
                                                    finish();
                                                    startActivity(getIntent());
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Error updating status: ", e);
                                                });
                                    }
                                });
                            }

                        } else {
                            Log.d(TAG, "Error getting requests: ", task.getException());
                        }
                    });
        } else {
            Log.d(TAG, "No user logged in.");
        }


        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
