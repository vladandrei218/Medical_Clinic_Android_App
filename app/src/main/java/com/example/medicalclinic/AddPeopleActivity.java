package com.example.medicalclinic;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AddPeopleActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_people);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText emailEditText = findViewById(R.id.email);
        Button sendRequestButton = findViewById(R.id.add);
        Button goBackButton = findViewById(R.id.goback);

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFriendRequest(emailEditText.getText().toString().trim());
            }
        });
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void sendFriendRequest(String email) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();

            if (email.isEmpty()) {
                Toast.makeText(AddPeopleActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                db.collection("requests")
                                        .whereEqualTo("emailFrom", currentUserEmail)
                                        .whereEqualTo("emailTo", email)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                    Toast.makeText(AddPeopleActivity.this, "Friend request already sent", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Map<String, Object> request = new HashMap<>();
                                                    request.put("emailFrom", currentUserEmail);
                                                    request.put("emailTo", email);
                                                    request.put("status", "pending");

                                                    db.collection("requests")
                                                            .add(request)
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(AddPeopleActivity.this, "Friend request sent successfully", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    } else {
                                                                        Toast.makeText(AddPeopleActivity.this, "Failed to send friend request", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(AddPeopleActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(AddPeopleActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
