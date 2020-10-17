package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
    }

    /** Called when the user taps the Login button */
    public void loginButton(View view) {
        Intent intent = new Intent(this, TravelActivity.class);
        startActivity(intent);

        
    }

    // Called when the user presses the sign-up text.
    public void signUp(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void goToRandomPage(View view){
        Intent intent = new Intent(this, RandomPage.class);
        startActivity(intent);
    }

}