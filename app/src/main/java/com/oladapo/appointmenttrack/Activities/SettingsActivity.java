package com.oladapo.appointmenttrack.Activities;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.oladapo.appointmenttrack.R;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int startColor = ContextCompat.getColor(this, R.color.colorPrimary);
            int endColor = ContextCompat.getColor(this, R.color.colorPrimary);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        } else {

            int startColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            int endColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        }

        toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsActivity.SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}
