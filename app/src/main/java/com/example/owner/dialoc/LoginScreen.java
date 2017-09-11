package com.example.owner.dialoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class LoginScreen extends AppCompatActivity {

    private final String TAG = "LoginScreen";
    private TextInputEditText emailView;
    private TextInputEditText passwordView;
    private Intent homeScreenIntent;
    private Intent registerIntent;
    private Button loginButton;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private TextView skip_text;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager = CallbackManager.Factory.create();
    private LoginButton fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);


    @Override
    public void onStart() {
        Log.w(TAG, "signInWithFacebook:cancelled");
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
            startActivity(homeScreenIntent);
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        emailView = (TextInputEditText) findViewById(R.id.email);
        passwordView = (TextInputEditText) findViewById(R.id.password);
        registerIntent = new Intent(this, RegisterActivity.class);
        homeScreenIntent = new Intent(this, HomeScreenActivity.class);
        skip_text = (TextView) findViewById(R.id.skip_signin);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(registerIntent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (validateLogin()) {
                    OnCompleteListener loginListener = new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                startActivity(homeScreenIntent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Snackbar.make(v, "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    };
                    mAuth.signInWithEmailAndPassword(emailView.getText().toString(), passwordView.getText().toString())
                            .addOnCompleteListener(LoginScreen.this, loginListener);

                }
            }
        });


        // Callback registration
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                startActivity(homeScreenIntent);
                finish();
            }

            @Override
            public void onCancel() {
                //Snackbar.make(v, "Facebook Authentication Cancelled", Snackbar.LENGTH_SHORT).show();
                Log.w(TAG, "signInWithFacebook:cancelled");

            }

            @Override
            public void onError(FacebookException exception) {
                Log.w(TAG, "signInWithFacebook:failure", exception);
                //Snackbar.make(v, "Facebook Authentication Failed", Snackbar.LENGTH_SHORT).show();
            }
        });





        skip_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(homeScreenIntent);
                finish();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean validateLogin() {
        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = true;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError("Invalid Password");
            focusView = passwordView;
            cancel = false;
        } else if (TextUtils.isEmpty(email)) {
            emailView.setError("Field Required");
            focusView = emailView;
            cancel = false;
        } else if (!isEmailValid(email)) {
            emailView.setError("Invalid Email");
            focusView = emailView;
            cancel = false;
        }
        if (focusView != null) {
            focusView.requestFocus();
        }
        return cancel;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 8;
    }
}
