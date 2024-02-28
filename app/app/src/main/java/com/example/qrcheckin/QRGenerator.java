package com.example.qrcheckin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class QRGenerator extends AppCompatActivity {

    Button generateQRCodeButton;
    Button reuseQRCodeButton;
    ImageView QRCodeImage;
    String inputValue;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_generator);

        generateQRCodeButton = findViewById(R.id.generateCheckinQRCodeButton);
        reuseQRCodeButton = findViewById(R.id.reuseCheckinQRCodeButton);
        QRCodeImage = findViewById(R.id.checkinQRCodeImageView);


        // TODO - Generate QR Code
        // Generate new QR Code
        // Set to display
        generateQRCodeButton.setOnClickListener(v->{
            //String inputValue = documentID from bundle
            inputValue = "tester";
            QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 800);

            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap(0);
            // Setting Bitmap to ImageView (Assuming qrImage is your ImageView)
            QRCodeImage.setImageBitmap(bitmap);
        });

        // TODO - Reuse QR Code
        // Add once implemented by person assigned to QR Scanner
    }

}
