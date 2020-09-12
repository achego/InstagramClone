package com.example.instagramclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapters.ProfilePagerAdapter;
import com.example.instagramclone.Fragments.PostedImagesFragment;
import com.example.instagramclone.Fragments.SavedImagesFragment;
import com.example.instagramclone.Models.PostModel;
import com.example.instagramclone.Models.UserModel;
import com.example.instagramclone.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView usename, userNoOfPost, userNoOfFollowers, userNoOfFollowing, userFullname, userBio;
    private ImageView moreOptions;
    private CircleImageView userProfileImage;
    private Button editProfile;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private FirebaseUser currentUser;

    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        usename = findViewById(R.id.usersUsername);
        moreOptions = findViewById(R.id.moreOptions);
        userProfileImage = findViewById(R.id.userProfileImage);
        userNoOfPost = findViewById(R.id.userNoOfPost);
        userNoOfFollowers = findViewById(R.id.userNoOfFollowers);
        userNoOfFollowing = findViewById(R.id.userNoOfFollowing);
        userFullname = findViewById(R.id.userFullname);
        userBio = findViewById(R.id.userBio);
        editProfile = findViewById(R.id.editProfile);


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        ProfilePagerAdapter pagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new PostedImagesFragment(), "");
        pagerAdapter.addFragment(new SavedImagesFragment(), "");

        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_posted_images);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_saved_images);

        setTabIcons(1, R.drawable.ic_saved_images_unselected, R.drawable.ic_saved_images);

        displayUserInfo();
        getNoOfPost();
        getNoOfFollowingAndFollowers();

    }

    private void setTabIcons(int position, int unselectedIcon, int selectedIcon) {

        if (tabLayout.getTabAt(position).isSelected()){
            tabLayout.getTabAt(position).setIcon(unselectedIcon);
        }
        else {
            tabLayout.getTabAt(position).setIcon(selectedIcon);
        }

    }

    private void getNoOfFollowingAndFollowers() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("followers")){
                    userNoOfFollowers.setText(String.valueOf(snapshot.child("followers").getChildrenCount()));
                }
                if (snapshot.hasChild("following")){
                    userNoOfFollowing.setText(String.valueOf(snapshot.child("following").getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getNoOfPost() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    PostModel post = snapshot1.getValue(PostModel.class);
                    if (post.getPublisher().equals(userId)){
                        counter++;
                    }
                }
                userNoOfPost.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void displayUserInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);

                Glide.with(getApplicationContext()).load(user.getProfileImage()).thumbnail(0.1f).into(userProfileImage);
                usename.setText(user.getUsername());
                userFullname.setText(user.getFullname());
                userBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
