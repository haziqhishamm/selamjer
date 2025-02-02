package com.example.selamjer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Homepage extends AppCompatActivity {

    Button btn_viewListing, btn_addListing, btn_sellerLocation, btn_aboutUs;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        btn_viewListing = findViewById(R.id.btn_viewListing);
        btn_addListing = findViewById(R.id.btn_addListing);
        btn_sellerLocation = findViewById(R.id.btn_sellerLocation);
        btn_aboutUs = findViewById(R.id.btn_about_us); // About Us button

        btn_viewListing.setOnClickListener(v ->
                startActivity(new Intent(Homepage.this, ViewListing.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK))
        );

        String restName = "";
        btn_addListing.setOnClickListener(v -> createDialog(restName));

        btn_sellerLocation.setOnClickListener(v ->
                startActivity(new Intent(Homepage.this, ShowSellerLocation.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK))
        );

        btn_aboutUs.setOnClickListener(v ->
                startActivity(new Intent(Homepage.this, AboutUs.class)) // Navigate to About Us
        );

        progressDialog = new ProgressDialog(Homepage.this);
    }

    private void createDialog(String restName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);
        View view = LayoutInflater.from(Homepage.this).inflate(R.layout.add_listing, null, false);
        builder.setView(view);
        EditText et_itemName = view.findViewById(R.id.et_itemname);
        EditText et_itemCategory = view.findViewById(R.id.et_itemcategory);
        EditText et_itemCondition = view.findViewById(R.id.et_itemcondition);
        EditText et_itemPrice = view.findViewById(R.id.et_itemprice);
        Button btn_addService = view.findViewById(R.id.btn_addListing);
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
        btn_addService.setOnClickListener(v -> {
            String itemName = et_itemName.getText().toString();
            String itemCategory = et_itemCategory.getText().toString();
            String itemCondition = et_itemCondition.getText().toString();
            String itemPrice = et_itemPrice.getText().toString();

            if (itemName.isEmpty() || itemCategory.isEmpty() || itemCondition.isEmpty() || itemPrice.isEmpty()) {
                Toast.makeText(Homepage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setMessage("Adding Listing");
                progressDialog.setTitle("Adding...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                addListingToFirestore(itemName, itemCategory, itemCondition, itemPrice, restName);
                alertDialog.dismiss();
            }
        });
    }

    private void addListingToFirestore(String itemName, String itemCategory, String itemCondition, String itemPrice, String restName) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = firebaseUser.getUid();

        CollectionReference listingCollection = db.collection("listing");
        DocumentReference newServiceRef = listingCollection.document();

        Map<String, Object> data = new HashMap<>();
        data.put("id", userId);
        data.put("itemName", itemName);
        data.put("itemCategory", itemCategory);
        data.put("itemCondition", itemCondition);
        data.put("itemPrice", itemPrice);
        data.put("restName", restName);

        newServiceRef.set(data).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(Homepage.this, "Listing added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Homepage.this, "Failed to add listing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, UserLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
