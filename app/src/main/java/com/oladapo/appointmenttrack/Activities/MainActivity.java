package com.oladapo.appointmenttrack.Activities;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.oladapo.appointmenttrack.Fragments.AboutFragment;
import com.oladapo.appointmenttrack.Fragments.CalenderFragment;
import com.oladapo.appointmenttrack.Fragments.HomeFragment;
import com.oladapo.appointmenttrack.Fragments.RemindersFragment;
import com.oladapo.appointmenttrack.Fragments.SettingsFragment;
import com.oladapo.appointmenttrack.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

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

        initToolbar();

        initDrawer();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.layout_container, new HomeFragment());
        transaction.commit();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void initDrawer() {

        AccountHeader header = new AccountHeaderBuilder()
                .withCompactStyle(true)
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header_background)
                .build();

        PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withName("Home")
                .withIdentifier(1)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedColorRes(R.color.colorLight)
                .withSelectedTextColorRes(R.color.colorPrimaryDark)
                .withIcon(R.drawable.ic_home_black_24dp);

        PrimaryDrawerItem calender = new PrimaryDrawerItem()
                .withName("Calender")
                .withIdentifier(2)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedColorRes(R.color.colorLight)
                .withSelectedTextColorRes(R.color.colorPrimaryDark)
                .withIcon(R.drawable.ic_calender_black_24dp);

        PrimaryDrawerItem reminders = new PrimaryDrawerItem()
                .withName("Reminders")
                .withIdentifier(3)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedColorRes(R.color.colorLight)
                .withSelectedTextColorRes(R.color.colorPrimaryDark)
                .withIcon(R.drawable.ic_access_alarm_black_24dp);

        PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName("Settings")
                .withIdentifier(4)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedColorRes(R.color.colorLight)
                .withSelectedTextColorRes(R.color.colorPrimaryDark)
                .withIcon(R.drawable.ic_settings_black_24dp);

        PrimaryDrawerItem about = new PrimaryDrawerItem()
                .withName("About")
                .withIdentifier(5)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedColorRes(R.color.colorLight)
                .withSelectedTextColorRes(R.color.colorPrimaryDark)
                .withIcon(R.drawable.ic_info_outline_black_24dp);

        new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .withToolbar(toolbar)
                .addDrawerItems(
                        home,
                        calender,
                        reminders,
                        new DividerDrawerItem(),
                        settings,
                        about
                )
                .withActionBarDrawerToggleAnimated(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        long i = drawerItem.getIdentifier();

                        if (i == 1) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new HomeFragment())
                                    .commit();

                        } else if (i == 2) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new CalenderFragment())
                                    .commit();

                        } else if (i == 3) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new RemindersFragment())
                                    .commit();

                        } else if (i == 4) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new SettingsFragment())
                                    .commit();

                        } else if (i == 5) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new AboutFragment())
                                    .commit();

                        }
                        return true;
                    }
                })
                .build();
    }
}
