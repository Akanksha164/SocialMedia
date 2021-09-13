package com.example.instagram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity2 extends AppCompatActivity {

    private TextView bio,name,followingCount,followersCount,postCount,reachMe;
    private CircleImageView profileimg;
    private Button follow;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FragmentProfileBinding binding;
    private FirebaseStorage storage;
    private GridLayout parentLayout;
    private ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        String UserId = getIntent().getStringExtra("Selected_User_Id");

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        profileimg = findViewById(R.id.profileimg);
        bio = findViewById(R.id.bio);
        name = findViewById(R.id.name);
        follow = findViewById(R.id.follow);
        parentLayout = findViewById(R.id.gridLayout);
        followingCount = findViewById(R.id.followingCount);
        followersCount = findViewById(R.id.followrersCount);
        postCount = findViewById(R.id.postCount);
        reachMe = findViewById(R.id.reachMe);

        reachMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity2.this, bottomActivity.class);
                startActivity(intent);
            }
        });

        DocumentReference dbuser = db.collection("Users").document(mAuth.getCurrentUser().getUid());
        dbuser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DocumentSnapshot value = documentSnapshot;
                Object following= value.get("Following")==null ? new ArrayList<String>() : value.get("Following");
                for (String s: convertObjectToList(following))
                {
                    if(s.equals(UserId))
                    {
                        follow.setText("UnFollow");
                        break;
                    }
                }
            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference dbUser = db.collection("Users").document(mAuth.getCurrentUser().getUid());
                if (follow.getText()=="Follow")
                {
                    dbUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            DocumentSnapshot value = documentSnapshot;
                            List<String> list = new ArrayList<String>();
                            Object following= value.get("Following")==null ? new ArrayList<String>() : value.get("Following");
                            for (String s: convertObjectToList(following))
                            {
                                list.add(new String(s));
                            }
                            list.add(UserId);
                            dbUser.update("Following",list).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    follow.setText("UnFollow");
                                }
                            });

                            DocumentReference dbUserr = db.collection("Users").document(UserId);
                            dbUserr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    DocumentSnapshot value = documentSnapshot;
                                    List<String> list = new ArrayList<String>();
                                    Object followers= value.get("Followers")==null ? new ArrayList<String>() : value.get("Followers");
                                    for (String s: convertObjectToList(followers))
                                    {
                                        list.add(new String(s));
                                    }
                                    list.add(mAuth.getCurrentUser().getUid());
                                    dbUserr.update("Followers",list).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            followersCount.setText(""+list.size());
                                        }
                                    });
                                }
                            });

                        }
                    });
                }

                else {
                    dbUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            DocumentSnapshot value = documentSnapshot;
                            List<String> list = new ArrayList<String>();
                            Object following= value.get("Following")==null ? new ArrayList<String>() : value.get("Following");
                            for (String s: convertObjectToList(following))
                            {
                                list.add(new String(s));
                            }
                            list.remove(UserId);
                            dbUser.update("Following",list).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    follow.setText("Follow");
                                }
                            });

                            DocumentReference dbUserr = db.collection("Users").document(UserId);
                            dbUserr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot ddocumentSnapshot) {

                                    DocumentSnapshot value = ddocumentSnapshot;
                                    List<String> list = new ArrayList<String>();
                                    Object followers= value.get("Followers")==null ? new ArrayList<String>() : value.get("Followers");
                                    for (String s: convertObjectToList(followers))
                                    {
                                        list.add(new String(s));
                                    }
                                    list.remove(mAuth.getCurrentUser().getUid());
                                    dbUserr.update("Followers",list).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            followersCount.setText(""+list.size());
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            }
        });

        DocumentReference dbUser = db.collection("Users").document(UserId);
        StorageReference storageRef = storage.getReference();
        dbUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot queryDocumentSnapshot) {
                DocumentSnapshot value = queryDocumentSnapshot;

                String Bio= value.get("bio")==null ? "": value.get("bio").toString();
                String fname = value.get("userName")==null ? "": value.get("userName").toString();
                String ename = value.get("userEmail")==null ? "": value.get("userEmail").toString();
                String contactNo = value.get("userPhone")==null ? "": value.get("userPhone").toString();

                Object following= value.get("Following")==null ? "0" : value.get("Following");
                List<String> clist = new ArrayList<String>();
                clist = convertObjectToList(following);
                int fcount = clist.size();
                followingCount.setText(""+fcount);

                Object followers= value.get("Followers")==null ? "0" : value.get("Followers");
                clist = convertObjectToList(followers);
                int focount = clist.size();
                followersCount.setText(""+focount);

                MainActivityModel object = new MainActivityModel(Bio, fname);
                name.setText(object.getuserName());
                bio.setText(object.getbio());

                storageRef.child(value.getString("profilePicture")).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(profileimg);
                                profileimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ProfileActivity2.this, "Image not seen.", Toast.LENGTH_SHORT).show();
                    }
                });
                database(UserId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public static List<String> convertObjectToList(Object obj) {
        List<String> list = new ArrayList<String>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((String[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<String>((Collection<String>)obj);
        }
        return list;
    }

    private void database(String id) {
        DocumentReference dbUser = db.collection("Users").document(id);
        CollectionReference post = dbUser.collection("Post");
        StorageReference storageRef = storage.getReference();
        postCount.setText("0");
        post.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int count = 0;

                    for (QueryDocumentSnapshot document: task.getResult()) {

                        storageRef.child(document.getString("imageUrl")).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        ImageView iv = new ImageView(getBaseContext());
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(230,230);
                                        layoutParams.gravity= Gravity.TOP|Gravity.LEFT;
                                        iv.setLayoutParams(layoutParams);

                                        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(iv.getLayoutParams());
                                        marginParams.setMargins(2,2 , 2, 2);
                                        LinearLayout.LayoutParams layoutImage= new LinearLayout.LayoutParams(marginParams);
                                        iv.setLayoutParams(layoutImage);

                                        Picasso.get().load(uri).into(iv);
                                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        parentLayout.addView(iv);

                                        iv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(ProfileActivity2.this, UserPost.class);
                                                intent.putExtra("Selected_Post_Id", document.getId());
                                                intent.putExtra("Selected_User_Id", id);
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Toast.makeText(ProfileActivity2.this, "Image not seen", Toast.LENGTH_LONG).show();
                            }
                        });

                        count++;
                        postCount.setText(""+count);
                    }
                }
                else {
                    Log.d("String", "Error getting document");
                }
            }
        });
    }

}