package com.example.medicalclinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MenuActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    Button viewRequests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button logoutButton = findViewById(R.id.logout);
        TextView welcomeText = findViewById(R.id.welcomeText);
        viewRequests = findViewById(R.id.viewrequests);



        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").whereEqualTo("email", currentUser.getEmail()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString("name");
                                    if(document.getString("role").equals("Doctor"))
                                    {
                                        welcomeText.setText("Welcome, Dr. " + name + "!");
                                    }

                                    else if(document.getString("role").equals("Patient"))
                                    {
                                        welcomeText.setText("Welcome, " + name + "!");
                                    }
                                    else welcomeText.setText("Welcome, 1 " + name + "!");

                                }
                            }
                        }
                    });
        }
        Button addPeopleButton = findViewById(R.id.addpeople);
        addPeopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AddPeopleActivity.class);
                startActivity(intent);
            }
        });
        updateRequestCount();
        viewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRequestCount();
                Intent intent = new Intent(MenuActivity.this, ViewRequestsActivity.class);
                startActivity(intent);
            }
        });
        updateRequestCount();
        Button viewContactsButton = findViewById(R.id.viewcontacts);

        viewContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ViewContactsActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void onAvatarButtonClick(View view) {
        // Redirect to another activity
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void updateRequestCount() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("requests")
                .whereEqualTo("status", "pending")
                .whereEqualTo("emailTo", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int requestCount = task.getResult().size();
                        viewRequests.setText("View Requests (" + requestCount + ")");
                    } else {
                        viewRequests.setText("View Requests");
                    }
                });
}
}
