package com.example.mycontactsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mycontactsapplication.Adapters.ContactListAdapter;
import com.example.mycontactsapplication.Common.Common;
import com.example.mycontactsapplication.Models.ContactModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListContacts extends AppCompatActivity implements deleteFun {

    List<ContactModel> list = new ArrayList<>();
    // Database database;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    FloatingActionButton floatingActionButton;
    Intent intent1;
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference2,reference;
    GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //sign in
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();

        intent1 = getIntent();

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // database = new Database(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference2 = firebaseDatabase.getReference("users_id");


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!Common.testuser){
                    reference2.child(Common.currentUser.getUserId()).setValue(task.getResult().getToken());
                }
            }
        });


        reference = firebaseDatabase.getReference("my_contacts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    ContactModel contactModel = dataSnapshot1.getValue(ContactModel.class);
                    list.add(contactModel);
                }
                Collections.sort(list,ContactModel.ContactCompare);
                adapter = new ContactListAdapter(list,ListContacts.this);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        floatingActionButton = findViewById(R.id.floatingActionButton);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.isUpdate =false;
                Intent intent = new Intent(ListContacts.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    @Override
    public void delete(String id) {
        new Database(this).delete(id);
        list.clear();

        // list = database.getAllContact();

        adapter = new ContactListAdapter(list,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        // recyclerView.scrollToPosition(list.size()-1);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Auth.GoogleSignInApi.signOut(googleApiClient);
        startActivity(new Intent(this,Login.class));
        finish();
    }
}
