package com.example.instagram;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity  extends AppCompatActivity {

    private  SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        mAuth = FirebaseAuth.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                if(sharedPreferences.getString("Email","").equals("")) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(sharedPreferences.getString("Email",""), sharedPreferences.getString("Password",""))
                            .addOnCompleteListener( SplashActivity.this,new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        Intent intent = new Intent(SplashActivity.this,bottomActivity.class);
                                        startActivity(intent);

                                        finish();

                                    } else {
                                        // Log.w("SAKSHI", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SplashActivity.this, "User failure to Login", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        }, 800);
    }
}