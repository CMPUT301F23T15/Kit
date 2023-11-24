package com.example.kit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Main activity for the app, contains a NavHostFragment to display the various fragments of the app
 */
public class MainActivity extends AppCompatActivity {

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

        Intent login = new Intent(this, ProfileActivity.class);
        startActivity(login);

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment == null) {
            Log.e("Navigation", "NavHostFragment Null, this is bad.");
            throw new NullPointerException("NavHostFragment Null");
        }
        navHostFragment.getNavController();
    }

}
