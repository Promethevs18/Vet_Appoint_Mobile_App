package com.example.vetclinicapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetclinicapp.R;
import com.example.vetclinicapp.interfaces.second_layer.Found_Pet_Details;
import com.example.vetclinicapp.models.pets_and_users_details_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class registered_pets_adapter extends FirebaseRecyclerAdapter<pets_and_users_details_model, registered_pets_adapter.myViewHolder> {

    //eto ang constructor na tatawagin sa main class
    public registered_pets_adapter(@NonNull FirebaseRecyclerOptions<pets_and_users_details_model> options) {
        super(options);
    }

    //eto naman ang responsible for setting the data sa layout file
    @Override
    protected void onBindViewHolder(@NonNull registered_pets_adapter.myViewHolder holder, int position, @NonNull pets_and_users_details_model model) {
        holder.setData(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pets_and_users_details_model get_pet = getSnapshots().get(holder.getAbsoluteAdapterPosition());
                String pet_path = get_pet.getOwnerName() + "/" + get_pet.getPetName();
                Intent goto_pet = new Intent(holder.itemView.getContext(), Found_Pet_Details.class);
                goto_pet.putExtra("Pet Name", pet_path);
                goto_pet.putExtra("Listed", true);
                holder.itemView.getContext().startActivity(goto_pet);
            }
        });
    }

    //eto ang maglilink ng layout from the res into our ui
    @NonNull
    @Override
    public registered_pets_adapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pets_layout, parent, false));
    }

    //dito nangyayari ang pag-instantiate ng ui elements
    public static class myViewHolder extends RecyclerView.ViewHolder {
        ImageView petImage;
        TextView pangalan, birthday, edad;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.pet_image);
            pangalan = itemView.findViewById(R.id.pet_pangalan);
            birthday = itemView.findViewById(R.id.pet_birthday);
            edad = itemView.findViewById(R.id.edad);
        }

        void setData(pets_and_users_details_model modelo) {
            Picasso.get().load(modelo.getPetImage()).into(petImage);
            pangalan.setText(modelo.getPetName());
            birthday.setText(modelo.getPetBirth());
            edad.setText(modelo.getPetAge());
        }
    }
}
