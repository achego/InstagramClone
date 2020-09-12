package com.example.instagramclone.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.Adapters.PostAdapter;
import com.example.instagramclone.Models.PostModel;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//TODO : if an account does not have any follower the app crashes

public class HomeFragment extends Fragment {

    private ImageView camera, sendMessage;
    private RecyclerView statusRecycler, postRecycler;
    private TextView followUser;

    private PostAdapter postAdapter;
    private List<PostModel> posts;
    private List<String> followingList;

    private DatabaseReference followRef;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        camera = view.findViewById(R.id.camera);
        sendMessage = view.findViewById(R.id.sendMessage);
        statusRecycler = view.findViewById(R.id.userStatus);
        postRecycler = view.findViewById(R.id.userPosts);
        followUser = view.findViewById(R.id.followUsers);

        postRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postRecycler.setLayoutManager(layoutManager);

        posts = new ArrayList<>();
        followingList = new ArrayList<>();


        postAdapter = new PostAdapter(getContext(), posts);
        postRecycler.setAdapter(postAdapter);

        followRef = FirebaseDatabase.getInstance().getReference().child("Follow");

        followRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").exists()){
                    checkFollowingUsers();
                }
                else {
                    followUser.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkFollowingUsers() {

        followRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
        child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    followingList.add(snapshot1.getKey());

                }
                //Toast.makeText(getContext(), "followingList Added : " + followingList.get(0), Toast.LENGTH_SHORT).show();
                readPosts();
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    PostModel post = snapshot1.getValue(PostModel.class);

                    for (String id : followingList){
                        if (post.getPublisher().equals(id)){
                            posts.add(post);
                        }
                    }
                    //Toast.makeText(getContext(), "posts added : " + posts.get(0).getDescription(), Toast.LENGTH_SHORT).show();
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
