package com.example.littleync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.littleync.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

import java.util.TreeMap;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LOGINACTIVTIY";
    public static FirebaseAuth userInstance = FirebaseAuth.getInstance();
    public static boolean loginStatus = false;
    public static int logoutTrigger = 0;
    public static double longitude;
    public static double latitude;
    public static LocationCallback lCB;
    private EditText emailLogin;
    private EditText passwordLogin;
    private Button loginButton;
    public static String UID = null;
    private Boolean _b = true;
    static User loggedInUser;
    public static FusedLocationProviderClient fLC;
    LocationRequest lR;
    public static TreeMap<String, Boolean> whereAmINowMap = new TreeMap<String, Boolean>();
    public static TreeMap<String, Boolean> whereWasIMap = new TreeMap<String, Boolean>();


    @Nullable
    @Override
    public ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setSubtitle(String.format("Please sign in or sign up below."));
        whereAmINowMap.put("Cendana", false);
        whereAmINowMap.put("Elm", false);
        whereAmINowMap.put("Saga", false);
        whereAmINowMap.put("Ecopond", false);
        whereAmINowMap.put("Armory", false);
        whereAmINowMap.put("initialized", true);
        whereWasIMap.putAll(whereAmINowMap);
        //Set the flag of initialized to true.
        whereWasIMap.put("initialized", false);

        //This listener will be activated upon successful sign in. We have to trigger the location receiver here and the navigation to next page from here.
        userInstance.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                try {
                    if ((firebaseAuth.getCurrentUser() != null) && (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                            (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (!firebaseAuth.getCurrentUser().getDisplayName().isEmpty())) {
                        Log.d(TAG, "Logged in, permission granted, display name not null.");
                        generateLocationReceiverAndNavigateToTravelPage();
                    } else if ((firebaseAuth.getCurrentUser() != null) && (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                            (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (firebaseAuth.getCurrentUser().getDisplayName().isEmpty())) {
                        Log.d(TAG, "Logged in, permission granted, display name null.");
                        userInstance.signOut();
                        return;
                    } else if ((firebaseAuth.getCurrentUser() == null) && (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                            (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (firebaseAuth.getCurrentUser().getDisplayName().isEmpty())) {
                        Log.d(TAG, "Not logged in, permission granted, display name null.");
                        userInstance.signOut();
                        return;
                    } else if ((firebaseAuth.getCurrentUser() != null) && (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) &&
                            (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) && (firebaseAuth.getCurrentUser().getDisplayName().isEmpty())) {
                        Log.d(TAG, "Logged in, permission not granted, display name null.");
                        userInstance.signOut();
                        return;
                    } else if ((firebaseAuth.getCurrentUser() == null) && (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) &&
                            (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) && (firebaseAuth.getCurrentUser().getDisplayName().isEmpty())) {
                        Log.d(TAG, "Not logged in, permission not granted, display name null.");
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getStackTrace().toString());
                }

            }

        });

        UID = null;

        setContentView(R.layout.login_page);
        loginButton = findViewById(R.id.login_btn);
        emailLogin = findViewById(R.id.input_email);
        passwordLogin = findViewById(R.id.input_password);

    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.print("onstart");

    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.print("onResume");

    }


    @Override
    protected void onPause() {
        super.onPause();
        System.out.print("onPause");

    }

    @Override
    public void onStop() {
        super.onStop();
        userInstance.removeAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.print("onRestart");

    }

    public void signIn() {
        try {

            userInstance.signInWithEmailAndPassword(emailLogin.getText().toString(), passwordLogin.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        /**
                         * Creates a user object
                         */
                        final String user = userInstance.getUid();
                        System.out.println();
                        Log.d("Login results:", "successfully signed in!");
                        Log.d("UID is", String.format(user));
                        UID = user;
                        loginStatus = true;
                        //else {
                    } else {
                        loginButton.clearFocus();
                        Log.d("Login results:", "nope! Didn't sign in!");
                        Log.d("Exception", task.getException().toString());
                        emailLogin.setError(task.getException().getMessage());
                        passwordLogin.setError(task.getException().getMessage());
                        loginButton.setEnabled(true);
                    }

                }

            });
            /**
             * Assuming the user has logged in before, and would like to go in again.
             */

        } catch (Exception e) {
            loginButton.clearFocus();
            System.out.println(e);
            emailLogin.setError(e.getMessage());
            passwordLogin.setError(e.getMessage());
            loginButton.setEnabled(true);
        }

    }

    @SuppressLint("MissingPermission")
    public void generateLocationReceiverAndNavigateToTravelPage() {

        /**
         * Setup a LocationRequest to be passed into the Activity Compat's request location method.
         */
        lR = LocationRequest.create();
        lR.setInterval(750);
        lR.setFastestInterval(500);
        lR.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /** Setup a LocationCallback to be passed into the Activity Compat's request location method.
         *
         */
        lCB = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onLocationResult(LocationResult lResult) {
                if (lResult != null) {
                    //compute location works on whereAmINowMap
                    computeLocations(lResult.getLastLocation());

                    if (!whereWasIMap.equals(whereAmINowMap)) {
                        logoutTrigger = 0;
                        /**
                         * Synchronize both whereWasIMap and whereAmINowMap after the if both maps are different statement is started.
                         */
                        synchronizeLocations(whereWasIMap, whereAmINowMap);

                        /**
                         * Gets the application context and creates a new intent for a new travel page if the person's GPS coordinates has changed enough.
                         */
                        Intent refresh = new Intent(LoginActivity.super.getApplicationContext(), TravelActivity.class);
                        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(refresh);

                    }

                    Log.d("Longitude", String.valueOf(lResult.getLastLocation().getLongitude()));
                    longitude = Double.valueOf(lResult.getLastLocation().getLongitude());
                    Log.d("Latitude", String.valueOf(lResult.getLastLocation().getLatitude()));
                    latitude = Double.valueOf(lResult.getLastLocation().getLatitude());


                } else {
                    System.out.println("Location result is null");

                }
            }

        };

        /**
         * Initialize a fused Location Provider client with the location requests and it will start executing.
         */
        fLC = LocationServices.getFusedLocationProviderClient(LoginActivity.this);
        fLC.requestLocationUpdates(lR, lCB, null);
        loginButton.setEnabled(true);
        loginStatus = true;
        userInstance.removeAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        });
        Intent refresh = new Intent(LoginActivity.super.getApplicationContext(), TravelActivity.class);
        startActivity(refresh);
    }


    //This is what happens when the user gives permission to access location for the first time.
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && ((grantResults[1] == PackageManager.PERMISSION_GRANTED) && (emailLogin.getText().toString() != null))) {
            signIn();
        } else {
            loginButton.setEnabled(true);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * SynchronizeLocations updates whereAmINowMap with whereWasIMap and ensures that both the maps are identical.
     *
     * @param _whereWasI
     * @param _whereAmI
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void synchronizeLocations(TreeMap<String, Boolean> _whereWasI, TreeMap<String, Boolean> _whereAmI) {

        for (String now : whereAmINowMap.keySet()) {
            if (!_whereAmI.get(now).equals(_whereWasI.get(now))) {
                //Updates the _where was I with what is currently the case.
                whereWasIMap.put(now, _whereAmI.get(now));
            }
        }
    }

    /**
     * ComputeLocations function updates the whereAmINowMap with the latest location of the user, based on the Location object returned from fusedLocationProviderClient.
     * whereAmINowMap can only have 1 true value at any one time.
     *
     * @param _loc
     */
    private void computeLocations(Location _loc) {
        double CENDANA_LAT = 1.3079833;
        double CENDANA_LON = 103.7725314;
        double CENDANA_LAT2 = 1.3079759;
        double CENDANA_LON2 = 103.7721854;
        double ELM_LAT = 1.3062946;
        double ELM_LON = 103.7723276;
        double SAGA_LAT = 1.3057288;
        double SAGA_LON = 103.7720902;
        double POND_LAT = 1.3068497;
        double POND_LON = 103.7721358;
        double DOS_LAT = 1.3072026;
        double DOS_LON = 103.7726464;
        double offset = 0.0003;


        /**
         * Find Cendana
         */
        if (((_loc.getLatitude() >= (CENDANA_LAT2 - offset)) && ((_loc.getLatitude() <= (CENDANA_LAT2 + offset)) && (_loc.getLongitude() >= (CENDANA_LON2 - offset)) && (_loc.getLongitude() <= (CENDANA_LON2 + offset)))) || ((_loc.getLatitude() >= (CENDANA_LAT - offset)) && ((_loc.getLatitude() <= (CENDANA_LAT + offset)) && (_loc.getLongitude() >= (CENDANA_LON - offset)) && (_loc.getLongitude() <= (CENDANA_LON + offset))))) {
            whereAmINowMap.put("Cendana", true);
            whereAmINowMap.put("Elm", false);
            whereAmINowMap.put("Saga", false);
            whereAmINowMap.put("Ecopond", false);
            whereAmINowMap.put("Armory", false);
        }

        /**
         * Find Elm
         */
        else if ((_loc.getLatitude() >= (ELM_LAT - offset)) && ((_loc.getLatitude() <= (ELM_LAT + offset)) && (_loc.getLongitude() >= (ELM_LON - offset)) && (_loc.getLongitude() <= (ELM_LON + offset)))) {

            whereAmINowMap.put("Cendana", false);
            whereAmINowMap.put("Elm", true);
            whereAmINowMap.put("Saga", false);
            whereAmINowMap.put("Ecopond", false);
            whereAmINowMap.put("Armory", false);
            //whereAmINowMap.put("Elm", !whereAmINowMap.get("Elm"));
        }
        /**
         * Find Saga
         */
        else if ((_loc.getLatitude() >= (SAGA_LAT - offset)) && ((_loc.getLatitude() <= (SAGA_LAT + offset)) && (_loc.getLongitude() >= (SAGA_LON - offset)) && (_loc.getLongitude() <= (SAGA_LON + offset)))) {
            //whereAmINowMap.put("Saga", !whereAmINowMap.get("Saga"));

            whereAmINowMap.put("Cendana", false);
            whereAmINowMap.put("Elm", false);
            whereAmINowMap.put("Saga", true);
            whereAmINowMap.put("Ecopond", false);
            whereAmINowMap.put("Armory", false);
        }

        /**
         * Find Ecopond
         */
        else if ((_loc.getLatitude() >= (POND_LAT - offset)) && ((_loc.getLatitude() <= (POND_LAT + offset)) && (_loc.getLongitude() >= (POND_LON - offset)) && (_loc.getLongitude() <= (POND_LON + offset)))) {

            whereAmINowMap.put("Cendana", false);
            whereAmINowMap.put("Elm", false);
            whereAmINowMap.put("Saga", false);
            whereAmINowMap.put("Ecopond", true);
            whereAmINowMap.put("Armory", false);

        }

        /**
         * Find Armory
         */
        else if ((_loc.getLatitude() >= (DOS_LAT - offset)) && ((_loc.getLatitude() <= (DOS_LAT + offset)) && (_loc.getLongitude() >= (DOS_LON - offset)) && (_loc.getLongitude() <= (DOS_LON + offset)))) {

            whereAmINowMap.put("Cendana", false);
            whereAmINowMap.put("Elm", false);
            whereAmINowMap.put("Saga", false);
            whereAmINowMap.put("Ecopond", false);
            whereAmINowMap.put("Armory", true);
        } else {
            whereAmINowMap.put("Cendana", false);
            whereAmINowMap.put("Elm", false);
            whereAmINowMap.put("Saga", false);
            whereAmINowMap.put("Ecopond", false);
            whereAmINowMap.put("Armory", false);
        }


    }


    /**
     * Called when the user taps the Login button
     *
     * @param view
     */
    public void loginButton(View view) {
        loginButton.setEnabled(false);

        /**
         * Setup the location requests
         */
        //Checks if the user has location services provided, and to give it if not.
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            signIn();
        }


    }

    /**
     * forgotPassword()
     *
     * @param view
     * @return new screen for forgot password
     */
    public void forgotPasswordScreen(View view) {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    // Called when the user presses the sign-up text.
    public void signUp(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

}