package com.example.vetclinicapp.interfaces.third_layer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetclinicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Add_feedback extends AppCompatActivity {

    //UI and other elements declaration
    EditText subject, starCount, content;
    Button submit_feed;

    //Firebase Related
    DatabaseReference feedback;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);

        //Link UI elements to the code

        //EditTexts
        subject = findViewById(R.id.feed_subject);
        starCount = findViewById(R.id.star_amount);
        content = findViewById(R.id.feed_content);

        //Buttons
        submit_feed = findViewById(R.id.submit_feed);

        //Firebase calls
        feedback = FirebaseDatabase.getInstance().getReference("Owners");
        user = FirebaseAuth.getInstance().getCurrentUser();


        //Below is where the magic begins
        submit_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pd = new ProgressDialog(Add_feedback.this);
                pd.setTitle("Sending feedback");
                pd.setMessage("This may take a while...");
                pd.show();

                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("feed_sub", subject.getText().toString());
                mapa.put("stars", starCount.getText().toString());
                mapa.put("feed_con", content.getText().toString());

                feedback.child(Objects.requireNonNull(user.getDisplayName())).child("Feedback").updateChildren(mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(Add_feedback.this);
                            builder.setTitle("Feedback sent!");
                            builder.setMessage("Your feedback has been sent to our system. We appreciate you taking your time to provide a helpful information that will help us improve our services. \n\nYou may click anywhere to close this message");
                            builder.setCancelable(true);
                            builder.setIcon(R.drawable.success);
                            builder.show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(Add_feedback.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}