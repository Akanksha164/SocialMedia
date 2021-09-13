package com.example.instagram.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.instagram.EditProfileActivity;
import com.example.instagram.LoginActivity;
import com.example.instagram.MainActivityModel;
import com.example.instagram.R;
import com.example.instagram.ShowPost;
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

public class ProfileFragment extends Fragment {

    private TextView bio,name,followingCount,postCount,followersCount;
    private CircleImageView profileimg;
    private Button edit_profile,logout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private  SharedPreferences sharedPreferences;
    private FirebaseStorage storage;
    private GridLayout parentLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        profileimg = (CircleImageView) root.findViewById(R.id.profileimg);
        bio = (TextView)root.findViewById(R.id.bio);
        name = (TextView)root.findViewById(R.id.name);
        edit_profile = (Button)root.findViewById(R.id.edit_profile);
        logout = (Button)root.findViewById(R.id.logout);
        parentLayout = (GridLayout) root.findViewById(R.id.parentLayout);
        followingCount = (TextView)root.findViewById(R.id.followingCount);
        postCount = (TextView)root.findViewById(R.id.postCount);
        followersCount = (TextView)root.findViewById(R.id.followersCount);
        profileimg.setImageResource(R.drawable.empty);

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Email","");
                editor.putString("Password","");
                editor.commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        DocumentReference dbUser = db.collection("Users").document(mAuth.getCurrentUser().getUid());
        StorageReference storageRef = storage.getReference();
        dbUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot queryDocumentSnapshot) {
                DocumentSnapshot value = queryDocumentSnapshot;

                Object following= value.get("Following")==null ? "0" : value.get("Following");
                List<String> clist = new ArrayList<String>();
                clist = convertObjectToList(following);
                int fcount = clist.size();
                followingCount.setText(""+fcount);

                Object followers= value.get("Followers")==null ? "0" : value.get("Followers");
                clist = convertObjectToList(followers);
                int focount = clist.size();
                followersCount.setText(""+focount);

                String Bio= value.get("bio")==null ? "": value.get("bio").toString();
                String fname = value.get("userName")==null ? "": value.get("userName").toString();

                MainActivityModel object = new MainActivityModel(Bio, fname);
                name.setText(object.getuserName());
                bio.setText(object.getbio());

                storageRef.child(value.getString("profilePicture")).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(profileimg);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "Image not seen.", Toast.LENGTH_SHORT).show();
                    }
                });

                database();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void database() {
        DocumentReference dbUser = db.collection("Users").document(mAuth.getCurrentUser().getUid());
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

                                        ImageView iv = new ImageView(getContext());
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
                                                Intent intent = new Intent(getContext(), ShowPost.class);
                                                intent.putExtra("Selected_Post_Id", document.getId());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Toast.makeText(getActivity(), "Image not seen", Toast.LENGTH_LONG).show();
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