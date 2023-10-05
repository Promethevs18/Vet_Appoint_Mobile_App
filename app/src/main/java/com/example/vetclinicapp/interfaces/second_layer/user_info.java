package com.example.vetclinicapp.interfaces.second_layer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetclinicapp.Intro_Interface;
import com.example.vetclinicapp.R;
import com.example.vetclinicapp.interfaces.third_layer.Add_feedback;
import com.example.vetclinicapp.models.pets_and_users_details_model;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_info extends AppCompatActivity {

    //UI ELEMENTS DECLARATION

    //textviews
    TextView name, email;

    //EditText
    EditText phone_number;

    //buttons
    Button update, save, out, feedback;
    //userProfile
    CircleImageView user_image;

    //Firebase user
    FirebaseUser user;

    //Firebase Database
    DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //UI ASSIGNMENT

        //textviews
        name = findViewById(R.id.username);
        email = findViewById(R.id.user_email);

        //EditTexts
        phone_number = findViewById(R.id.editTextPhone);

        //button
        update = findViewById(R.id.update_data);
        save = findViewById(R.id.save_data);
        out = findViewById(R.id.log_out);
        feedback = findViewById(R.id.give_feed);

        //CircleImageView
        user_image = findViewById(R.id.profile_picture);

        //Firebase Database
        userRef = FirebaseDatabase.getInstance().getReference("Owners");


        //BELOW IS WHERE THE MAGIC HAPPENS

        //calling the Firebase Auth, and assigning User to Auth to get user details
        user = FirebaseAuth.getInstance().getCurrentUser();

        //assigning values sa mga textviews
        name.setText(Objects.requireNonNull(user).getDisplayName());
        email.setText(user.getEmail());

        //assigning user image to picasso to load into circleimage
        Picasso.get().load(user.getPhotoUrl()).into(user_image);

        //this is to assign the user's phone number (if in case na meron na nalagay si user na phone number)
        userRef.child(Objects.requireNonNull(user.getDisplayName())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pets_and_users_details_model pet_deets = snapshot.getValue(pets_and_users_details_model.class);
                    phone_number.setText(Objects.requireNonNull(pet_deets).getOwnerContact());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(user_info.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //pag pinindot ni user ang update, magtatago ang update button, at magpapakita si save
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setVisibility(View.VISIBLE);
                phone_number.setEnabled(true);
                phone_number.setFocusable(true);
                update.setVisibility(View.GONE);
            }
        });

        //eto naman kapag magsasave ng details ni user sa Database natin
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone_number.setEnabled(false);
                phone_number.setFocusable(false);
                //below we are declaring a hashmap na magso-store ng information ni user
                HashMap<String, Object> user_mapa = new HashMap<>();
                user_mapa.put("ownerName", user.getDisplayName());
                user_mapa.put("ownerEmail", user.getEmail());
                user_mapa.put("ownerContact", phone_number.getText().toString());

                //we are using a builder para magpakita ng message stuff
                AlertDialog.Builder show_stats = new AlertDialog.Builder(user_info.this);
                //yung loading shit
                final ProgressDialog pd = new ProgressDialog(user_info.this);
                pd.setTitle("Uploading Information");
                pd.setMessage("Data is uploading. This may take a while...");
                pd.show();

                //we are now uploading the data to the database
                userRef.child(Objects.requireNonNull(user.getDisplayName())).updateChildren(user_mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    //kapag success ang upload
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            show_stats.setTitle("Upload success!");
                            show_stats.setMessage("User information has been uploaded to the database. Click anywhere to close this window");
                            show_stats.setCancelable(true);
                            show_stats.show();
                            update.setVisibility(View.VISIBLE);
                            save.setVisibility(View.GONE);
                        }
                    }
                    //kapag hindi naman
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        show_stats.setTitle("Upload Failed");
                        show_stats.setMessage(String.format("Task is incomplete due to %s ", e.getMessage()));
                        show_stats.setCancelable(true);
                        show_stats.show();
                    }
                });
            }
        });

        //eto naman for log out
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                    GoogleSignInClient signClient = GoogleSignIn.getClient(user_info.this, signInOptions);
                    signClient.signOut();
                    Intent gohome = new Intent(user_info.this, Intro_Interface.class);
                    startActivity(gohome);
                    Toast.makeText(user_info.this, "You have successfully logged out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //eto naman for leaving feedback
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goFeed = new Intent(user_info.this, Add_feedback.class);
                startActivity(goFeed);
            }
        });
    }
}