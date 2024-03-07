package com.example.qrcheckin;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.qrcheckin.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.Menu;
import android.view.MenuItem;

import java.io.FileOutputStream;

/**
* Main class to the project
*/

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
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
                    Log.d("FIREBASE", "device ID"+deviceID);
                    Log.d("FIREBASE", "android ID "+android_id);
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
