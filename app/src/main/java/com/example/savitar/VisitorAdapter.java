package com.example.savitar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VisitorAdapter extends FirestoreRecyclerAdapter<Visitor, VisitorAdapter.VisitorHolder> {
    private List<Visitor> exampleList;
    public static final String TAG = "VisitorAdapter";
    private OnItemClickListener listener;
    public VisitorAdapter(@NonNull FirestoreRecyclerOptions<Visitor> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull VisitorHolder holder, int position, @NonNull Visitor model) {
        holder.setHolderColor(model);
        holder.textViewVisitorName.setText(model.getName());
        holder.textViewLicencePlates.setText(model.getLicensePlates());
        holder.textViewHostName.setText(model.getHostName());
        holder.testViewHostAddress.setText(model.getHostAddress());
        holder.textViewHostPhoneNumber.setText(model.getHostPhoneNo());
    }

    @NonNull
    @Override
    public VisitorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.visitor_item, parent, false);
        return new VisitorHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class VisitorHolder extends RecyclerView.ViewHolder{
        TextView textViewVisitorName, textViewLicencePlates, textViewHostName, testViewHostAddress, textViewHostPhoneNumber;

        public VisitorHolder(@NonNull View itemView) {
            super(itemView);
            textViewVisitorName = itemView.findViewById(R.id.list_visitor_name);
            textViewLicencePlates = itemView.findViewById(R.id.list_licence_plates);
            textViewHostName = itemView.findViewById(R.id.list_host_name);
            testViewHostAddress = itemView.findViewById(R.id.list_host_address);
            textViewHostPhoneNumber = itemView.findViewById(R.id.list_host_phone_number);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }

        public void setHolderColor(Visitor model){
            if(model.isAllowEntrance()){
                itemView.setBackgroundColor(Color.rgb(63,214,131));
            }else{
                itemView.setBackgroundColor(Color.rgb(231,70,48));
            }
        }

    }


    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
