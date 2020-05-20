package es.ucm.fdi.moodcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * First Activity that a user will see, it will have a button for login in with
 * a gmail account and another button for log out.<br>
 * Once a user has logged in, it will start the MainActivity
 * */
public class LogInActivity extends AppCompatActivity {
    private static final String TAG = "LogInActivity";
    /**
     * While signing in we will start a new activity made by Google which
     * will request a gmail account for loging in, this constant value is
     * for identifying that Activity when it returns to the current one
     *
     * @see <a href="https://developers.google.com/identity/sign-in/android/sign-in">Google sign in process</a>
     * */
    private static final int RC_SIGN_IN = 9001;
    /**
     * Our app will need authorization by the user to use its Drive, to do so we
     * will trigger another activity. This constant is for identifying that
     * activity once it's finished and returned a value to this one
     * */
    private static final int RC_DRIVE_AUTH = 4001;
    //access to a hidden folder in the user's drive called appdata where it
    //will store all the application information
    //private static final Scope ACCESS_DRIVE_SCOPE = new Scope(Scopes.DRIVE_APPFOLDER);
    //access to "My drive" folder
    private static final Scope ACCESS_DRIVE_SCOPE = new Scope(Scopes.DRIVE_FULL);
    /**
     * We will requesting an email as a method to signing in
     * */
    private static final Scope SCOPE_EMAIL = new Scope(Scopes.EMAIL);

    /**
     * Google Sign in client where it stores all the information about
     * a signed in user
     * */
    private GoogleSignInClient mGoogleSignInClient;
    /**
     * Sign in button given by google, it will handle all the authentication
     * for us
     * */
    private SignInButton googleSingInButton;
    /**
     * Log out button. It will revoke all permissions of the current app to
     * access the user's drive space and will log out the account
     * */
    private Button logOut;
    private TextView errorText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        googleSingInButton = findViewById(R.id.google_sign_in_button);
        errorText = findViewById(R.id.errorSignInText);
        logOut = findViewById(R.id.logOutButton);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSingInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LogInActivity.this);

                if(GoogleSignIn.hasPermissions(account, ACCESS_DRIVE_SCOPE, SCOPE_EMAIL)){
                    goToMainActivity();
                }else{
                    //we should start al authentication here
                    signIn();
                }
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogOut();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //activity for requesting a gmail account to log in
        if(requestCode == RC_SIGN_IN && resultCode == RESULT_OK){
            errorText.setVisibility(View.GONE);
            Log.i(TAG, "User has logged in with its google account");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSingInResul(task);
            return;
        //activity for requesting permissions to access the user's Drive
        } else if(requestCode == RC_DRIVE_AUTH && resultCode == RESULT_OK){
            Log.i(TAG, "User has granted its permission to use him/her google drive");
            goToMainActivity();
            return;
        }

        if(resultCode == RESULT_CANCELED){
            return;
        }

        errorText.setVisibility(View.VISIBLE);
    }

    /**
     * Will start the MainActivity and finish the current one
     * */
    private void goToMainActivity() {
        Intent toMainActivity = new Intent(this, MainActivity.class);
        startActivity(toMainActivity);
        finish();
    }

    /**
     * Will start a Google Activity for login with a gmail account
     * */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Will be called when the Activity for requesting a gmail account has ended.<br>
     * It will process the result and start the MainActivity if everything went correct
     * */
    private void handleSingInResul(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //request access to authenticated user's drive
            if(!GoogleSignIn.hasPermissions(account, ACCESS_DRIVE_SCOPE, SCOPE_EMAIL)){
                GoogleSignIn.requestPermissions(this, RC_DRIVE_AUTH, account, ACCESS_DRIVE_SCOPE, SCOPE_EMAIL);
                Log.i(TAG, "requesting access to drive");
            }else{
                goToMainActivity();
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * It will close the application, revoke all permissions given and log out from the gmail
     * account
     * */
    private void processLogOut() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: logged out !!!!");
            }
        });

        finish();
    }
}
