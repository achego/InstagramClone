package com.example.instagramclone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapters.AddRecyclerAdapter;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddActivity extends AppCompatActivity {

    private ImageView close, imageToPost;
    private TextView post, error;
    RecyclerView recyclerView;

    private SocialAutoCompleteTextView description;

    private ArrayList<String> images;
    private Uri imageUri;
    private String imageUrl;

    private StorageReference storageRef;

    String imagePath = Environment.getExternalStorageDirectory()+ File.separator+"fireBaseDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        close = findViewById(R.id.closePost);
        imageToPost = findViewById(R.id.imageToPost);
        post = findViewById(R.id.post);
        recyclerView = findViewById(R.id.postRecycler);
        description = findViewById(R.id.description);
        error =findViewById(R.id.error);

        //CropImage.activity().start(AddActivity.this);

        images = new ArrayList<>();

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);

        addFiles(imagePath);

        Bundle intent = getIntent().getExtras();
        if (intent == null){
            Glide.with(this).load(images.get(0)).into(imageToPost);
            imageUri = Uri.fromFile(new File(images.get(0)));
            Toast.makeText(this, "Intent Null", Toast.LENGTH_SHORT).show();
        }
        else{
            int pos = intent.getInt("imagePosition");
            Toast.makeText(this, intent.toString() + " position - "+pos, Toast.LENGTH_SHORT).show();
            imageUri = Uri.fromFile(new File(images.get(pos)));
            Glide.with(this).load(images.get(pos)).into(imageToPost);
        }

        AddRecyclerAdapter adapter = new AddRecyclerAdapter(this, images);

        recyclerView.setAdapter(adapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });
    }

    private void addFiles(String imagePath) {

        File imageFiles = new File(imagePath);
        File[] imageFileArray = imageFiles.listFiles();

        for (File file : imageFileArray){
            images.add(file.getPath());
        }

    }

    private void uploadPost() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri!=null){

            //description.setText(imageUri.toString());

            final StorageReference fileRef = FirebaseStorage.getInstance().getReference("Posts").child(System.
                    currentTimeMillis()+getFileExtention(imageUri));

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    //Toast.makeText(AddActivity.this, "Completed actually", Toast.LENGTH_SHORT).show();

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            addToDatabase(url);
                            Toast.makeText(AddActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddActivity.this, "Post not Uploaded, an Error occurred", Toast.LENGTH_SHORT).show();
                            //description.setText(e.getMessage());
                        }
                    });

                    pd.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        }
        else{
            Toast.makeText(this, "No Image was selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToDatabase(String imageUrl) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        String postId = reference.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("postId", postId);
        map.put("imageUrl", imageUrl);
        map.put("description", description.getText().toString());
        //map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("publisher", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert postId != null;
        reference.child(postId).setValue(map);

        DatabaseReference hashTag = FirebaseDatabase.getInstance().getReference("HashTags");
        List<String> hashTags = description.getHashtags();
        if (!hashTags.isEmpty()){
            for (String tag : hashTags){
                map.clear();
                map.put("hashTag", tag.toLowerCase());
                map.put("postId", postId);

                hashTag.child(tag.toLowerCase()).child(postId).setValue(map);
            }
        }

    }

    private String getFileExtention(Uri imageUri) {

        String fileName = imageUri.getPath();
        assert fileName != null;
        int dot = fileName.lastIndexOf(".");

        return fileName.substring(dot, fileName.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            imageUri = result.getUri();
            Glide.with(getApplicationContext()).load(imageUri).into(imageToPost);
        }
        else {
            Toast.makeText(this, "Image did not load please try again", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
