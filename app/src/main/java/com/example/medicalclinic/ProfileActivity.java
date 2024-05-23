// ProfileActivity.java
package com.example.medicalclinic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private String name;
    private String email;
    private String role;
    private String details;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView nameEditText = findViewById(R.id.name);
        TextView emailEditText = findViewById(R.id.email);
        TextView roleEditText = findViewById(R.id.role);
        TextView detailsEditText = findViewById(R.id.details);
        Button goBackButton = findViewById(R.id.goback);
        Button updateDetailsButton = findViewById(R.id.update);
        updateDetailsButton.setVisibility(View.INVISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            email = currentUser.getEmail();
            emailEditText.setText(email);
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            name = task.getResult().getDocuments().get(0).getString("name");
                            role = task.getResult().getDocuments().get(0).getString("role");
                            nameEditText.setText(name);
                            roleEditText.setText(role);
                            if (role.equals("Doctor")) {
                                updateDetailsButton.setVisibility(View.VISIBLE);
                                details = task.getResult().getDocuments().get(0).getString("details");
                                if (details != null && !details.isEmpty()) {
                                    detailsEditText.setText(details);
                                } else {
                                    detailsEditText.setText("No details available");
                                }
                            }
                        }
                    });
        }

        goBackButton.setOnClickListener(v -> finish());

        updateDetailsButton.setOnClickListener(v -> {
            DialogFragment dialogFragment = new UpdateDetailsDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "UpdateDetailsDialog");
        });
    }
}
