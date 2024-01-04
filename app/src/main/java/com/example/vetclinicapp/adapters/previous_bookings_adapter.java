package com.example.vetclinicapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetclinicapp.R;
import com.example.vetclinicapp.interfaces.third_layer.Prev_Appointment;
import com.example.vetclinicapp.models.pets_and_users_details_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class previous_bookings_adapter extends FirebaseRecyclerAdapter<pets_and_users_details_model, previous_bookings_adapter.myViewHolder> {

    public previous_bookings_adapter(@NonNull FirebaseRecyclerOptions<pets_and_users_details_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull previous_bookings_adapter.myViewHolder holder, int position, @NonNull pets_and_users_details_model model) {
        holder.setData(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pets_and_users_details_model petsModels = getSnapshots().get(holder.getAbsoluteAdapterPosition());
                Intent goPrev = new Intent(holder.itemView.getContext(), Prev_Appointment.class);
                goPrev.putExtra("Date", petsModels.getSched_date());
                holder.itemView.getContext().startActivity(goPrev);
            }
        });
    }

    @NonNull
    @Override
    public previous_bookings_adapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_bookings_layout, parent, false));
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView service, patient, date;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            service = itemView.findViewById(R.id.service_done);
            patient = itemView.findViewById(R.id.patients_admitted);
            date = itemView.findViewById(R.id.appoint_date);
        }

        public void setData(pets_and_users_details_model model) {
            service.setText(model.getServices());
            patient.setText(model.getPatients());
            date.setText(model.getSched_date());
        }
    }
}
