package com.example.instagramclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Activities.MainActivity;
import com.example.instagramclone.Models.CommentModel;
import com.example.instagramclone.Models.UserModel;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    Context context;
    List<CommentModel> comments;

    private FirebaseUser currentUser;

    public CommentAdapter(Context context, List<CommentModel> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View comment = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentHolder(comment);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final CommentModel userComment = comments.get(position);

        getUserNameAndProfilePic(userComment, holder.userName, holder.userProfilePhoto);
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("publisherId", userComment.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.userProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("publisherId", userComment.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.comment.setText(userComment.getComment());

    }

    private void getUserNameAndProfilePic(CommentModel userComment, final TextView userName, final CircleImageView userProfilePhoto) {

        FirebaseDatabase.getInstance().getReference().child("Users").child(userComment.getPublisher())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                userName.setText(user.getUsername());

                if (user.getProfileImage().equals("default")){
                    userProfilePhoto.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(context).load(user.getProfileImage()).into(userProfilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        TextView userName, comment;
        CircleImageView userProfilePhoto;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.userCommment);
            userProfilePhoto = itemView.findViewById(R.id.userImage);

        }
    }
}
