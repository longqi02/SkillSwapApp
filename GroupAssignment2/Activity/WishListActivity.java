package com.example.GroupAssignment2.Activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GroupAssignment2.Adapter.SkillAdapter;
import com.example.GroupAssignment2.Adapter.SkillMatchAdapter;
import com.example.GroupAssignment2.Domain.SkillDomain;
import com.example.GroupAssignment2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WishListActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private RecyclerView rv;
    private SkillMatchAdapter adapter;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        firestore = FirebaseFirestore.getInstance();
        auth      = FirebaseAuth.getInstance();

        rv = findViewById(R.id.rvWishlist);
        rv.setLayoutManager(new LinearLayoutManager(this));
        // we can reuse SkillMatchAdapter but hide button:
        adapter = new SkillMatchAdapter(s -> {});
        rv.setAdapter(adapter);

        loadWishlist();
    }

    private void loadWishlist() {

        if (auth.getCurrentUser() != null) {
            uid = auth.getCurrentUser().getUid();
        } else {
            uid = "user123"; // Default user ID if not logged in
        }
        firestore.collection("users")
                .document(uid)
                .collection("wishlist")
                .get()
                .addOnSuccessListener(qsnap -> {
                    List<SkillDomain> list = new ArrayList<>();
                    for (DocumentSnapshot d : qsnap) {
                        list.add(d.toObject(SkillDomain.class));
                    }
                    adapter.setItems(list);
                })
                .addOnFailureListener(e -> {
                    Log.e("WishlistSave", "Error saving skill", e);
                    Toast.makeText(this, "Failed to save: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
