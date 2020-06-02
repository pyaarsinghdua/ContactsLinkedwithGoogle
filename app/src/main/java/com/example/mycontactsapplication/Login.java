package com.example.mycontactsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mycontactsapplication.Common.Common;
import com.example.mycontactsapplication.Models.UserModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    SignInButton login;
    GoogleApiClient googleApiClient;
    EditText lpassword,lusername;
    Button privatelogin;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference verifyreference,loginreference;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseDatabase = FirebaseDatabase.getInstance();
        verifyreference = firebaseDatabase.getReference("verified_ids");
        loginreference = firebaseDatabase.getReference("Login Accounts");
        Toast.makeText(this, loginreference.getKey(), Toast.LENGTH_SHORT).show();


        lpassword = findViewById(R.id.lpassword);
        lusername = findViewById(R.id.luser);
        privatelogin = findViewById(R.id.login);



        intent = getIntent();

        login = findViewById(R.id.loginBtn);

        //fetch loged in accounts
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

//        sign in
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();

//        Common.loginscreen = true;

        privatelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.testuser = true;
//                boolean check;
//                check = String.valueOf(lusername.getText()).equals("Family");
//                Toast.makeText(Login.this, String.valueOf(check), Toast.LENGTH_SHORT).show();
//                Log.i("hello",lusername.getText().toString());

                if (String.valueOf(lusername.getText()).equals("Family") && String.valueOf(lpassword.getText()).equals("love123")){
                    Common.currentUser.setName("test");
                    Common.currentUser.setImageUri("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1000&q=80");
                    String randomid = String.valueOf(System.currentTimeMillis());
                    Common.currentUser.setUserId(randomid);
                    gotolistactivity();
                }else if (String.valueOf(lusername.getText()).equals("Family")){
                    lpassword.setError("Please Enter correct Password");
                }else{
                    lpassword.setError("Please Enter correct Password");
                    lusername.setError("Please check the Username");
                    Toast.makeText(Login.this, "Not such an Account", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign();
            }
        });

    }

    private void gotolistactivity() {
        Intent intent = new Intent(Login.this,ListContacts.class);
        startActivity(intent);
        finish();
    }

    private void sign() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, 123);
    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> sign = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if (sign.isDone()){

            GoogleSignInResult signInResult = sign.get();
            chechSignIn(signInResult);
        }
    }




    private void chechSignIn(GoogleSignInResult signInResult) {

        if (signInResult.isSuccess()){
            GoogleSignInAccount account = signInResult.getSignInAccount();

            if (account.getPhotoUrl() == null){
                UserModel userModel = new UserModel(account.getId(),account.getDisplayName(),"https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1000&q=80");
                Common.currentUser = userModel;
            }else{
                UserModel userModel = new UserModel(account.getId(),account.getDisplayName(),account.getPhotoUrl().toString());
                Common.currentUser = userModel;
            }

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference reference = firebaseDatabase.getReference("verified_ids");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean ver = false;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if (dataSnapshot1.getValue().equals(Common.currentUser.getUserId())){
                            ver = true;
                        }
                    }
                    if (ver){
                        gotolistactivity();
                    }else {
                        Auth.GoogleSignInApi.signOut(googleApiClient);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }else {
            Toast.makeText(this, "Sign in Fail", Toast.LENGTH_SHORT).show();
        }

    }






    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123){

            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            chechSignIn(signInResult);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        lpassword = findViewById(R.id.lpassword);
        lusername = findViewById(R.id.luser);

        lpassword.clearComposingText();
        lusername.clearComposingText();

    }
}
