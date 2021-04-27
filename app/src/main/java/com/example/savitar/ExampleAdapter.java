package com.example.savitar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExampleAdapter extends
        RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> implements Filterable{
    private List<Visitor> exampleList;
    private List<Visitor> exampleListFull;
    private boolean isGuard;
    private OnItemClickListener listener;

    @Override
    public Filter getFilter() {
        return examplefilter;
    }

    class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewVisitorName, textViewLicencePlates, textViewHostName, testViewHostAddress, textViewHostPhoneNumber, textViewHostPhoneTag, textViewHostNameTag;
        ExampleViewHolder(View itemView) {
            super(itemView);
            textViewVisitorName = itemView.findViewById(R.id.list_visitor_name);
            textViewLicencePlates = itemView.findViewById(R.id.list_licence_plates);
            testViewHostAddress = itemView.findViewById(R.id.list_host_address);
            textViewHostPhoneNumber = itemView.findViewById(R.id.list_host_phone_number);
            textViewHostName = itemView.findViewById(R.id.list_host_name);

            textViewHostPhoneTag = itemView.findViewById(R.id.host_phone_number);
            textViewHostNameTag = itemView.findViewById(R.id.host_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(position);
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
    ExampleAdapter(List<Visitor> exampleList, boolean isGuard) {
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
        this.isGuard = isGuard;
    }
    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.visitor_item, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        Visitor model = exampleList.get(position);
        holder.setHolderColor(model);
        holder.textViewVisitorName.setText(model.getName());
        holder.textViewLicencePlates.setText(model.getLicensePlates());
        holder.testViewHostAddress.setText(model.getHostAddress());
        if (isGuard){
            holder.textViewHostPhoneNumber.setText(model.getHostPhoneNo());
            holder.textViewHostName.setText(model.getHostName());
        }else{
            holder.textViewHostPhoneTag.setVisibility(View.GONE);
            holder.textViewHostNameTag.setVisibility(View.GONE);
            holder.textViewHostPhoneNumber.setVisibility(View.GONE);
            holder.textViewHostName.setVisibility(View.GONE);
        }

    }
    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    private Filter examplefilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Visitor> filterList=new ArrayList<>();
            if(constraint==null|| constraint.length()==0){
                filterList.addAll(exampleListFull);
            }
            else{
                String pattern=constraint.toString().toLowerCase().trim();
                for(Visitor item :exampleListFull){
                    if(isGuard){
                        if(item.getName().toLowerCase().contains(pattern) || item.getLicensePlates().toLowerCase().contains(pattern)|| item.getHostAddress().contains(pattern)){
                            filterList.add(item);
                        }
                    }else{
                        if(item.getName().toLowerCase().contains(pattern)){
                            filterList.add(item);
                        }
                    }

                }
            }
            FilterResults filterResults=new FilterResults();
            filterResults.values=filterList;
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}