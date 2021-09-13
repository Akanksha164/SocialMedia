package com.example.instagram.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.instagram.ProfileActivity2;
import com.example.instagram.R;
import com.example.instagram.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    private LinearLayout parentLayout;

    @Override
    public void onStart() {
        super.onStart();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference storageRef = storage.getReference();
        parentLayout= root.findViewById(R.id.parentLayout);

        DocumentReference dbUser = db.collection("Users").document(mAuth.getCurrentUser().getUid());
        dbUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ddocumentSnapshot) {

                DocumentSnapshot value = ddocumentSnapshot;
                Object following= value.get("Following")==null ? new ArrayList<String>() : value.get("Following");
                for(String s: convertObjectToList(following))
                {
                    Log.d("ABCD",s);
                    db.collection("Users").document(s).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            storageRef.child(documentSnapshot.getString("profilePicture")).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri urii) {

                                            db.collection("Users").document(documentSnapshot.getId()).collection("Post")
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String Caption= document.get("caption")==null ? "": document.get("caption").toString();

                                                            Object likes= document.get("likes")==null ? new ArrayList<String>() : document.get("likes");
                                                            List<String> list = new ArrayList<String>();

                                                            for(String s: convertObjectToList(likes)) {
                                                                Log.d("post list", s);
                                                                list.add(new String(s));
                                                            }

                                                            storageRef.child(document.getString("imageUrl")).getDownloadUrl()
                                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri uri) {
                                                                            populateLayout(parentLayout,urii,uri,documentSnapshot.getString("userName"),documentSnapshot.getId(),Caption,document.getId(),list);
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception exception) {
                                                                    // Handle any errors
                                                                    Toast.makeText(getActivity(), "Image not seen", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                    else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
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
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.d("Abcdd",e.getMessage());
                            Toast.makeText(getActivity(), "data not fetched", Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("Abcdd",e.getMessage());
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


    private void populateLayout(LinearLayout parentlinearLayout ,Uri profileimageurl,Uri postimage,String fname,String userId,String caption,String postId,List<String> list)
    {
        LinearLayout mainLinearLayout = new LinearLayout(getContext());
        mainLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
        parentlinearLayout.addView(mainLinearLayout);

        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(mainLinearLayout.getLayoutParams());
        marginParams.setMargins(10,18 , 10, 2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(marginParams);
        mainLinearLayout.setLayoutParams(layoutParams);

        LinearLayout scndLinearLayout = new LinearLayout(getContext());
        scndLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        scndLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLinearLayout.addView(scndLinearLayout);

        CircleImageView iv = new CircleImageView(getContext());
        iv.setLayoutParams(new LinearLayout.LayoutParams(80,80));
        scndLinearLayout.addView(iv);
        Picasso.get().load(profileimageurl).into(iv);

        TextView name = new TextView(getContext());
        name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        scndLinearLayout.addView(name);
        name.setText(fname);
        name.setTextColor(Color.BLACK);
        name.setLayoutParams(layoutParams);
        name.setTextSize(16);
        name.setTypeface(null, Typeface.BOLD);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity2.class);
                intent.putExtra("Selected_User_Id", userId);
                startActivity(intent);
            }
        });

        LinearLayout postLayout = new LinearLayout(getContext());
        postLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 800));
        postLayout.setOrientation(LinearLayout.VERTICAL);
        mainLinearLayout.addView(postLayout);

        ImageView post = new ImageView(getContext());
        post.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,800));
        postLayout.addView(post);
        Picasso.get().load(postimage).into(post);
        post.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ViewGroup.MarginLayoutParams marginImage = new ViewGroup.MarginLayoutParams(mainLinearLayout.getLayoutParams());
        marginImage.setMargins(2,8, 2, 5);
        LinearLayout.LayoutParams layoutImage = new LinearLayout.LayoutParams(marginImage);
        postLayout.setLayoutParams(layoutImage);

        LinearLayout likeLinearLayout = new LinearLayout(getContext());
        likeLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        likeLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLinearLayout.addView(likeLinearLayout);
        likeLinearLayout.setLayoutParams(layoutParams);

        ImageView heart = new ImageView(getContext());
        heart.setLayoutParams(new LinearLayout.LayoutParams(60,60));
        likeLinearLayout.addView(heart);
        heart.setImageResource(R.drawable.heart);

        if(list.contains(mAuth.getCurrentUser().getUid())){
            heart.setImageResource(R.drawable.filled_heart);
        }

        TextView postLike = new TextView(getContext());
        postLike.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        likeLinearLayout.addView(postLike);
        postLike.setTextColor(Color.BLACK);
        postLike.setGravity(Gravity.CENTER_VERTICAL);
        postLike.setLayoutParams(layoutParams);
        postLike.setTextSize(16);
        postLike.setLayoutParams(layoutParams);
        postLike.setTypeface(null, Typeface.BOLD);
        postLike.setText(" " + list.size() + " likes");

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference dbUser = db.collection("Users").document(userId);
                DocumentReference post = dbUser.collection("Post").document(postId);
                post.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        DocumentSnapshot value = documentSnapshot;
                        Object likes= value.get("likes")==null ? new ArrayList<String>() : value.get("likes");
                        List<String> tlist = new ArrayList<String>();
                        for (String s : convertObjectToList(likes)) {
                            //Log.d("ABCD",s);
                            tlist.add(new String(s));
                        }

                        List<String> copy=new ArrayList<String>(tlist);

                        if(!tlist.contains(mAuth.getCurrentUser().getUid()))
                        {
                            tlist.add(mAuth.getCurrentUser().getUid());
                            heart.setImageResource(R.drawable.filled_heart);
                        }
                        else
                        {
                            for(String ss : copy) {
                                if (ss.equals(mAuth.getCurrentUser().getUid())) {
                                    tlist.remove(ss);
                                    heart.setImageResource(R.drawable.heart);
                                }
                            }
                        }
                        post.update("likes", tlist).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                postLike.setText(" " + tlist.size() + " likes");
                              //  Toast.makeText(getActivity(), "successfully to like", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        LinearLayout commentLinearLayout = new LinearLayout(getContext());
        commentLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        commentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLinearLayout.addView(commentLinearLayout);
        commentLinearLayout.setLayoutParams(layoutParams);

        TextView namee = new TextView(getContext());
        namee.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        commentLinearLayout.addView(namee);
        namee.setText(fname+"  ");
        namee.setTextColor(Color.BLACK);
        namee.setTextSize(16);
        namee.setTypeface(null, Typeface.BOLD);

        TextView Caption = new TextView(getContext());
        Caption.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        commentLinearLayout.addView(Caption);
        Caption.setText(caption);
        Caption.setTextColor(Color.BLACK);
        Caption.setTextSize(16);

        View vi = new View(getContext());
        vi.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
        mainLinearLayout.addView(vi);
        vi.setBackgroundResource(R.color.status);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}