package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity{

    private TextView name, bio;
    DatabaseReference demoName,demoBio, rootName, rootBio;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        name = (TextView) findViewById(R.id.name);
        bio = (TextView) findViewById(R.id.bio);

        rootName = FirebaseDatabase.getInstance().getReference();
        rootBio = FirebaseDatabase.getInstance().getReference();

        demoName = rootName.child("Name");
        demoBio = rootBio.child("Bio");


    }

    public void logout(View View) {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void edit_profile(View View){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),EditProfileActivity.class));
    }

}

