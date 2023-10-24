package com.example.vetclinicapp.interfaces.second_layer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetclinicapp.R;
import com.example.vetclinicapp.interfaces.first_layer.Home;
import com.example.vetclinicapp.models.pets_and_users_details_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Found_Pet_Details extends AppCompatActivity {

    //Declarations of different variables
    //textviews
    TextView petName, petAge, petAddress, petBirth, ownerName, ownerEmail, ownerPhone;

    //profile image
    CircleImageView petImage;

    //Firebase Database Reference
    DatabaseReference finalData = FirebaseDatabase.getInstance().getReference("Owners and Pets");

    //String Array
    String[] fullPath;

    //ImageButtons
    ImageButton phone, email;

    //ImageView
    ImageView qr;

    //Button
    Button pet_delete;

    //Boolean
    Boolean listed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_pet_details);

        try {
            //UI AND VARIABLES ASSIGNMENT
            //textviews
            petName = findViewById(R.id.pet_name);
            petAge = findViewById(R.id.pet_age);
            petAddress = findViewById(R.id.pet_add);
            petBirth = findViewById(R.id.pet_birth);
            ownerName = findViewById(R.id.owner_name);
            ownerEmail = findViewById(R.id.owner_email);
            ownerPhone = findViewById(R.id.owner_phone);

            //circleimageview
            petImage = findViewById(R.id.pet_pic);

            //Intent
            Intent get = getIntent();

            //String
            fullPath = get.getStringExtra("Pet Name").split("/");

            //ImageButtons
            phone = findViewById(R.id.contact_phone);
            email = findViewById(R.id.contact_email);

            //ImageView
            qr = findViewById(R.id.pet_qr);

            //Button
            pet_delete = findViewById(R.id.pet_delete);

            //Boolean
            listed = get.getBooleanExtra("Listed", false);


            //BELOW IS WHERE THE MAGIC HAPPENS

            //using a progressDialog to show a loading eme (for estete)
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Loading data");
            pd.setMessage("Data is loading, please wait...");
            pd.show();

            //Use a for-loop to iterate through the array values
            for (String components : fullPath) {
                finalData = finalData.child(components);
            }
            ;

            //we are calling the pet's data using the name passed through Intent
            finalData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        pd.dismiss();
                    }
                    //calling the model class as a blueprint for getting data
                    pets_and_users_details_model petModel = snapshot.getValue(pets_and_users_details_model.class);
                    //assignment of data from database to the textviews
                    petName.setText(Objects.requireNonNull(petModel).getPetName());
                    petAge.setText(String.format("%s years old", Objects.requireNonNull(petModel.getPetAge())));
                    petAddress.setText(Objects.requireNonNull(petModel.getPetAddress()));
                    petBirth.setText(Objects.requireNonNull(petModel.getPetBirth()));
                    ownerEmail.setText(Objects.requireNonNull(petModel.getOwnerEmail()));
                    ownerName.setText(Objects.requireNonNull(petModel.getOwner()));
                    ownerPhone.setText(Objects.requireNonNull(petModel.getOwnerContact()));

                    //using Picasso to load petProfile
                    Picasso.get().load(petModel.getPetImage()).into(petImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Found_Pet_Details.this, "Pet details cannot be found.\nEither the pet hasn't been registered, or the code is invalid", Toast.LENGTH_SHORT).show();
                }
            });

            //when the user presses on the phone
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goPhone = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + ownerPhone.getText().toString()));
                    startActivity(goPhone);
                }
            });

            //when the user presses on the mail
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goEmail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + ownerEmail.getText().toString() + "?subject=" + "I found your pet using AniCare!"));
                    goEmail.setPackage("com.google.android.gm");
                    startActivity(goEmail);
                }
            });

            //eto ang qr encoder
            if (get.getStringExtra("Pet Name") != null) {
                MultiFormatWriter sulat = new MultiFormatWriter();
                try {
                    //Initializing bit matrix
                    BitMatrix matrix = sulat.encode("Install AniCare App to view the record of - " + get.getStringExtra("Pet Name"), BarcodeFormat.QR_CODE, 450, 450);
                    //enabling barcode encoder
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    //Initializing bitmap
                    Bitmap bitmap = encoder.createBitmap(matrix);
                    //set the bitmap as our image source
                    qr.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                qr.setVisibility(View.GONE);
            }

            //To show the button
            if (listed) {
                pet_delete.setVisibility(View.VISIBLE);
            }

            //Kapag pinindot ang delete button
            pet_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder warn = new AlertDialog.Builder(Found_Pet_Details.this);
                    warn.setTitle("Delete Pet");
                    warn.setMessage("Are you sure you want to delete this pet from your current record?");
                    warn.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ProgressDialog del_prog = new ProgressDialog(Found_Pet_Details.this);
                            del_prog.setTitle("Deleting Information");
                            del_prog.setMessage("This may take a while...");
                            del_prog.show();
                            finalData.removeValue();
                        }
                    });
                    warn.show();
                }
            });
        } catch (Exception ex) {
            Intent goBack = new Intent(Found_Pet_Details.this, Home.class);
            startActivity(goBack);
            Toast.makeText(getApplicationContext(), "Finished", Toast.LENGTH_SHORT).show();
        }

    }
}