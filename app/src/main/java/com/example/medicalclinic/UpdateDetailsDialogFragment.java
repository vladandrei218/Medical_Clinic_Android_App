// UpdateDetailsDialogFragment.java
package com.example.medicalclinic;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateDetailsDialogFragment extends DialogFragment {

    private EditText detailsEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Update Details");

        detailsEditText = new EditText(getContext());
        builder.setView(detailsEditText);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newDetails = detailsEditText.getText().toString().trim();
                if (!newDetails.isEmpty()) {
                    updateDetailsInDatabase(newDetails);
                } else {
                    Toast.makeText(getContext(), "Details cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    private void updateDetailsInDatabase(String newDetails) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            String userId = task.getResult().getDocuments().get(0).getId();
                            db.collection("users").document(userId)
                                    .update("details", newDetails)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Details updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating details", Toast.LENGTH_SHORT).show());
                        }
                    });
        }
    }
}
