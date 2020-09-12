package com.example.instagramclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    
    EditText username, fullName, email, password;
    Button register;

    DatabaseReference roofRef;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        username = findViewById(R.id.username);
        fullName = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);

        roofRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_txt = username.getText().toString();
                String fullname_txt = fullName.getText().toString();
                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();

                if(username_txt.isEmpty() || email_txt.isEmpty() || password_txt.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Fields cannot be left empty", Toast.LENGTH_SHORT).show();
                }
                else if (password_txt.length() < 6){
                    Toast.makeText(RegisterActivity.this, "The password is too short", Toast.LENGTH_SHORT).show();
                }
                else{
                    signUpUser(username_txt, fullname_txt, email_txt, password_txt);
                }
            }
        });
        
    }

    private void signUpUser(final String username, final String fullname, final String email, final String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("username", username);
                    map.put("fullname", fullname);
                    map.put("email", email);
                    map.put("password", password);
                    map.put("id", auth.getCurrentUser().getUid());
                    map.put("bio", "");
                    map.put("profileImage", "default");

                    roofRef.child("Users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "all files added successfull", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Toast.makeText(RegisterActivity.this, "Registration was successfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error : "+e.toString()+" Occured", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
