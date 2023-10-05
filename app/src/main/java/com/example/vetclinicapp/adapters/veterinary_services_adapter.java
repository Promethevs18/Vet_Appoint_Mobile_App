package com.example.vetclinicapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetclinicapp.R;
import com.example.vetclinicapp.models.veterinary_service_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class veterinary_services_adapter extends FirebaseRecyclerAdapter<veterinary_service_model, veterinary_services_adapter.myViewHolder> {

    //eto ang constructor na tatawagin sa main class
    public veterinary_services_adapter(@NonNull FirebaseRecyclerOptions<veterinary_service_model> options) {
        super(options);
    }

    //eto naman ang responsible for setting the data sa layout file
    @Override
    protected void onBindViewHolder(@NonNull veterinary_services_adapter.myViewHolder holder, int position, @NonNull veterinary_service_model model) {
        holder.setData(model);
    }

    //eto ang maglilink ng layout from the res into our ui
    @NonNull
    @Override
    public veterinary_services_adapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_services_layout, parent, false));
    }

    //dito nangyayari ang pag-instantiate ng ui elements
    public static class myViewHolder extends RecyclerView.ViewHolder {

        TextView service_name, short_desc, price;
        ImageView service_image;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            service_name = itemView.findViewById(R.id.veterinary_name);
            short_desc = itemView.findViewById(R.id.short_desc);
            price = itemView.findViewById(R.id.price);
            service_image = itemView.findViewById(R.id.veterinary_image);
        }

        void setData(veterinary_service_model modelo) {
            Picasso.get().load(modelo.getVeterinary_image()).into(service_image);
            service_name.setText(modelo.getVeterinary_name());
            short_desc.setText(modelo.getShort_desc());
            price.setText(String.format("₱%s", modelo.getPrice()));
        }
    }
}
