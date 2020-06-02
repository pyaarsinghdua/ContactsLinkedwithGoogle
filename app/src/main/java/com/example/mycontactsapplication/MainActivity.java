package com.example.mycontactsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mycontactsapplication.Common.Common;
import com.example.mycontactsapplication.Models.ContactModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView image;
    Button save_update;
    EditText firstname, lastname, email, phone;
    RadioButton rmale, rfemale, rother;
    TextView topheading;
    Switch isclose;
    Intent intent;

    String userchosen;
    Uri imageuri;

    Database database;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef, usersrefer;

    ProgressDialog progressDialog;

    int camerarequest, galleryrequest;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    GoogleApiClient googleApiClient;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("my_contacts");
        usersrefer = firebaseDatabase.getReference("users_id");

        // database = new Database(this);

        progressDialog = new ProgressDialog(this);


        intent = getIntent();

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
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();


        topheading = findViewById(R.id.topheading);
        image = findViewById(R.id.contactimage);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        rmale = findViewById(R.id.rmale);
        rfemale = findViewById(R.id.rfemale);
        rother = findViewById(R.id.rothers);
        phone = findViewById(R.id.phonenumber);
        email = findViewById(R.id.email);
        isclose = findViewById(R.id.closefriend);
        save_update = findViewById(R.id.bsaveupdate);
        linearLayout = findViewById(R.id.parentlayout);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(MainActivity.this);
            }
        });


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("userimages/");

        if (Common.isUpdate) {
            save_update.setText("Update");
            topheading.setText("EDIT");

            firstname.setText(Common.currentContact.getFirstname());
            lastname.setText(Common.currentContact.getLastname());
            phone.setText(Common.currentContact.getPhonenumber());
            email.setText(Common.currentContact.getEmail());
            Glide.with(this).load(Common.currentContact.getUserimage()).into(image);
            if (Common.currentContact.getIsclose() == 1)
                isclose.setChecked(true);
            else
                isclose.setChecked(false);

        }


        save_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstname.getText().toString().isEmpty()) {
                    firstname.setError("First Name is missing");
                }
                if (phone.length() < 10) {
                    phone.setError("Please Check Phone Number");
                }

                if (!firstname.getText().toString().isEmpty()) {
                    if (phone.length() > 9) {
                        savetodatabase();
                    }
                }
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camerarequest = 101;
                galleryrequest = 102;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose Image From...");
                CharSequence[] items = {"Camera", "Gallery", "Default"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            userchosen = "Camera";
                            permissioncamera();
                        } else if (which == 1) {
                            userchosen = "Gallery";
                            permissiongallery();
                        } else {
                            userchosen = "Default";
                            Glide.with(MainActivity.this).load(R.drawable.ic_account_circle_blue_24dp).into(image);
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });


    }

    private void permissiongallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            opengallery();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, galleryrequest);
        }
    }

    private void opengallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, galleryrequest);
    }

    private void permissioncamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            opencamera();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 9);
        }
    }

    private void opencamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, camerarequest);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == camerarequest) {
                opencamera();
            } else if (requestCode == galleryrequest) {
                opengallery();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission Denied");
            builder.setCancelable(true);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageuri = getimageuri(MainActivity.this, photo);
            Glide.with(MainActivity.this).load(imageuri).into(image);
        } else if (requestCode == 102) {
            Log.i("result code", String.valueOf(requestCode));
            imageuri = data.getData();
            Glide.with(MainActivity.this).load(imageuri).into(image);
        } else {

        }
    }

    private Uri getimageuri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void savetodatabase() {


        progressDialog.show();

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving Contact...");

        final ContactModel contactModel = new ContactModel();


        contactModel.setFirstname(firstname.getText().toString());
        contactModel.setLastname(lastname.getText().toString());
        contactModel.setPhonenumber(phone.getText().toString());
        contactModel.setEmail(email.getText().toString());
        if (rmale.isChecked()) {

            contactModel.setGender("Male");

        } else if (rfemale.isChecked()) {

            contactModel.setGender("Female");
        } else {
            contactModel.setGender("Other");
        }

        if (isclose.isChecked()) {
            contactModel.setIsclose(1);
        } else {
            contactModel.setIsclose(0);
        }


        if (Common.isUpdate) {
            // database.updateContact(contactModel);
            contactModel.setId(Common.currentContact.getId());
            Common.isUpdate = false;
        } else {
            // database.savecontact(contactModel);
            contactModel.setId(String.valueOf(System.currentTimeMillis()));
        }

        if (imageuri != null) {
            storageReference.child(contactModel.getId()).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    storageReference.child(contactModel.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            contactModel.setUserimage(uri.toString());
                            myRef.child(contactModel.getId()).setValue(contactModel);

                            progressDialog.hide();
                            showSuccessDilog();
                        }

                    });


                }
            });
        } else {
            progressDialog.hide();
            contactModel.setUserimage("https://freesvg.org/img/abstract-user-flat-3.png");
            myRef.child(contactModel.getId()).setValue(contactModel);
            showSuccessDilog();
        }


    }

    private void getusersid() {

        usersrefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        String token = dataSnapshot1.getValue().toString();
                        sendnotification(token, Common.currentUser.getName() + " Added New Contact", firstname.getText().toString() + " " + lastname.getText().toString());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void showSuccessDilog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("New contact saved successfully !");



        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.show();
        gotocontacts();
        getusersid();

    }


    public void gotocontacts(){
        Intent intent1 = new Intent(MainActivity.this,ListContacts.class);
        startActivity(intent1);

    }




    private void sendnotification(String deviceId, String title, String message) {
        JSONObject top = new JSONObject();
        JSONObject middle = new JSONObject();
        JSONObject bottom = new JSONObject();

        try {
            middle.put("title",title);
            middle.put("body",message);
            top.put("to",deviceId);
            top.put("notification",middle);

        }catch (Exception e){
            Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", top, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("NotificationRes",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Volley Error:" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","key=AIzaSyBzXnvtULBUh1sRCtyEM-wBPrpE3eyMQ-0");
                map.put("Content-Type","application/json");
                return map;
            }
        };

        RequestQueue queue  = Volley.newRequestQueue(this);
        queue.add(request);

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }




    private void logout() {
        // Toast.makeText(this, "Logout Function", Toast.LENGTH_SHORT).show();

        Auth.GoogleSignInApi.signOut(googleApiClient);
        startActivity(new Intent(this,Login.class));
        finish();
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout,menu);
        return true;
    }
}
