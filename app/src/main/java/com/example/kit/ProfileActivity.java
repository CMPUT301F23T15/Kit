package com.example.kit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kit.data.FirestoreManager;
import com.example.kit.databinding.ProfilePageBinding;
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
        binding.button.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            if(!isLoggedIn()){
                signIn(email, password);
            } else {
                userAuth.signOut();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    // When activity starts determines if a user is logged in, changing what is visible
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

    /**
     * This method returns the true if a user is logged in, otherwise returns false
     * @return State of logged in user
     */
    public boolean isLoggedIn(){
        FirebaseUser user = userAuth.getCurrentUser();
        if (user != null){
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method signs a user in, or if the user does not exist
     * it will prompt the user to make a new account
     * @param email String representing an email
     * @param password String representing the users password
     */
    public void signIn(String email, String password) {
        try {
            userAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        Log.i("Sign In", "Signed in successfully!");
                        switchToMainActivity();
                    })
                    .addOnFailureListener(e -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setMessage(R.string.createAccount)
                                .setPositiveButton("Yes", (dialog, which) -> createAccount(email, password))
                                .setNegativeButton("No", (dialog, which) -> {});
                        builder.create().show();
                    });
        } catch (Exception e) {
            Toast.makeText(ProfileActivity.this, "Must Enter Email and Password", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a firebase user, and creates a document for the user inside
     * the database
     * @param email String in an email format
     * @param password Password, must be greater than 6 characters
     */
    public void createAccount(String email, String password) {
        userAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d("Create User", "createUserWithEmail:success");
                    HashMap<String, String> userData = new HashMap<>();
                    userData.put("Email", userAuth.getCurrentUser().getEmail());
                    userData.put("userID", userAuth.getUid());
                    FirestoreManager.getInstance().getCollection("Users").document(userAuth.getCurrentUser().getUid()).set(userData);
                    signIn(email, password);

                })
                .addOnFailureListener(e -> {
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
                });

    }

    /**
     * Navigates to the main activity, which will open the list view (if logged in properly)
     */
    private void switchToMainActivity(){
        Log.d("Navigation", "Navigating to the list view");
        Intent listIntent = new Intent(this, MainActivity.class);
        startActivity(listIntent);
    }
}
