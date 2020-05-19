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
import androidx.core.util.Pair;

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
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.util.Collections;
import java.util.List;

import es.ucm.fdi.moodcalendar.viewModel.DriveServiceHelper;

public class LogInActivity extends AppCompatActivity {
    private static final String TAG = "LogInActivity";

    private static final String AUTH_USER = "auth";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_DRIVE_AUTH = 4001;
    //private static final Scope ACCESS_DRIVE_SCOPE = new Scope(Scopes.DRIVE_APPFOLDER);
    private static final Scope ACCESS_DRIVE_SCOPE = new Scope(Scopes.DRIVE_FULL);
    private static final Scope SCOPE_EMAIL = new Scope(Scopes.EMAIL);

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSingInButton;
    private DriveServiceHelper mDriveServiceHelper;
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

        if(requestCode == RC_SIGN_IN && resultCode == RESULT_OK){
            errorText.setVisibility(View.GONE);
            Log.i(TAG, "User has logged in with its google account");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSingInResul(task);
            return;
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

    private void testDriveFunctionality() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        driveSetUp(account);

        Task<String> task = mDriveServiceHelper.searchFile();

        task.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.d(TAG, "onComplete: Trying to find a file");
                String fileId = task.getResult();
                Log.d(TAG, "drive file detected: " + fileId);
                if(fileId != null){
                    Log.d(TAG, "Drive file found, id: " + fileId);
                    Log.d(TAG, "Proceeding to update file...");
                    mDriveServiceHelper.saveFile(fileId, "This is a test");
                    mDriveServiceHelper.readFile(fileId).addOnCompleteListener(new OnCompleteListener<Pair<String, String>>() {
                        @Override
                        public void onComplete(@NonNull Task<Pair<String, String>> task) {
                            Pair<String, String> data = task.getResult();
                            Log.d(TAG, "Trying to read file content");
                            if(data != null){
                                Log.d(TAG, "(first, second)= (" + data.first + ", " + data.second + ")");
                            }else{
                                Log.d(TAG, "Error");
                            }
                        }
                    });
                }else {
                    Log.d(TAG, "File not detected, proceeding to create it...");
                    mDriveServiceHelper.createFile();
                }
            }
        });

        Task<List<File>> listTask = mDriveServiceHelper.listFiles();


        listTask.addOnCompleteListener(new OnCompleteListener<List<File>>() {
            @Override
            public void onComplete(@NonNull Task<List<File>> task) {
                try {
                    Log.d(TAG, "onComplete: trying to list all files");
                    for (File fil : task.getResult()) {
                        Log.d(TAG, "(file name, file id, MIME type) = (" + fil.getName() + ", " + fil.getId() + ", " + fil.getMimeType() + ")");
                    }
                }catch (NullPointerException e){
                    Log.d(TAG, "NullPointerException. Maybe there isn't any file inside?");
                }
            }
        });
    }

    private void goToMainActivity() {
        Intent toMainActivity = new Intent(this, MainActivity.class);
        startActivity(toMainActivity);
        finish();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

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

    private void processLogOut() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: logged out !!!!");
            }
        });

        finish();
    }

    private void driveSetUp(GoogleSignInAccount mAccount) {

        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Collections.singleton(ACCESS_DRIVE_SCOPE.getScopeUri()));

        credential.setSelectedAccount(mAccount.getAccount());

        Drive googleDriveService =
                new Drive.Builder(
                        new NetHttpTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName("MoodCalendar")
                        .build();

        String searchScope = "drive";
        //String scope = "appDataFolder";

        mDriveServiceHelper = new DriveServiceHelper(googleDriveService, "root", searchScope);
    }


}
