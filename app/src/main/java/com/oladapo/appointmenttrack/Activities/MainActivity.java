package com.oladapo.appointmenttrack.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_SIGN_IN = 1;

    private static final String TAG = "vkv";

    private PrimaryDrawerItem logout, login;
    private Drawer drawer;

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initToolbar();

        initDrawer();

        coordinatorLayout = findViewById(R.id.coordinator_layout);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.layout_container, new HomeFragment());
        transaction.commit();

        signIn();
    }

    private void signIn() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    if (!user.isAnonymous()) {
                        if (drawer.getDrawerItem(1) != null) {
                            drawer.removeItem(1);
                        }
                        if (drawer.getFooter() == null) {
                            drawer.addStickyFooterItem(logout);
                        }
                    } else {
                        if (drawer.getDrawerItem(1) == null) {
                            drawer.addItemAtPosition(login, 1);
                        }
                        if (drawer.getStickyFooter() != null) {
                            drawer.removeAllStickyFooterItems();
                        }
                    }
                } else {
                    mAuth.signInAnonymously()
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(coordinatorLayout, "You are signed in with an anonymous account", Snackbar.LENGTH_LONG).show();

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                linkAccountAlertDialog();
                                            }
                                        }, 3000);
                                    } else {
                                        Snackbar.make(coordinatorLayout, "Authentication failed!", Snackbar.LENGTH_LONG).show();
                                        if (drawer.getDrawerItem(1) == null) {
                                            drawer.addItem(login);
                                        }
                                    }
                                }
                            });
                }
            }
        };
    }

    //alertDialog for linking accounts
    private void linkAccountAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Would you like to link your Google account?")
                .setMessage("You are currently signed in as an anonymous user and in the " +
                        "instance of losing access to this account by uninstalling the app or clearing app data," +
                        " you would not regain access to the account. The only way to get back your account is by linking " +
                        "your Google account to avoid losing your data.")
                .setPositiveButton("Link Google account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signInWithGoogle();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do something
                    }
                })
                .show();
    }

    //google sign in
    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    //onActivityResult for google sign in
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();

                linkWithGoogle(Objects.requireNonNull(acct));
            } else {
                Snackbar.make(coordinatorLayout, "Authentication failed!", Snackbar.LENGTH_LONG).show();
                Log.i(TAG, result.toString());
            }
        }
    }

    //links google and anonymous accounts
    private void linkWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "link account with google successful");
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                Log.d(TAG, "google account collision");
                                firebaseAuthWithGoogle(acct);

                            } else {
                                Snackbar.make(coordinatorLayout, "Authentication failed!", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    //create user in firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "account creation successful");
                        } else {
                            Snackbar.make(coordinatorLayout, "Authentication failed!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //signs out user
    private void signOut() {
        mAuth.signOut();
        drawer.removeAllStickyFooterItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void initDrawer() {
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_background)
                .withTranslucentStatusBar(true)
                .withCurrentProfileHiddenInList(false)
                .build();

        login = new PrimaryDrawerItem()
                .withName("Login")
                .withIdentifier(1)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedTextColorRes(R.color.colorPrimary)
                .withSelectedColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withName("Home")
                .withIdentifier(2)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedTextColorRes(R.color.colorPrimary)
                .withSelectedColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem calender = new PrimaryDrawerItem()
                .withName("Calender")
                .withIdentifier(3)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedTextColorRes(R.color.colorPrimary)
                .withSelectedColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem reminders = new PrimaryDrawerItem()
                .withName("Reminders")
                .withIdentifier(4)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedTextColorRes(R.color.colorPrimary)
                .withSelectedColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName("Settings")
                .withIdentifier(5)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedTextColorRes(R.color.colorPrimary)
                .withSelectedColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem about = new PrimaryDrawerItem()
                .withName("About")
                .withIdentifier(6)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedTextColorRes(R.color.colorPrimary)
                .withSelectedColorRes(R.color.colorPrimaryDark);

        logout = new PrimaryDrawerItem()
                .withName("Logout")
                .withIdentifier(7)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedTextColorRes(R.color.colorPrimary)
                .withSelectedColorRes(R.color.colorPrimaryDark);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
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
                            signInWithGoogle();

                        } else if (i == 2) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new HomeFragment())
                                    .commit();

                        } else if (i == 3) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new CalenderFragment())
                                    .commit();

                        } else if (i == 4) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new RemindersFragment())
                                    .commit();

                        } else if (i == 5) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new SettingsFragment())
                                    .commit();

                        } else if (i == 6) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new AboutFragment())
                                    .commit();

                        } else if (i == 7) {
                            signOut();
                        }
                        return false;
                    }
                })
                .build();
    }
}
