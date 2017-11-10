package com.example.owner.dialoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Screen to display user profile information
 */
public class UserProfileScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    private DatabaseReference mDatabase;
    private String firstName;
    private String lastName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_profile_toolbar);
        toolbar.setTitle("Account Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName = "";
        lastName = "";

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = "";
        if (currentUser != null) {
            userId = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            getUserProfileInformation();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.user_profile_edit) {
            Toast.makeText(UserProfileScreen.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getUserProfileInformation() {
        getFirstLastName();
        getAge();
        getHeight();
        getWeight();
    }

    public void getFirstLastName() {
        DatabaseReference ref = mDatabase.child("/users/" + currentUser.getUid() + "/first-last-name");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                System.out.println("User First Name: " + name);
                firstName =  name;
                TextView userProfileName = (TextView) findViewById(R.id.user_profile_name);
                userProfileName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                firstName = "";
            }
        });
    }

    public void getAge() {
        DatabaseReference ref = mDatabase.child("/users/" + currentUser.getUid() + "/age");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String age = dataSnapshot.getValue(String.class);
                System.out.println("User Age: " + age);
                String ageText = "Age: " + age;
                TextView userProfileAge = (TextView) findViewById(R.id.user_profile_age);
                userProfileAge.setText(ageText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void getHeight() {
        DatabaseReference ref = mDatabase.child("/users/" + currentUser.getUid() + "/height");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String height = dataSnapshot.getValue(String.class);
                System.out.println("User Height: " + height);
                String heightText = "Height: " + height + "\"";
                TextView userProfileAge = (TextView) findViewById(R.id.user_profile_height);
                userProfileAge.setText(heightText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void getWeight() {
        DatabaseReference ref = mDatabase.child("/users/" + currentUser.getUid() + "/weight");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String weight = dataSnapshot.getValue(String.class);
                System.out.println("User Weight: " + weight);
                String weightText = "Weight: " + weight + " lbs";
                TextView userProfileAge = (TextView) findViewById(R.id.user_profile_weight);
                userProfileAge.setText(weightText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                firstName = "";
            }
        });
    }
}
