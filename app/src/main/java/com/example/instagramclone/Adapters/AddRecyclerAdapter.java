package com.example.instagramclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Activities.AddActivity;
import com.example.instagramclone.R;

import java.io.File;
import java.util.ArrayList;

public class AddRecyclerAdapter extends RecyclerView.Adapter<AddRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<String> images;


    public AddRecyclerAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View gridFiles = LayoutInflater.from(context).inflate(R.layout.grid_files, parent, false);
        return new ViewHolder(gridFiles);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String image = images.get(position);

        Glide.with(context).load(image).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, AddActivity.class);
                intent.putExtra("imagePosition", position);
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, imageToBeLoaded;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            imageToBeLoaded = itemView.findViewById(R.id.imageToPost);

        }
    }
}
