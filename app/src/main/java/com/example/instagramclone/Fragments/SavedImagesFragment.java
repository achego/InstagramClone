package com.example.instagramclone.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Adapters.AddRecyclerAdapter;
import com.example.instagramclone.Models.PostModel;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedImagesFragment extends Fragment {

    private FirebaseUser currentUser;
    private String userId;

    private ArrayList<String> savedImages;

    private RecyclerView savedImageRecycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View savedImage = inflater.inflate(R.layout.saved_image, container, false);
        return  savedImage;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        savedImageRecycler = view.findViewById(R.id.savedImageRecycler);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        savedImages = new ArrayList<>();

        savedImageRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        AddRecyclerAdapter adapter = new AddRecyclerAdapter(getContext(), savedImages);
        savedImageRecycler.setHasFixedSize(true);

        getSavedImages();

        savedImageRecycler.setAdapter(adapter);
        
    }

    private void getSavedImages() {

        FirebaseDatabase.getInstance().getReference().child("Saved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    PostModel post = snapshot1.getValue(PostModel.class);

                    if (snapshot1.hasChild(userId)) {
                        savedImages.add(post.getImageUrl());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
