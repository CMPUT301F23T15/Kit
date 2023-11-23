package com.example.kit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kit.data.FirestoreManager;
import com.example.kit.databinding.ProfilePageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class ProfilePageFragment extends Fragment {
    private ProfilePageBinding binding;
    private NavController navController;
    private FirebaseAuth userAuth;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        userAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = userAuth.getCurrentUser();

        if(currentUser != null){
            Log.i("User Info", currentUser.getEmail() + " " + currentUser.getUid());
            navController.navigate(ProfilePageFragmentDirections.profilePageToItemList());
        }
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(binding.email.getText().toString(), binding.password.getText().toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfilePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void signIn(String email, String password) {
        userAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        navController.navigate(ProfilePageFragmentDirections.profilePageToItemList());
                        Log.i("Sign In", "Signed in succesfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        switch(e.getMessage()){
                            case "The supplied auth credential is incorrect, malformed or has expired.":
                                Log.d("Sign In", e.getMessage() + " Attempting to create account");
                                createAccount(email, password);
                                break;


                        }
                    }
                });

    }
    private void createAccount(String email, String password) {
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
    private void updateUI(FirebaseUser user) {

    }
}
