package com.example.instagram.ui.search;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.instagram.R;
import com.example.instagram.SearchAdapter;
import com.example.instagram.databinding.FragmentSearchBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private ArrayList<SearchViewModel> list;
    private ListView search_list;
    private ArrayList<SearchViewModel> searchResult;
    private ArrayList<SearchViewModel> queryResult;
    private FragmentSearchBinding binding;
    private EditText search;
    private FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth mAuth;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        search=root.findViewById(R.id.Search);
        search_list = root.findViewById(R.id.search_list);
        searchResult = new ArrayList<SearchViewModel>();
        queryResult = new ArrayList<SearchViewModel>();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        StorageReference storageRef = storage.getReference();
        CollectionReference dbUser = db.collection("User");
        dbUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        storageRef.child(document.getString("profileImage")).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri profile = uri;
                                        String name = document.getString("fname");
                                        String uid = document.getId();
                                        /* if(uid==mAuth.getCurrentUser().getUid())
                                         {
                                             searchResult.add(new SearchViewModel(null, null, null));
                                         }
                                         else {
                                             searchResult.add(new SearchViewModel(name, profile, uid));
                                         }*/
                                        searchResult.add(new SearchViewModel(name, profile, uid));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Toast.makeText(getActivity(), "Not found", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //  Toast.makeText(getActivity(), "before text change: "+s, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Toast.makeText(getActivity(), "on text change:" +s, Toast.LENGTH_LONG).show();
                queryResult.clear();

                for(SearchViewModel temp : searchResult){

                    if(temp.getUserName().toLowerCase().contains(s.toString().toLowerCase())){
                        queryResult.add(temp);

                        if (s.toString().isEmpty()){
                            queryResult.clear();
                        }
                    }
                }

                SearchAdapter searchAdapter = new SearchAdapter(getContext(),queryResult);
                search_list.setAdapter(searchAdapter);
                // progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Toast.makeText(getActivity(), "after text change:" +s, Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }
    /*  @Override
      public void onResume(){
          super.onResume();
          progressBar.setVisibility(View.GONE);
      }*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
