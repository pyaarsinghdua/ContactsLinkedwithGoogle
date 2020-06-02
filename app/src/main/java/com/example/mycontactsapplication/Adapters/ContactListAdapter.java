package com.example.mycontactsapplication.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycontactsapplication.Common.Common;
import com.example.mycontactsapplication.ListContacts;
import com.example.mycontactsapplication.MainActivity;
import com.example.mycontactsapplication.Models.ContactModel;
import com.example.mycontactsapplication.R;
import com.example.mycontactsapplication.deleteFun;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Permission;
import java.util.List;

import static android.Manifest.*;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    List<ContactModel> list;
    Context context;
    String callphonenumber;


    com.example.mycontactsapplication.deleteFun deleteFun;

    public ContactListAdapter(List<ContactModel> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_view, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_view, parent, false);

        }

        return new ContactViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, final int position) {


        holder.userName.setText(list.get(position).getFirstname() + " " + list.get(position).getLastname());

        holder.userNumber.setText(list.get(position).getPhonenumber());

        Glide.with(context).load(list.get(position).getUserimage()).error(R.drawable.ic_account_circle_blue_24dp).into(holder.userImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callphonenumber = "tel:" + list.get(position).getPhonenumber();
                makephonecall(callphonenumber);
//                if (ContextCompat.checkSelfPermission(context, permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
//                    context.startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(callphonenumber)));
//                }else{
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setMessage("Permission Denied");
//                    builder.setCancelable(false);
//                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    builder.show();
//                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.inflate(R.menu.option);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.edit){
                            Common.isUpdate = true;
                            Common.currentContact = list.get(position);
                           context.startActivity(new Intent(context, MainActivity.class));

                        }else{

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Are sure You want to Delete");
                            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = firebaseDatabase.getReference("my_contacts");
                                    reference.child(list.get(position).getId()).removeValue();

                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }

                        return false;
                    }
                });
                popupMenu.show();

                return false;
            }
        });
    }

    private void makephonecall(String callphonenumber) {
        if (ContextCompat.checkSelfPermission(context, permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            context.startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(callphonenumber)));
        }else{
            ActivityCompat.requestPermissions((Activity) context,new String[] {Manifest.permission.CALL_PHONE},101);
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage("Permission Denied");
//            builder.setCancelable(false);
//            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.show();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 101) {
                makephonecall(callphonenumber);
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Permission Denied");
            builder.setCancelable(false);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }


    class ContactViewHolder extends RecyclerView.ViewHolder{

        ImageView userImg;
        TextView userName,userNumber;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            userImg = itemView.findViewById(R.id.userImg);
            userName = itemView.findViewById(R.id.userName);
            userNumber = itemView.findViewById(R.id.userNumber);
        }
    }
}
