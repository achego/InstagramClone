package com.example.instagramclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagramclone.Fragments.HeartFragment;
import com.example.instagramclone.Fragments.HomeFragment;
import com.example.instagramclone.Fragments.SearchFragment;
import com.example.instagramclone.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.home :
                        selectedFragment = new HomeFragment();
                        break;

                    case R.id.search :
                        selectedFragment = new SearchFragment();
                        break;

                    case R.id.add :
                        startActivity(new Intent(getApplicationContext(), AddActivity.class).
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        break;

                    case R.id.heart :
                        selectedFragment = new HeartFragment();
                        break;

                    case R.id.profile :
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class).
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        break;
                }

                if(selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                }
                return true;
            }
        });

        /*Bundle intent = getIntent().getExtras();
        if (intent != null){
            String profileId = intent.getString("publisherId");
            getSharedPreferences("profile", MODE_PRIVATE).edit().putString("profileId", profileId);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ProfileFragment()).commit();
        }
        else {

        }*/

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();

    }
}
