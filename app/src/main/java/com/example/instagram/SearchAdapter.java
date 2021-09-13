package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instagram.ui.search.SearchViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends ArrayAdapter<SearchViewModel> {

    // invoke the suitable constructor of the ArrayAdapter class
    public SearchAdapter(@NonNull Context context, ArrayList<SearchViewModel> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.search_layout, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        SearchViewModel currentNumberPosition = getItem(position);

        // then according to the position of the view assign the desired image for the same
        CircleImageView profileImage = currentItemView.findViewById(R.id.profilepic);
        assert currentNumberPosition != null;
        Picasso.get().load(currentNumberPosition.getImageurl()).into(profileImage);

        // then according to the position of the view assign the desired TextView 2 for the same
        TextView name = currentItemView.findViewById(R.id.name);
        name.setText(currentNumberPosition.getUserName());

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity2.class);
                intent.putExtra("Selected_User_Id", currentNumberPosition.getUid());
                getContext().startActivity(intent);
            }
        });

        // then return the recyclable view
        return currentItemView;
    }
}