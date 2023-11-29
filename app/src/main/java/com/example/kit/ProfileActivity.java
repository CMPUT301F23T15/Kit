package com.example.kit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kit.data.FirestoreManager;
import com.example.kit.databinding.ProfilePageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private ProfilePageBinding binding;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAuth = FirebaseAuth.getInstance();
        binding = ProfilePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                if(!isLoggedIn()){
                    signIn(email, password);
                } else {
                    signOut();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isLoggedIn()){
            binding.email.setVisibility(View.GONE);
            binding.password.setVisibility(View.GONE);
            binding.button.setText(R.string.signOut);

        } else {
            binding.button.setText(R.string.submit_button);

        }
    }

    public boolean isLoggedIn(){
        FirebaseUser user = userAuth.getCurrentUser();
        if (user != null){
            return true;
        } else {
            return false;
        }
    }
    public void signIn(String email, String password) {
        try {
            userAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.i("Sign In", "Signed in successfully!");
                            toListView();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                            builder.setMessage(R.string.createAccount)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            createAccount(email, password);
                                        }
                                    });
                            builder.create().show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(ProfileActivity.this, "Must Enter Email and Password", Toast.LENGTH_SHORT).show();
        }
    }
    public void createAccount(String email, String password) {
        userAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d("Create User", "createUserWithEmail:success");
                        HashMap<String, String> userData = new HashMap<>();
                        userData.put("Email", userAuth.getCurrentUser().getEmail());
                        userData.put("userID", userAuth.getUid());
                        FirestoreManager.getInstance().getCollection("Users").document(userAuth.getCurrentUser().getUid()).set(userData);
                        signIn(email, password);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Create User", "createUserWithEmail:failure", e);
                        switch(e.getMessage()){
                            case "Password should be at least 6 characters":
                                Log.i("Create User", e.getMessage());
                                break;
                            case "The email address is already in use by another account.":
                                Log.i("Create User", e.getMessage());
                                break;
                            case "The email address is badly formatted.":
                                Log.i("Create User", e.getMessage());
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setMessage(e.getMessage()).create().show();
                    }
                });

    }

    public void signOut(){
        userAuth.signOut();
    }

    private void toListView(){
        Log.d("Navigation", "Navigating to the list view");
        Intent listIntent = new Intent(this, MainActivity.class);
        startActivity(listIntent);
    }
}
