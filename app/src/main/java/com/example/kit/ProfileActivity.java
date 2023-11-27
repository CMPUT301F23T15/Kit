package com.example.kit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        binding = ProfilePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                signIn(email, password);
                if(!isLoggedIn()){
                    createAccount(email, password);
                }
            }
        });

        return super.onCreateView(name, context, attrs);
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
        userAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.i("Sign In", "Signed in succesfully!");
                        toListView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        switch(e.getMessage()){
                            case "The supplied auth credential is incorrect, malformed or has expired.":
                                Log.d("Sign In", e.getMessage() + " Attempting to create account");
                                break;
                        }
                    }
                });
    }
    public void createAccount(String email, String password) {
        // [START create_user_with_email]
        userAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d("Create User", "createUserWithEmail:success");
                        HashMap<String, String> userData = new HashMap<>();
                        userData.put("Email", userAuth.getCurrentUser().getEmail());
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

                        }
                    }
                });

    }

    public void signOut(){
        userAuth.signOut();
    }

    private void toListView(){
        Intent listIntent = new Intent(this, MainActivity.class);
        startActivity(listIntent);
    }
}
