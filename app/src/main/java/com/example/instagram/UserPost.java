package com.example.instagram;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPost extends AppCompatActivity {

    private TextView name,name1,caption;
    private CircleImageView profileimage;
    private ImageView Post;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference storageRef = storage.getReference();
        name =findViewById(R.id.username);
        name1 =findViewById(R.id.username1);
        profileimage =findViewById(R.id.profileimage);
        caption =findViewById(R.id.caption);
        Post =findViewById(R.id.post);

        String post_id = getIntent().getStringExtra("Selected_Post_Id");
        String user_id = getIntent().getStringExtra("Selected_User_Id");

        DocumentReference dbUser = db.collection("Users").document(user_id);
        DocumentReference post = dbUser.collection("Post").document(post_id);

        dbUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DocumentSnapshot values = documentSnapshot;
                String Name = values.get("userName") == null ? "" : values.get("userName").toString();
                name.setText(Name);
                name1.setText(Name);

                storageRef.child(values.getString("profilePicture")).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(profileimage);
                                // Toast.makeText(ProfileActivity2.this, "Dp changed", Toast.LENGTH_LONG).show();
                                // Got the download URL for 'users/me/profile.png'
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        //  Toast.makeText(ProfileActivity2.this, "Image not seen", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        post.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DocumentSnapshot value = documentSnapshot;
                String Caption = value.get("caption") == null ? "" : value.get("caption").toString();
                caption.setText(Caption);
                storageRef.child(value.getString("imageUrl")).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(Post);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }
        });
    }

}

