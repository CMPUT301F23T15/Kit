package com.example.kit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kit.data.FirestoreManager;
import com.example.kit.databinding.ProfilePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private ProfilePageBinding binding;
    private FirebaseAuth userAuth;

    Button deleteAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAuth = FirebaseAuth.getInstance();
        binding = ProfilePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        deleteAccount = findViewById(R.id.signOutButton);
        binding.signInButton.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            if (!isLoggedIn()) {
                signIn(email, password);
            } else {
                userAuth.signOut();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        // Delete account
        deleteAccount.setOnClickListener(v -> {
            Log.d("Delete Account", "Account deletion started");
            // Verify user intentions
            AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
            dialog.setTitle(R.string.confirmDelAccount);
            dialog.setMessage(R.string.delAccMessage);
            dialog.setPositiveButton("Delete", (dialog1, which) -> {
                FirebaseUser user = userAuth.getCurrentUser();
                user.delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        // Delete user items
                        FirestoreManager.getInstance().getCollection("Users")
                                .document(user.getUid()).collection("Items").whereNotEqualTo("name", null)
                                        .get().addOnCompleteListener(task1 -> {
                                            if(task1.isSuccessful()){
                                                for (QueryDocumentSnapshot document : task1.getResult()){
                                                    FirestoreManager.getInstance().getCollection("Users")
                                                            .document(user.getUid()).collection("Items")
                                                            .document(document.getId()).delete();
                                                }
                                            } else {
                                                Log.d("Error getting documents: ", task.getException().getMessage());
                                            }
                                        });
                        // Delete user tags
                        FirestoreManager.getInstance().getCollection("Users")
                                .document(user.getUid()).collection("Tags").whereNotEqualTo("alpha", null)
                                .get().addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task1.getResult()){
                                            FirestoreManager.getInstance().getCollection("Users")
                                                    .document(user.getUid()).collection("Tags")
                                                    .document(document.getId()).delete();
                                        }
                                    } else {
                                        Log.d("Error getting documents: ", task.getException().getMessage());
                                    }
                                });
                        // Delete user document
                        FirestoreManager.getInstance().getCollection("Users").document(user.getUid()).delete();
                        Log.d("Delete Account", "Account deleted succesfully!");
                        Toast.makeText(ProfileActivity.this, "Account Deleted", Toast.LENGTH_LONG).show();
                        // Reset to sign in screen
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }else{
                        // Prompt user to re-authenticate
                        Log.d("Delete Account", "Account deletion failed!");
//                        Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        AlertDialog.Builder reAuthenticate = new AlertDialog.Builder(ProfileActivity.this);
                        reAuthenticate.setTitle(R.string.reSignTitle)
                                .setMessage(R.string.reSignMessage)
                                .setPositiveButton("Yes", (dialog2, which1) -> {
                                    userAuth.signOut();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                })
                                .setNegativeButton("No", (dialogInterface, which1) -> dialogInterface.dismiss())
                                .create().show();
                    }
                });

            });
            dialog.setNegativeButton("Dismiss", (dialogInterface, which) -> dialogInterface.dismiss());
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        });


    }

    // When activity starts determines if a user is logged in, changing what is visible
    @Override
    protected void onStart() {
        super.onStart();
        if (isLoggedIn()) {
//            binding.email.setVisibility(View.GONE);
            binding.email.setText(userAuth.getCurrentUser().getEmail());
            binding.email.setEnabled(false);
            binding.passwordLayout.setVisibility(View.GONE);
            binding.signInButton.setText(R.string.signOut);
            binding.signOutButton.setText(R.string.deleteAccount);
            binding.signOutButton.setVisibility(View.VISIBLE);

        } else {
            binding.signInButton.setText(R.string.submit_button);
            binding.signOutButton.setVisibility(View.GONE);
            binding.email.setEnabled(true);
        }
    }

    /**
     * This method returns the true if a user is logged in, otherwise returns false
     *
     * @return State of logged in user
     */
    public boolean isLoggedIn() {
        FirebaseUser user = userAuth.getCurrentUser();
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method signs a user in, or if the user does not exist
     * it will prompt the user to make a new account
     *
     * @param email    String representing an email
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
                                .setNegativeButton("No", (dialog, which) -> {dialog.dismiss();});
                        builder.create().show();
                    });
        } catch (Exception e) {
            Toast.makeText(ProfileActivity.this, "Must Enter Email and Password", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a firebase user, and creates a document for the user inside
     * the database
     *
     * @param email    String in an email format
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
                    switch (e.getMessage()) {
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
    private void switchToMainActivity() {
        Log.d("Navigation", "Navigating to the list view");
        Intent listIntent = new Intent(this, MainActivity.class);
        startActivity(listIntent);
    }
}