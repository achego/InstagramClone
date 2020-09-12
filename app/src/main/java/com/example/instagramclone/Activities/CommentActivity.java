package com.example.instagramclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapters.CommentAdapter;
import com.example.instagramclone.Models.CommentModel;
import com.example.instagramclone.Models.UserModel;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView commentRecycler;
    private CircleImageView currentUserImage;
    private EditText addComment;
    private TextView post;

    private String postId, publisher;

    private List<CommentModel> commentList;
    CommentAdapter adapter;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        toolbar = findViewById(R.id.toolBar);
        addComment = findViewById(R.id.addComment);
        commentRecycler = findViewById(R.id.commentRecycler);
        currentUserImage = findViewById(R.id.userImage);
        post = findViewById(R.id.post);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        commentRecycler.setHasFixedSize(true);
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();

        adapter = new CommentAdapter(this, commentList);
        commentRecycler.setAdapter(adapter);

        Bundle intent = getIntent().getExtras();
        if(intent != null){
            postId = intent.getString("postId");
            publisher = intent.getString("publisher");
        }

        getComments();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        addComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (addComment.getText().toString().isEmpty()) {
                    post.setTextColor(Color.argb(60, 0, 56, 248));
                    post.setTag("cantComment");
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!addComment.getText().toString().isEmpty()) {
                    post.setTextColor(Color.rgb(29, 80, 255));
                    post.setTag("canComment");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loadUserImage(currentUser);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getTag().equals("cantComment")){

                }
                else {
                    addCommentToDatabase();
                }
            }
        });

    }

    private void getComments() {

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    CommentModel comment = snapshot1.getValue(CommentModel.class);
                    commentList.add(comment);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addCommentToDatabase() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("publisher", currentUser.getUid());
        map.put("comment", addComment.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).push().setValue(map).
        addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CommentActivity.this, "comment added", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CommentActivity.this, "failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadUserImage(FirebaseUser currentUser) {

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user.getProfileImage().equals("default")){
                    currentUserImage.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(getApplicationContext()).load(user.getProfileImage()).into(currentUserImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
