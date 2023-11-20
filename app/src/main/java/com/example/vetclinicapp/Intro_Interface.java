package com.example.vetclinicapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.vetclinicapp.interfaces.Capture;
import com.example.vetclinicapp.interfaces.first_layer.Home;
import com.example.vetclinicapp.interfaces.second_layer.Found_Pet_Details;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Objects;

public class Intro_Interface extends AppCompatActivity {


    //PUT ALL UI DECLARATION HERE

    //Buttons
    Button found, googleSign;

    //Google Sign in tools
    GoogleSignInClient signInClient;
    GoogleSignInOptions signInOptions;

    //ProgressDialog
    ProgressDialog alert;
    ActivityResultLauncher<ScanOptions> scanLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            String buongData = result.getContents();
            int umpisa = buongData.indexOf("- ") + 2;
            if (umpisa > 1 && umpisa < buongData.length()) {
                String extracted = buongData.substring(umpisa);
                Intent pass_to_pet_found = new Intent(Intro_Interface.this, Found_Pet_Details.class);
                pass_to_pet_found.putExtra("Pet Name", extracted);
                startActivity(pass_to_pet_found);
            }

        }

    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //PUT INSTANTIATION OF UI HERE
        //google sign in button
        googleSign = findViewById(R.id.google_sign);

        //buttons
        found = findViewById(R.id.found);


        //CODE BELOW ARE THE FUNCTIONS

        //This is for the google account sign in option
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(getString(R.string.default_web_client_id)).build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);


        //FOR CLICKING THE SIGN IN BUTTON
        googleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sign_In();
            }
        });

        //FOR CLICKING THE FOUND BUTTON
        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions scanOptions = new ScanOptions();
                scanOptions.setPrompt("Press volume up to turn on light\nPress volume down to turn off light");
                scanOptions.setOrientationLocked(true);
                scanOptions.setCaptureActivity(Capture.class);
                scanOptions.setBeepEnabled(true);
                scanLauncher.launch(scanOptions);
            }
        });

    }

    //the method of SIGN_IN (For showing the intent for Account selection)
    private void Sign_In() {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, 1000);
    }

    //override method para makuha yung sinelect ni user, and then use that email to sign in to Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            //signs in the selected account
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //kunin ang naka sign in na account, tapos papalitan ng Firebase-given account
                GoogleSignInAccount google_account = task.getResult(ApiException.class);
                if (google_account != null) {
                    AuthCredential creds = GoogleAuthProvider.getCredential(google_account.getIdToken(), null);
                    firebaseWithGoogle(creds);
                }


            } catch (ApiException e) {
//                alert.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    //This block is responsible for signing the user's Google Account to our Firebase Auth section
    private void firebaseWithGoogle(AuthCredential creds) {
        //code for the progress emerut
        alert = new ProgressDialog(this);
        alert.setTitle("Authenticating");
        alert.setMessage("Credentials authenticating. This may take a while...");
        alert.show();

        //using an OnSuccess para madetermine if pumasok ang creds sa database
        FirebaseAuth.getInstance().signInWithCredential(creds).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Intent next = new Intent(Intro_Interface.this, Home.class);
                        next.putExtra("name", Objects.requireNonNull(user).getDisplayName());
                        next.putExtra("imageLink", Objects.requireNonNull(user.getPhotoUrl()).toString());
                        startActivity(next);
                        alert.dismiss();
                    }
                })
                //failureListener if in case na may error sa pag sign up
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alert.dismiss();
                        Toast.makeText(Intro_Interface.this, "We cannot proceed with the request due to: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ActivityCompat.requestPermissions(Intro_Interface.this, new String[]{
                android.Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR
        }, PackageManager.PERMISSION_GRANTED);


    }
}