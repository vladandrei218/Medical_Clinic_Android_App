package com.example.medicalclinic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    Context context;
    ArrayList<Request> requests;
    String currentUserEmail;
    FirebaseFirestore db;

    public RequestAdapter(Context context, ArrayList<Request> requests, String currentUserEmail) {
        this.context = context;
        this.requests = requests;
        this.currentUserEmail = currentUserEmail;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RequestAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.request, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.RequestViewHolder holder, int position) {
        Request request = requests.get(position);

        String otherEmail = request.getEmailFrom().equals(currentUserEmail) ? request.getEmailTo() : request.getEmailFrom();
        holder.email.setText(otherEmail);
        holder.sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(otherEmail);
            }
        });

        holder.viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof FragmentActivity) {
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    ProfileDialog dialog = new ProfileDialog(otherEmail);
                    dialog.show(fragmentManager, "profile");
                } else {
                    Toast.makeText(context, "Error showing profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Query 1: emailFrom = currentUserEmail and emailTo = otherEmail
                Query query1 = db.collection("requests")
                        .whereEqualTo("emailFrom", currentUserEmail)
                        .whereEqualTo("emailTo", otherEmail)
                        .whereEqualTo("status", "accepted");

                // Query 2: emailFrom = otherEmail and emailTo = currentUserEmail
                Query query2 = db.collection("requests")
                        .whereEqualTo("emailFrom", otherEmail)
                        .whereEqualTo("emailTo", currentUserEmail)
                        .whereEqualTo("status", "accepted");

                query1.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                    boolean found = false;
                    if (!queryDocumentSnapshots1.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots1) {
                            deleteDocument(document.getId(), holder.getAdapterPosition());
                            found = true;
                        }
                    }
                    // If no documents found in query1, execute query2
                    if (!found) {
                        query2.get().addOnSuccessListener(queryDocumentSnapshots2 -> {
                            if (!queryDocumentSnapshots2.isEmpty()) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots2) {
                                    deleteDocument(document.getId(), holder.getAdapterPosition());
                                }
                            } else {
                                Toast.makeText(context, "No matching contact found", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, "Error querying contacts", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error querying contacts", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deleteDocument(String documentId, int position) {
        db.collection("requests").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show();
                    requests.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error deleting contact", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + email));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Message");

        if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(emailIntent);
        } else {
            context.startActivity(emailIntent);
        }
    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView email;
        Button sendEmail, viewProfile;
        ImageButton deleteContact;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.email);
            sendEmail = itemView.findViewById(R.id.sendemail);
            viewProfile = itemView.findViewById(R.id.viewprofile);
            deleteContact = itemView.findViewById(R.id.delete_button);
        }
    }
}
