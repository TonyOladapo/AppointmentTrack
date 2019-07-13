package com.oladapo.appointmenttrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.oladapo.appointmenttrack.Fragments.HomeFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_SIGN_IN = 1;

    private static final String TAG = "vkv";

    private PrimaryDrawerItem logout;
    private PrimaryDrawerItem login;
    private Drawer drawer;
    private AccountHeader header;

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        signIn();
    }

    //starts authentication flow
    private void signIn() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    if (!user.isAnonymous()) {
                        updateUI(user);

                        if (drawer.getDrawerItem(1) != null) {
                            drawer.removeItem(1);
                        }

                        header.clear();

                        header.addProfiles(new ProfileDrawerItem().withIdentifier(1).withName(user.getDisplayName()).withEmail(user.getEmail()));
                        drawer.removeItem(1);
                        drawer.addStickyFooterItem(logout);
                    } else {
                        if (drawer.getDrawerItem(1) == null) {
                            drawer.addItem(login);
                        }

                        header.addProfiles(new ProfileDrawerItem().withName("User"));
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

                                        header.addProfiles(new ProfileDrawerItem().withName("User"));
                                    }
                                }
                            });
                }
            }
        };
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

    //updates ui onAuthStateChanged
    private void updateUI(FirebaseUser user) {

        String displayName = user.getDisplayName();

        if (displayName == null) {
            enterNameAlertDialog();
        }
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

    //alertDialog for entering username
    private void enterNameAlertDialog() {

        final EditText enterNameEditText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("What would you like us to call you?")
                .setMessage("Enter any name you wish for us to call you. This can be changed later in settings.")
                .setView(enterNameEditText)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String userName = enterNameEditText.getText().toString();

                        setUserName(userName);
                    }
                })
                .setCancelable(false)
                .show();
    }

    //update user name in firebase
    private void setUserName(final String userName) {
        final FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        Objects.requireNonNull(user).updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            String displayName = user.getDisplayName();

                            Snackbar.make(coordinatorLayout, "Welcome " + displayName, Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(coordinatorLayout, "Error changing display name. This can be changed later in settings", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //initializes toolbar
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    //initializes navigation drawer
    private void initDrawer() {
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

        logout = new PrimaryDrawerItem()
                .withName("Logout")
                .withIdentifier(3)
                .withTextColorRes(R.color.colorPrimaryDark)
                .withSelectedTextColorRes(R.color.colorPrimary)
                .withSelectedColorRes(R.color.colorPrimaryDark);

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withTranslucentStatusBar(true)
                .withCurrentProfileHiddenInList(false)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        home
                )
                .withActionBarDrawerToggleAnimated(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        long i = drawerItem.getIdentifier();

                        if (i == 2) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .addToBackStack(null)
                                    .replace(R.id.layout_container, new HomeFragment())
                                    .commit();
                        } else if (i == 1) {
                            signInWithGoogle();
                        } else if (i == 3) {
                            signOut();
                        }
                        return false;
                    }
                })
                .build();
    }

    //signs out user
    private void signOut() {
        mAuth.signOut();
        header.removeProfileByIdentifier(1);
        drawer.removeAllStickyFooterItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
