package com.example.instagram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private EditText Bio;
    private EditText Name;
    private TextView Email,Phone,ProfileChange;
    private Button Save, reset;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String bio, userName;
    private CircleImageView Profile;
    private ImageView back;
    private Uri uri;
    private FirebaseStorage storage;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Profile = (CircleImageView) findViewById(R.id.Profile);
        Name = (EditText) findViewById(R.id.Name);
        Bio = (EditText) findViewById(R.id.Bio);
        Email = (TextView) findViewById(R.id.Email);
        ProfileChange = (TextView) findViewById(R.id.ProfileChange);
        Phone = (TextView) findViewById(R.id.Phone);
        Save = (Button) findViewById(R.id.Save);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        back = findViewById(R.id.back);
        reset = findViewById(R.id.reset);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, bottomActivity.class);
                startActivity(intent);            }
        });

        ProfileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, ChangeProfilePic.class);
                startActivity(intent);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bio = Bio.getText().toString();
                userName = Name.getText().toString();
                database(bio, userName);
                progressBar.setVisibility(View.VISIBLE);
               // startActivity(new Intent(getApplicationContext(), bottomActivity.class));
                Toast.makeText(EditProfileActivity.this, "Profile Edited", Toast.LENGTH_LONG).show();
            }
        });

        DocumentReference dbUser = db.collection("Users").document(mAuth.getCurrentUser().getUid());

        dbUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot queryDocumentSnapshot) {

                DocumentSnapshot value = queryDocumentSnapshot;

                String bio= value.get("bio")==null ? "": value.get("bio").toString();
                String fname = value.get("userName")==null ? "": value.get("userName").toString();
                String ename = value.get("userEmail")==null ? "": value.get("userEmail").toString();
                String userPhone = value.get("userPhone")==null ? "": value.get("userPhone").toString();

                MainActivityModel object = new MainActivityModel(fname,ename,userPhone);
                object.setbio(bio);

                Name.setText(object.getuserName());
                Email.setText(object.getuserEmail());
                Phone.setText(object.getuserPhone());
                Bio.setText(object.getbio());

                imagefetch();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //   Toast.makeText(EditProfileActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void database(String bio,String userName) {

        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbuser = db.collection("Users").document(mAuth.getCurrentUser().getUid());
        // adding our data to our courses object class.
        MainActivityModel users = new MainActivityModel(bio,userName);

        // below method is use to add data to Firebase Firestore.
        String userNamee = users.getuserName();
        String bioo = users.getbio();

        dbuser.update("bio",bioo,"userName",userNamee).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Fail to add course \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void imagefetch() {
        DocumentReference dbUser = db.collection("Users").document(mAuth.getCurrentUser().getUid());
        StorageReference storageRef = storage.getReference();

        dbUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot queryDocumentSnapshot) {
                DocumentSnapshot value = queryDocumentSnapshot;
                storageRef.child(value.getString("profilePicture")).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(Profile);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(EditProfileActivity.this, "Image not seen.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}