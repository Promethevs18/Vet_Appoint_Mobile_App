package com.example.vetclinicapp.interfaces.third_layer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vetclinicapp.R;
import com.example.vetclinicapp.interfaces.first_layer.Home;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QR_Shower extends AppCompatActivity {

    //INTENT
    Intent getCode;
    //String
    String passedCode;
    //IMAGEVIEW
    ImageView petQr;
    //BUTTON
    Button return_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_shower);

        //Elements assignments
        //Intent
        getCode = getIntent();
        //String
        passedCode = getCode.getStringExtra("pathOfPet");
        //ImageView
        petQr = findViewById(R.id.petQR);
        //Button
        return_main = findViewById(R.id.return_to_main);


        //BELOW IS WHERE THE MAGIC HAPPENS

        //Our QR generator executes in the code below
        if (passedCode != null) {
            MultiFormatWriter sulat = new MultiFormatWriter();
            try {
                //Initializing bit matrix
                BitMatrix matrix = sulat.encode("Install AniCare App to view the record of - " + passedCode, BarcodeFormat.QR_CODE, 450, 450);
                //enabling barcode encoder
                BarcodeEncoder encoder = new BarcodeEncoder();
                //Initializing bitmap
                Bitmap bitmap = encoder.createBitmap(matrix);
                //set the bitmap as our image source
                petQr.setImageBitmap(bitmap);

            } catch (WriterException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        //if the user clicks on the return to menu
        return_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoMenu = new Intent(QR_Shower.this, Home.class);
                startActivity(gotoMenu);
                QR_Shower.this.finish();
            }
        });
    }
}