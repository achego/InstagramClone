package com.example.instagramclone.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import java.util.Collections;

import static com.example.instagramclone.R.id.errorCheck;


public class PostedImagesFragment extends Fragment {

    private FirebaseUser currentUser;
    private String userId;

    EditText errorCheck;

    private ArrayList<String> postedImages;

    private RecyclerView postedImageRecycler;
    AddRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View postedImage = inflater.inflate(R.layout.posted_image, container, false);
        return  postedImage;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postedImageRecycler = view.findViewById(R.id.postedImageRecycler);
        errorCheck = view.findViewById(R.id.errorCheck);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        postedImages = new ArrayList<>();

        postedImageRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new AddRecyclerAdapter(getContext(), postedImages);
        postedImageRecycler.setHasFixedSize(true);

        getUserPosts();

        postedImageRecycler.setAdapter(adapter);

        if (postedImages.size()!=0){
            errorCheck.setText(postedImages.get(0));
        }

    }

    private void getUserPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postedImages.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){

                    PostModel post = snapshot1.getValue(PostModel.class);
                    if (post.getPublisher().equals(userId)){
                        postedImages.add(post.getImageUrl());
                    }
                    Collections.reverse(postedImages);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
