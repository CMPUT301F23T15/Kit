package com.example.kit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
<<<<<<< Updated upstream
import android.view.View;
import android.widget.Toast;
=======
import android.view.Menu;
import android.view.MenuItem;
>>>>>>> Stashed changes

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

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
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment == null) {
            Log.e("Navigation", "NavHostFragment Null, this is bad.");
            throw new NullPointerException("NavHostFragment Null");
        }
        //navHostFragment.getNavController();
    }
}
