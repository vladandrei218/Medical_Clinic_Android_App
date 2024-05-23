package com.example.medicalclinic;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileDialog extends DialogFragment {

    private String email;
    private FirebaseFirestore db;

    public ProfileDialog(String email) {
        this.email = email;
        this.db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_profile, container, false);

        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView emailTextView = view.findViewById(R.id.emailTextView);
        TextView roleTextView = view.findViewById(R.id.roleTextView);
        TextView detailsTextView = view.findViewById(R.id.detailsTextView);

        // Set the email
        emailTextView.setText(email);

        // Fetch user details from Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            nameTextView.setText(name);
                            String role = document.getString("role");
                            roleTextView.setText(role);
                            String details = document.getString("details");
                            detailsTextView.setText(details);

                        }
                    } else {
                        nameTextView.setText("User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    nameTextView.setText("Error fetching user details");
                });

        return view;
    }
}
