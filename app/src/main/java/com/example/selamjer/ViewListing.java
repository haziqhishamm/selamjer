
package com.example.selamjer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selamjer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

// Import statements

public class ViewListing extends AppCompatActivity {

    private List<ListingModel> mList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ViewListingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_listing);

        recyclerView = findViewById(R.id.rv_showAllListing);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewListing.this));

        // Initialize and set up the adapter
        mAdapter = new ViewListingAdapter(ViewListing.this, mList);
        recyclerView.setAdapter(mAdapter);

        getAllListings();
    }

    private void getAllListings() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference listingsRef = db.collection("listing");

            listingsRef.whereEqualTo("id", firebaseUser.getUid())

                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                mList.clear();
                                for (DocumentSnapshot document : task.getResult()) {
                                    ListingModel model = document.toObject(ListingModel.class);
                                    mList.add(model);
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ViewListing.this, "No listings found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                Toast.makeText(ViewListing.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    });
        } else {
            // Handle case where the user is not authenticated
            Toast.makeText(ViewListing.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {

        // Create an Intent to navigate back to ServiceDashboard
        Intent intent = new Intent(this, Homepage.class);

        // Add any additional flags if needed
        // For example, to clear the back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Start ServiceDashboard
        startActivity(intent);

        // Finish the current activity to remove it from the back stack
        finish();
    }

}
