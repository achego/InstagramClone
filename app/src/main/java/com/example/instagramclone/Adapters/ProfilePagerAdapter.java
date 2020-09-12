package com.example.instagramclone.Adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.instagramclone.Fragments.PostedImagesFragment;
import com.example.instagramclone.Fragments.SavedImagesFragment;

import java.util.ArrayList;
import java.util.List;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    PostedImagesFragment postedImagesFragment;
    SavedImagesFragment savedImagesFragment;

    List<Fragment> fragments;
    List<String> pageTitles;

    public ProfilePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

       fragments = new ArrayList<>();

        pageTitles = new ArrayList<>();

    }

    public void addFragment(Fragment fragment, String pageTitle) {

        fragments.add(fragment);
        pageTitles.add(pageTitle);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }

}
