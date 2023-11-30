package com.example.kit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Main activity for the app, contains a NavHostFragment to display the various fragments of the app
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth userAuth;
    /**
     * Sets the content view with the NavHostFragment.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @throws NullPointerException
     *  If NavHostFragment is null, which is almost should never be.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userAuth = FirebaseAuth.getInstance();
        if(!isLoggedIn()) {
            Intent login = new Intent(this, ProfileActivity.class);
            startActivity(login);
        } else {
            NavHostFragment navHostFragment = (NavHostFragment)
                    getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
            if (navHostFragment == null) {
                Log.e("Navigation", "NavHostFragment Null, this is bad.");
                throw new NullPointerException("NavHostFragment Null");
            }
            navHostFragment.getNavController();
        }


    }

    /**
     * This method returns the true if a user is logged in, otherwise returns false
     * @return State of logged in user
     */
    private boolean isLoggedIn(){
        FirebaseUser user = userAuth.getCurrentUser();
        if (user != null){
            return true;
        } else {
            return false;
        }
    }

}
