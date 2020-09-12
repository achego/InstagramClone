package com.example.instagramclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Models.UserModel;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<UserModel> users;
    private boolean isFragment;

    FirebaseUser currentUser;

    public UserAdapter(Context context, ArrayList<UserModel> users, boolean isFragment) {
        this.context = context;
        this.users = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userItem = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(userItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {

        final UserModel user = users.get(position);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.btnfollow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());

        if (user.getProfileImage() != "default"){
            Glide.with(context).load(user.getProfileImage()).into(holder.profileImage);
        }
        else{
            holder.profileImage.setImageResource(R.mipmap.ic_launcher);
        }

        isFollowed(user.getId(), holder.btnfollow);

        if(user.getId().equals(currentUser.getUid())){
            holder.btnfollow.setVisibility(View.GONE);
        }

        holder.btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnfollow.getText().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid()).
                    child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).
                            child("followers").child(currentUser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid()).
                            child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).
                            child("followers").child(currentUser.getUid()).removeValue();
                }
            }
        });

    }

    private void isFollowed(final String id, final Button btnfollow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists()){
                    btnfollow.setText("following");
                }
                else{
                    btnfollow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView username, fullname;
        Button btnfollow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            btnfollow = itemView.findViewById(R.id.btnFollow);

        }
    }
}
