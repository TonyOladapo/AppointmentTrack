package com.oladapo.appointmenttrack.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.oladapo.appointmenttrack.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int startColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            int endColor = ContextCompat.getColor(this, R.color.colorPrimary);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        } else {

            int startColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            int endColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        }

        setupNavigation();
    }

    private void setupNavigation() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, drawer);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawer);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);
        drawer.closeDrawers();

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                navController.navigate(R.id.upcomingFragment);
                break;

            case R.id.nav_all:
                navController.navigate(R.id.allAppointmentsFragment);
                break;

            case R.id.nav_reminders:
                navController.navigate(R.id.remindersFragment);
                break;

            case R.id.nav_settings:
                startSettingsActivity();
                break;

            case R.id.nav_about:
                startAboutActivity();
                break;
        }
        return true;
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void startAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
