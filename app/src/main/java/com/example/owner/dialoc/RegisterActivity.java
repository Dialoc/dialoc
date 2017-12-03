package com.example.owner.dialoc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    private TextInputEditText emailView;
    private TextInputEditText passwordView;
    private EditText firstnView;
    private TextInputEditText lastnView;
    private TextInputEditText ageView;
    private TextInputEditText heightView;
    private TextInputEditText weightView;

    private Intent loginScreenIntent;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailView = (TextInputEditText) findViewById(R.id.email);
        passwordView = (TextInputEditText) findViewById(R.id.password);
        firstnView = findViewById(R.id.first_name);
        lastnView = (TextInputEditText) findViewById(R.id.last_name);
        ageView = (TextInputEditText) findViewById(R.id.age);
        heightView = (TextInputEditText) findViewById(R.id.height);
        weightView = (TextInputEditText) findViewById(R.id.weight);

        loginScreenIntent = new Intent(this, LoginScreen.class);
        registerButton = (Button) findViewById(R.id.register_button);
        mAuth = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (validateRegistration()) {
                    String email = emailView.getText().toString();
                    String password = passwordView.getText().toString();
                    OnCompleteListener<AuthResult> regListener = new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                storeUserInfo();
                                startActivity(loginScreenIntent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Snackbar.make(view, "Registration Failed",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        }

                    };
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, regListener);
                }
            }
        });
    }

    private void storeUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("/users/" + user.getUid() + "/first-name").setValue(firstnView.getText().toString());
        mDatabase.child("/users/" + user.getUid() + "/last-name").setValue(lastnView.getText().toString());
        String fullName = firstnView.getText().toString() + " " + lastnView.getText().toString();
        mDatabase.child("/users/" + user.getUid() + "/first-last-name").setValue(fullName);
        mDatabase.child("/users/" + user.getUid() + "/age").setValue(ageView.getText().toString());
        mDatabase.child("/users/" + user.getUid() + "/height").setValue(heightView.getText().toString());
        mDatabase.child("/users/" + user.getUid() + "/weight").setValue(weightView.getText().toString());
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean validateRegistration() {
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
