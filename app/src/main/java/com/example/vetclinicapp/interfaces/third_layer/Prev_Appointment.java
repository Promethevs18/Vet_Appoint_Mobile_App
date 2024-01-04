package com.example.vetclinicapp.interfaces.third_layer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetclinicapp.R;
import com.example.vetclinicapp.models.pets_and_users_details_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Prev_Appointment extends AppCompatActivity {

    //elements declarations
    CircleImageView profileImage;
    TextView owner, patient, date, time, status, services;
    Intent getIntent;
    String takenCode;

    DatabaseReference getPrevAppointment, getNotes;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev_appointment);

        //Element instantiation
        profileImage = findViewById(R.id.profile_image);

        owner = findViewById(R.id.may_ari);
        patient = findViewById(R.id.patientName);
        date = findViewById(R.id.sched_date);
        time = findViewById(R.id.time);
        status = findViewById(R.id.status);
        services = findViewById(R.id.services);

        getIntent = getIntent();

        user = FirebaseAuth.getInstance().getCurrentUser();

        //BELOW IS WHERE THE MAGIC HAPPENS
        takenCode = getIntent.getStringExtra("Date");

        getPrevAppointment = FirebaseDatabase.getInstance().getReference("Owners").child(Objects.requireNonNull(user.getDisplayName())).child("Previous Bookings").child(takenCode);

        //to take the details from the Previous appointments node
        getPrevAppointment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pets_and_users_details_model modelo = snapshot.getValue(pets_and_users_details_model.class);
                Picasso.get().load(Objects.requireNonNull(modelo).getImageUrl()).into(profileImage);
                owner.setText(modelo.getOwner());
                patient.setText(String.format("Patient/s\n%s", modelo.getPatients()));
                date.setText(modelo.getSched_date());
                time.setText(modelo.getSched_time());
                status.setText(String.format("Appointment status is\n %s", modelo.getStatus()));
                services.setText(String.format("Service/s done\n %s", modelo.getServices()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Prev_Appointment.this, "Data can't be fetched as of the moment. Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}