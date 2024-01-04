package com.example.vetclinicapp.interfaces.second_layer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetclinicapp.R;
import com.example.vetclinicapp.interfaces.third_layer.QR_Shower;
import com.example.vetclinicapp.models.pets_and_users_details_model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Pet_Adder extends AppCompatActivity {

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    //CircleImageView
    CircleImageView set_profile_image;
    //EditText
    EditText setName, setBirthday, setAge, setBarangay, setRegion, setSpec, setNote;

    //AutoCompleteTextView
    AutoCompleteTextView setCity, setBreed;
    //TextView
    TextView ownerName, ownerEmail, ownerPhone;
    //Button
    Button upload;
    //Calendar instantiation
    Calendar cal;
    //DatePickerDialog
    DatePickerDialog datePicker;
    //Strings
    String selectedDate, ageDate, imageLink, completeAddress;

    //Firebase Database Reference
    DatabaseReference putData;
    DatabaseReference getData;
    //FirebaseUser Reference
    FirebaseUser user;
    //Firebase Storage
    StorageReference imageStorage;
    //ProgressDialog
    ProgressDialog pd;
    //LocalDate
    LocalDate dob;
    // Uri indicates, where the image will be picked from
    private Uri filePath;

    //Places API
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_adder);

        Places.initialize(this, "AIzaSyCkF5-bPHh1DsHkY2Ho8sLzpyXrO7j6-is");

        placesClient = Places.createClient(this);
        //CIRCLEIMAGEVIEW
        set_profile_image = findViewById(R.id.set_pet_image);

        //EDIT TEXTS
        setName = findViewById(R.id.set_pet_name);
        setBirthday = findViewById(R.id.set_pet_birth);
        setAge = findViewById(R.id.set_pet_age);
        setBreed = findViewById(R.id.set_breed);
        setBarangay = findViewById(R.id.barangayAdd);
        setCity = findViewById(R.id.city);
        setRegion = findViewById(R.id.region);
        setSpec = findViewById(R.id.specAddress);
        setNote = findViewById(R.id.notes);


        //TEXTVIEWS
        ownerName = findViewById(R.id.owner_name_display);
        ownerEmail = findViewById(R.id.owner_mail_display);
        ownerPhone = findViewById(R.id.owner_phone_display);

        //BUTTON
        upload = findViewById(R.id.upload_info);

        //Calendar assignment
        cal = Calendar.getInstance();

        //assigning year, month, days to different variables
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        //Database Callback
        putData = FirebaseDatabase.getInstance().getReference("Owners and Pets");
        getData = FirebaseDatabase.getInstance().getReference("Owners");

        //FirebaseUser
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Firebase Storage
        imageStorage = FirebaseStorage.getInstance().getReference();


        //BELOW IS WHERE IS THE MAGIC HAPPENS


        //This is for the autocomplete section
        // Set up the TextWatcher on setCity EditText
        setCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used, but required method
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Respond to text changes and perform autocomplete request
                performAutocompleteRequest(charSequence.toString(), setCity);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used, but required method

            }
        });

        //This is for the autocomplete of the pet breeds
        String[] mgaBreeds = getResources().getStringArray(R.array.hayop_breeds);
        ArrayAdapter<String> breedAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, mgaBreeds);
        setBreed.setAdapter(breedAdapter);

        setBreed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pinili = String.valueOf(parent.getItemAtPosition(position));
                setBreed.setText(pinili);
            }
        });
        //using the calendar picker when the set Birthday is clicked
        setBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //using the datePicker
                datePicker = new DatePickerDialog(Pet_Adder.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        //assigning the values of year, months, and days to each in the parameters above
                        cal.set(Calendar.YEAR, i);
                        cal.set(Calendar.MONTH, i1);
                        cal.set(Calendar.DAY_OF_MONTH, i2);
                        //using a simpleDateFormatter to format the date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault());
                        SimpleDateFormat ageFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        selectedDate = dateFormat.format(cal.getTime());
                        ageDate = ageFormat.format(cal.getTime());
                        dob = LocalDate.parse(ageDate);
                        ageCalculator(dob);
                        //set the values to the textView
                        setBirthday.setText(selectedDate);
                    }
                    //pass the variables to one of the parameters of DatePickerDialog
                }, year, month, day);
                datePicker.show();
            }
        });

        //taking the data from the owner's database and putting those into the textviews
        getData.child(Objects.requireNonNull(user.getDisplayName())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //gagawa tayo ng if, para i check if nag-exist. Nang sa ganon, di mag crash ang app, if wala syang mahanap
                if (snapshot.exists()) {
                    pets_and_users_details_model detailsModel = snapshot.getValue(pets_and_users_details_model.class);
                    ownerName.setText(Objects.requireNonNull(detailsModel).getOwner());
                    ownerEmail.setText(Objects.requireNonNull(detailsModel).getOwnerEmail());
                    ownerPhone.setText(Objects.requireNonNull(detailsModel).getOwnerContact());
                } else {
                    AlertDialog.Builder firstTime = new AlertDialog.Builder(Pet_Adder.this);
                    firstTime.setCancelable(true);
                    firstTime.setTitle("Your information has not yet been recorded");
                    firstTime.setMessage("As a first time user, you must upload your phone number in our system. To do this, go to the home page, click on your profile icon on the top-right corner of the screen");
                    firstTime.show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Pet_Adder.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //pag pinindot ni user ang upload
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if condition para i-check kung lahat ng info ay meron
                if (setName.getText().toString().isEmpty() ||
                        setBreed.getText().toString().isEmpty() || selectedDate.isEmpty() || ageDate.isEmpty() || setNote.getText().toString().isEmpty() ||
                        imageLink == null) {
                    AlertDialog.Builder gawa = new AlertDialog.Builder(Pet_Adder.this);
                    gawa.setTitle("Some fields are empty");
                    gawa.setMessage("All pet information are required. Make sure you have uploaded a profile picture by clicking on the icon above, or provided data in the fields required");
                    gawa.setIcon(R.drawable.error);
                    gawa.setCancelable(true);
                    gawa.show();
                } else {
                    //ProgressDialog to show the loading eme
                    pd = new ProgressDialog(Pet_Adder.this);
                    pd.setTitle("Uploading information");
                    pd.setMessage("Uploading information to the database. Please wait...");
                    pd.show();

                    //for the complete address
                    completeAddress = String.format("%s, %s, %s, %s", setSpec.getText().toString(), setBarangay.getText().toString(), setCity.getText().toString(), setRegion.getText().toString());

                    //we are using a hashmap as our data container
                    HashMap<String, Object> mapa = new HashMap<>();
                    mapa.put("petName", setName.getText().toString());
                    mapa.put("petAddress", completeAddress);
                    mapa.put("petBirth", setBirthday.getText().toString());
                    mapa.put("petAge", setAge.getText().toString());
                    mapa.put("city", setCity.getText().toString());
                    mapa.put("region", setRegion.getText().toString());
                    mapa.put("petImage", imageLink);
                    mapa.put("ownerContact", ownerPhone.getText().toString());
                    mapa.put("owner", ownerName.getText().toString());
                    mapa.put("ownerEmail", ownerEmail.getText().toString());
                    mapa.put("breed", setBreed.getText().toString());
                    mapa.put("notes", setNote.getText().toString());

                    //passing the data to our realtime database, and pushing a condition that will check if it is passed successfully
                    putData.child(Objects.requireNonNull(user.getDisplayName())).child(setName.getText().toString()).updateChildren(mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                AlertDialog.Builder build = new AlertDialog.Builder(Pet_Adder.this);
                                build.setTitle("Information Saved");
                                build.setMessage("Pet information has been saved");
                                build.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent goQR = new Intent(Pet_Adder.this, QR_Shower.class);
                                        goQR.putExtra("pathOfPet", Objects.requireNonNull(user.getDisplayName()) + "/" + setName.getText().toString());
                                        startActivity(goQR);
                                        Pet_Adder.this.finish();
                                    }
                                });
                                build.show();
                            }
                        }
                    });
                }
            }
        });

        //pag pinindot ni user ang image sa taas
        set_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

    }
    /*END OF MAIN BLOCK*/


    //METHODS ARE PLACED BELOW
    //this is an age calculator
    private void ageCalculator(LocalDate localDate) {
        LocalDate currentDate = LocalDate.now();
        if (localDate != null || currentDate != null) {
            setAge.setText(String.format(Locale.getDefault(), "%d years old", Period.between(localDate, currentDate).getYears()));
        } else {
            setAge.setText("0");
        }
    }

    //This is the method that opens an intent to allow the user to select an image from the gallery
    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);

    }

    // Override onActivityResult method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST || resultCode == RESULT_OK || data != null || data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                Pet_Adder.this.getContentResolver(),
                                filePath);
                set_profile_image.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                Toast.makeText(this, "An error occurred due to:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //THIS IS THE METHOD THAT WILL UPLOAD THE IMAGE CHOSEN ABOVE TO THE FIREBASE STORAGE
    private void uploadImage() {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(Pet_Adder.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            user = FirebaseAuth.getInstance().getCurrentUser();
            // Defining the child of storageReference
            StorageReference ref = imageStorage.child("Owners/" + Objects.requireNonNull(user).getDisplayName() + "/Pet Profile Images/" + setName.getText() + "/" + LocalDate.now() + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnCompleteListener(task -> imageLink = task.getResult().toString());
                // Image uploaded successfully
                // Dismiss dialog
                progressDialog.dismiss();
                Toast.makeText(Pet_Adder.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast
                        .makeText(Pet_Adder.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            });
        }
    }

    // Modify the performAutocompleteRequest method to update the hint based on suggestions
    private void performAutocompleteRequest(String query, EditText editText) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountries("PH")
                .setTypeFilter(TypeFilter.CITIES)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                    if (!predictions.isEmpty()) {
                        List<String> predictionStrings = new ArrayList<>();
                        for (AutocompletePrediction prediction : predictions) {
                            String[] selectedString = prediction.getFullText(null).toString().split(", ");
                            predictionStrings.add(selectedString[0]);
                        }
                        // Create and set the adapter for AutoCompleteTextView
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, predictionStrings);
                        setCity.setAdapter(adapter);
                        // Show all predictions in the dropdown
                        setCity.showDropDown();
                    }
                })
                .addOnFailureListener(exception -> {
                    // Handle error
                    Toast.makeText(this, "Autocomplete request failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}