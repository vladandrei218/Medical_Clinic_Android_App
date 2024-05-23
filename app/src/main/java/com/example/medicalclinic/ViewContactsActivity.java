package com.example.medicalclinic;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ViewContactsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Request> requests;
    RequestAdapter requestAdapter;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewcontacts);

        Button goBackButton = findViewById(R.id.goback);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        requests = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            requestAdapter = new RequestAdapter(ViewContactsActivity.this, requests, currentUserEmail);
            recyclerView.setAdapter(requestAdapter);
            EventChangeListener(currentUserEmail);
        }

        goBackButton.setOnClickListener(v -> finish());
    }

    private void EventChangeListener(String currentUserEmail) {
        db.collection("requests")
                .whereEqualTo("emailTo", currentUserEmail)
                .whereEqualTo("status", "accepted")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }
                        handleDocumentChanges(value);
                    }
                });

        db.collection("requests")
                .whereEqualTo("emailFrom", currentUserEmail)
                .whereEqualTo("status", "accepted")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }
                        handleDocumentChanges(value);
                    }
                });
    }

    private void handleDocumentChanges(QuerySnapshot value) {
        if (value != null) {
            Set<String> uniqueDocuments = new HashSet<>();
            for (DocumentChange dc : value.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    Request request = dc.getDocument().toObject(Request.class);
                    if (uniqueDocuments.add(dc.getDocument().getId())) {
                        requests.add(request);
                    }
                }
            }
            requestAdapter.notifyDataSetChanged();
        }
    }
}
