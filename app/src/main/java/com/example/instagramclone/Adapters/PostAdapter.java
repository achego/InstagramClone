package com.example.instagramclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Activities.CommentActivity;
import com.example.instagramclone.Models.PostModel;
import com.example.instagramclone.Models.UserModel;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private Context context;
    private List<PostModel> userPosts;

    private FirebaseUser currentUser;
    boolean result = false;

    public PostAdapter(Context context, List<PostModel> userPosts) {
        this.context = context;
        this.userPosts = userPosts;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View postItem = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostHolder(postItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, int position) {

        final PostModel userPost = userPosts.get(position);

        Glide.with(context).load(userPost.getImageUrl()).thumbnail(0.1f).into(holder.postImage);
        holder.description.setText(userPost.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(userPost.getPublisher()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);

                        if (user.getProfileImage() != "default"){
                            Glide.with(context).load(user.getProfileImage()).into(holder.authorProfileimage);
                        }
                        else{
                            holder.authorProfileimage.setImageResource(R.mipmap.ic_launcher);
                        }

                        holder.username.setText(user.getUsername());
                        holder.author.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        isLiked(userPost.getPostId(), holder.like);
        isSaved(userPost.getPostId(), holder.save);


        noOfLikes(userPost.getPostId(), holder.noOfLikes);
        noOfComments(userPost.getPostId(), holder.noOfComments);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.like.setImageResource(R.drawable.ic_liked);
                Toast.makeText(context, holder.like.getTag().toString(), Toast.LENGTH_SHORT).show();
                
                if (holder.like.getTag().equals("Like")){
                    Toast.makeText(context, "addingTo Data", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(userPost.getPostId())
                            .child(currentUser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(userPost.getPostId())
                            .child(currentUser.getUid()).removeValue();
                    Toast.makeText(context, "could not add", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", userPost.getPostId());
                intent.putExtra("publisher", userPost.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", userPost.getPostId());
                intent.putExtra("publisher", userPost.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.save.setImageResource(R.drawable.ic_saved);
                Toast.makeText(context, holder.save.getTag().toString(), Toast.LENGTH_SHORT).show();

                if (holder.save.getTag().equals("Save")){
                    Toast.makeText(context, "addingTo saved", Toast.LENGTH_SHORT).show();
                    HashMap<String, Object> map = new HashMap<>();
                    map.clear();
                    map.put(currentUser.getUid(), true);
                    map.put("imageUrl", userPost.getImageUrl());
                    FirebaseDatabase.getInstance().getReference().child("Saved").child(userPost.getPostId()).setValue(map);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Saved").child(userPost.getPostId())
                            .child(currentUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Saved").child(userPost.getPostId())
                            .child("imageUrl").removeValue();
                    Toast.makeText(context, "could not add", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void isSaved(String postId, final ImageView save) {

        FirebaseDatabase.getInstance().getReference().child("Saved").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(currentUser.getUid()).exists()){
                    save.setImageResource(R.drawable.ic_saved);
                    save.setTag("Saved");
                }
                else{
                    save.setImageResource(R.drawable.ic_save_to_collection);
                    save.setTag("Save");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void noOfComments(String postId, final TextView noOfComments) {

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String comments = "";
                if ((int)snapshot.getChildrenCount() <= 1){
                    comments = "View " + (int)snapshot.getChildrenCount() + " comment";
                }
                else if ((int)snapshot.getChildrenCount() == 0){
                    noOfComments.setVisibility(View.GONE);
                }
                else{
                    comments = "View all " + (int)snapshot.getChildrenCount() + " comments";
                }

                noOfComments.setText(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isLiked(String postId, final ImageView like) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(currentUser.getUid()).exists()){
                    like.setImageResource(R.drawable.ic_liked);
                    like.setTag("Liked");
                }
                else{
                    like.setImageResource(R.drawable.ic_like);
                    like.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noOfLikes(String postId, final TextView noOfLikes){

        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String likes = "";
                if ((int)snapshot.getChildrenCount() <= 1){
                    likes = (int)snapshot.getChildrenCount() + " like";
                }
                else if ((int)snapshot.getChildrenCount() == 0){
                    noOfLikes.setVisibility(View.GONE);
                }
                else {
                    likes = (int)snapshot.getChildrenCount() + " likes";
                }
                noOfLikes.setText(likes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        ImageView authorProfileimage, currentUserProfileImage, postImage, like, comment, save, more;
        TextView username, noOfLikes, author, noOfComments, timeOfPost;
        SocialTextView description;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            authorProfileimage = itemView.findViewById(R.id.authorProfileImage);
            currentUserProfileImage = itemView.findViewById(R.id.currentUserProfileImage);
            postImage = itemView.findViewById(R.id.postImage);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.saveToCollection);
            more = itemView.findViewById(R.id.more);

            username = itemView.findViewById(R.id.username);
            noOfLikes = itemView.findViewById(R.id.noOfLikes);
            noOfComments = itemView.findViewById(R.id.noOfComments);
            author = itemView.findViewById(R.id.author);
            authorProfileimage = itemView.findViewById(R.id.authorProfileImage);
            timeOfPost = itemView.findViewById(R.id.timeOfPost);
            description = itemView.findViewById(R.id.description);

        }
    }
}
