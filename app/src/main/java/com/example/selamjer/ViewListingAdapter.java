package com.example.selamjer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selamjer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewListingAdapter extends RecyclerView.Adapter<ViewListingAdapter.viewHolder> {
    private Context mContext;
    private List<ListingModel> mList = new ArrayList<>();

    private ProgressDialog progressDialog;

    public ViewListingAdapter(Context mContext, List<ListingModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
        progressDialog = new ProgressDialog(mContext);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_listing, parent, false);
        return new viewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ListingModel model = mList.get(position);
        holder.tv_itemname.setText(model.getItemName());
        holder.tv_itemcategory.setText(model.getItemCategory());
        holder.tv_itemcondition.setText(model.getItemCondition());
        holder.tv_itemprice.setText(model.getItemPrice());

        holder.btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Complete");
                progressDialog.setTitle("Complete....");
                progressDialog.show();
                markAsComplete(model);
            }
        });

    }

    public void markAsComplete(ListingModel model) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = firebaseUser.getUid();

        // Assuming 'services' is your Firestore collection name
        DocumentReference documentReference = db.collection("services").document(model.getItemName());

        Map<String, Object> data = new HashMap<>();
        data.put("id", userId);
        data.put("Name", model.getItemName());
        data.put("cost", model.getItemPrice());

        documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        private TextView tv_itemname;
        private TextView tv_itemprice;
        private TextView tv_itemcategory;
        private TextView tv_itemcondition;
        private Button btn_complete;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            tv_itemname = itemView.findViewById(R.id.tv_itemname);
            tv_itemprice = itemView.findViewById(R.id.tv_itemprice);
            tv_itemcategory = itemView.findViewById(R.id.tv_itemcategory);
            tv_itemcondition = itemView.findViewById(R.id.tv_itemcondition);

            btn_complete = itemView.findViewById(R.id.btn_complete);


        }
    }
}
