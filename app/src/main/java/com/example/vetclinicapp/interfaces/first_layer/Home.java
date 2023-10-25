package com.example.vetclinicapp.interfaces.first_layer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetclinicapp.R;
import com.example.vetclinicapp.adapters.previous_bookings_adapter;
import com.example.vetclinicapp.adapters.registered_pets_adapter;
import com.example.vetclinicapp.adapters.veterinary_services_adapter;
import com.example.vetclinicapp.interfaces.second_layer.Pet_Adder;
import com.example.vetclinicapp.interfaces.second_layer.user_info;
import com.example.vetclinicapp.interfaces.third_layer.Appoint_Window;
import com.example.vetclinicapp.models.pets_and_users_details_model;
import com.example.vetclinicapp.models.veterinary_service_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class Home extends AppCompatActivity {

    //TEXTVIEW
    TextView userName;

    //CIRCLEImageView
    CircleImageView profile_pic;

    //RecyclerView
    RecyclerView services, pets, previous;

    //FIREBASE RECYCLERS
    FirebaseRecyclerOptions<veterinary_service_model> vet_queue;
    FirebaseRecyclerOptions<pets_and_users_details_model> pet_queue;
    FirebaseRecyclerOptions<pets_and_users_details_model> prev_queue;

    //FIREBASE DATABASE
    DatabaseReference databaseReference, petReference, ownerReference, bookings;

    //FIREBASE USER
    FirebaseUser currentUser;

    //IMAGE BUTTONS
    ImageButton vet, groom, other;

    //BUTTONS
    Button addPet, book, cancel;

    //String
    String booking_date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //UI instantiation
        //textview
        userName = findViewById(R.id.user_name);

        //circle image view
        profile_pic = findViewById(R.id.prof_pic);


        //RecyclerView
        services = findViewById(R.id.services_view);
        pets = findViewById(R.id.pet_list);
        previous = findViewById(R.id.prev_recycler);

        //DATABASE REFERENCE ASSIGNMENT
        databaseReference = FirebaseDatabase.getInstance().getReference("Services");
        petReference = FirebaseDatabase.getInstance().getReference("Owners and Pets");
        ownerReference = FirebaseDatabase.getInstance().getReference("Owners");
        bookings = FirebaseDatabase.getInstance().getReference("Bookings");

        //USER REFERENCE ASSIGNMENT
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Image Buttons
        vet = findViewById(R.id.vet_button);
        groom = findViewById(R.id.groom_button);
        other = findViewById(R.id.other_button);

        //Buttons
        addPet = findViewById(R.id.add_a_pet);
        book = findViewById(R.id.add_appoint);
        cancel = findViewById(R.id.cancel);


        //BELOW ARE THE MAIN FUNCTIONS AND STUFF


        //replacing the name with the user's actual name
        userName.setText(currentUser.getDisplayName());
        //replacing the pic with the user's profile pic
        Picasso.get().load(currentUser.getPhotoUrl()).into(profile_pic);

        //eto ang nangyayari kapag ang pinili ay si Veterinary
        vet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queueList("Veterinary");
            }
        });

        //eto naman for the Grooming
        groom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queueList("Grooming");

            }
        });

        //eto naman for the other
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queueList("Others");
            }
        });

        //eto naman for user profile
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(Home.this, user_info.class);
                startActivity(profile);
            }
        });

        //eto naman for adding a pet
        addPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_a_pet = new Intent(Home.this, Pet_Adder.class);
                startActivity(add_a_pet);
            }
        });

        //eto naman for adding a new appointment
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_appointment = new Intent(Home.this, Appoint_Window.class);
                startActivity(go_appointment);
                Home.this.finish();
            }
        });

        //eto naman mag-check kapag may appointment, mawawala si set appointment at lalabas si cancel
        ownerReference.child(Objects.requireNonNull(currentUser.getDisplayName())).child("Booking").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pets_and_users_details_model getDate = snapshot.getValue(pets_and_users_details_model.class);
                    booking_date = Objects.requireNonNull(getDate).getSched_date();
                    book.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                } else {
                    book.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Data cannot be loaded. Try agan later", Toast.LENGTH_SHORT).show();
            }
        });

        //kapag pinindot ni user ang cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder cancelWarning = new AlertDialog.Builder(Home.this);
                cancelWarning.setTitle("Warning!");
                cancelWarning.setMessage("Are you sure you want to cancel your appointment? You may have to rebook if you chose to proceed");
                cancelWarning.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProgressDialog pd = new ProgressDialog(Home.this);
                        pd.setTitle("Cancelling Appointment");
                        pd.setMessage("Please wait...");
                        pd.show();
                        bookings.child(booking_date).child(currentUser.getDisplayName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ownerReference.child(currentUser.getDisplayName()).child("Booking").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            dialogInterface.dismiss();
                                            pd.dismiss();
                                            AlertDialog.Builder success = new AlertDialog.Builder(Home.this);
                                            success.setTitle("Cancellation Successful");
                                            success.setMessage("Booking cancellation was successful. Click anywhere to close this window");
                                            success.show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                cancelWarning.show();
            }
        });

    }
    /*MAIN BLOCK ENDS HERE*/

    //BELOW NAKALAGAY ANG MGA METHODS
    //method for populating the recyclerview based sa kung anong pipiliin ni user
    private void queueList(String path) {
        //Using a progressDialog para may loading kunwari
        ProgressDialog pd = new ProgressDialog(Home.this);
        pd.setTitle("Data Loading");
        pd.setMessage("Fetching data, this may take a while...");
        pd.show();

        //queueing ng services
        RecyclerView.LayoutManager layoutMan = new LinearLayoutManager(Home.this, RecyclerView.VERTICAL, false);
        services.setLayoutManager(layoutMan);
        vet_queue = new FirebaseRecyclerOptions.Builder<veterinary_service_model>().setQuery(databaseReference.child(path), veterinary_service_model.class)
                .build();
        veterinary_services_adapter vet_adapter = new veterinary_services_adapter(vet_queue);
        services.setAdapter(vet_adapter);
        vet_adapter.startListening();

        vet_adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                pd.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //RecyclerView para sa mga pets eme
        RecyclerView.LayoutManager petLayout = new LinearLayoutManager(Home.this, RecyclerView.VERTICAL, false);
        pets.setLayoutManager(petLayout);
        pet_queue = new FirebaseRecyclerOptions.Builder<pets_and_users_details_model>().setQuery(petReference.child(Objects.requireNonNull(currentUser.getDisplayName())), pets_and_users_details_model.class)
                .build();
        registered_pets_adapter pets_adapter = new registered_pets_adapter(pet_queue);
        pets.setAdapter(pets_adapter);
        pets_adapter.startListening();

        //RecyclerView para sa mga previous appointments
        RecyclerView.LayoutManager prevLayout = new LinearLayoutManager(Home.this, RecyclerView.VERTICAL, false);
        previous.setLayoutManager(prevLayout);
        prev_queue = new FirebaseRecyclerOptions.Builder<pets_and_users_details_model>().setQuery(ownerReference.child(currentUser.getDisplayName()).child("Previous Bookings"), pets_and_users_details_model.class)
                .build();
        previous_bookings_adapter prev_adapter = new previous_bookings_adapter(prev_queue);
        previous.setAdapter(prev_adapter);
        prev_adapter.startListening();

    }
}