package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private EditText Username;
    private EditText Phone;
    private Button button;
    private TextView tvlogin;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private AuthResult ar;
    private String userName, userEmail, userPhone;
    private FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void reload() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        //  startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Phone = (EditText) findViewById(R.id.Phone);
        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Password);
        button = (Button) findViewById(R.id.buttonsign);
        tvlogin = (TextView) findViewById(R.id.tvlogin);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        Username = (EditText) findViewById(R.id.Username);
        tvlogin = (TextView) findViewById(R.id.tvlogin);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ar = registerUser();
            }
        });

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private AuthResult registerUser(){

        //getting email and password from edit texts
        String email = Email.getText().toString().trim();
        String password  = Password.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Email.setError("Please enter Email");
            return null;
        }

        if(TextUtils.isEmpty(password)){
            Password.setError("Please enter password");
            return null;
        }

        if(password.length()<6){
            Password.setError("Password should be greater than 6");
            return null;
        }

        progressBar.setVisibility(View.VISIBLE);

        //creating a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if(task.isSuccessful()){

                            ar= task.getResult();
                            userName = Username.getText().toString();
                            userEmail = Email.getText().toString();
                            userPhone = Phone.getText().toString();

                            if (TextUtils.isEmpty(userName)) {
                                Username.setError("Please enter User Name");
                            } else if (TextUtils.isEmpty(userPhone)) {
                                Phone.setError("Please enter Phone");
                            } else {
                                // calling method to add data to Firebase Firestore.
                                database(userName, userEmail, userPhone,ar.getUser().getUid());
                            }

                            Toast.makeText(MainActivity.this,"Registration Successfull.",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            //  finish();
                        }
                        else{
                            //  Log.w("akanksha", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this,"User failed to Register",Toast.LENGTH_LONG).show();
                            ar=null;
                        }
                    }
                });
        return ar;
    }

    private void database(String userName, String userEmail, String userPhone, String Uid) {

        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbuser = db.collection("Users").document(Uid);

        // adding our data to our courses object class.
        MainActivityModel users = new MainActivityModel(userName, userEmail, userPhone,"Profile Picture/empty.png");

        // below method is use to add data to Firebase Firestore.
        dbuser.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Toast.makeText(MainActivity.this, "Your Course has been added to Firebase Firestore", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                // Toast.makeText(MainActivity.this, "Fail to add course \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
