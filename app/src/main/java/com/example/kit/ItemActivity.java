package com.example.kit;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

public class ItemActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment == null) {
            Log.e("Navigation", "NavHostFragment Null, this is bad.");
            throw new NullPointerException("NavHostFragment Null");
        }
        navHostFragment.getNavController();
    }
}
