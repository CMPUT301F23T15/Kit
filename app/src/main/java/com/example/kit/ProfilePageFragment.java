package com.example.kit;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kit.databinding.ProfilePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class ProfilePageFragment extends Fragment {
    private ProfilePageBinding binding;
    private NavController navController;
    private FirebaseAuth userAuth;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAuth = FirebaseAuth.getInstance();
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = userAuth.getCurrentUser();
        String uid = currentUser.getUid();

        if(false){
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
                        signIn(email, password);
                        navController.navigate(ProfilePageFragmentDirections.profilePageToItemList());

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
