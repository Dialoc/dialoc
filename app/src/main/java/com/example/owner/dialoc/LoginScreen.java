package com.example.owner.dialoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Owner on 4/18/2017.
 */

public class LoginScreen extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

//        Button skipSignUp = (Button) findViewById(R.id.skip_sign_up_button);
//        skipSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginScreen.this, HomeScreenActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
