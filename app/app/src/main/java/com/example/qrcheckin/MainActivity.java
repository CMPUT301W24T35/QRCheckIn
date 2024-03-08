package com.example.qrcheckin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

//import com.example.qrcheckin.databinding.ActivityMainBinding;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileOutputStream;

/**
* Main class to the project
*/

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private AppBarConfiguration appBarConfiguration;
    //private ActivityMainBinding binding;
    String android_id;
    String mainUserID;
    boolean doesExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        checkIsUserProfile();
    }

    private void checkIsUserProfile() {
        android_id = Settings.Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        db.collection("user").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){
                    Log.e("FirestoreError", "Error getting event details",error);
                    return;
                }
                Log.d("FirestoreSuccess", "Successfully fetched events.");
                doesExist = false;
                assert value != null;
                for(QueryDocumentSnapshot doc: value){
                    String deviceID = doc.getString("androidID");
                    if (deviceID == null){
                        // Do nothing
                    }
                    else if (deviceID.equals(android_id)){
                        mainUserID = doc.getId();
                        doesExist = true;
                    }
                }
                if (doesExist){
                    Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
                    //intent.putExtra("UserID", doc.getId()); // Attach the bundle to the intent
                    try {
                        FileOutputStream fos = openFileOutput("localStorage.txt", Context.MODE_PRIVATE);
                        fos.write(mainUserID.getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, CreateProfileActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}
