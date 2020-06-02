package com.example.mycontactsapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.mycontactsapplication.Common.Common;
import com.example.mycontactsapplication.Models.ContactModel;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    Context context;
   private static String database = "mycontactapp.db";
   private static String table = "contact";
   private static String id = "id";
   private static String firstname = "first_name";
   private static String lastname = "last_name";
   private static String gender = "gender";
   private static String phonenumber = "phone";
   private static String email = "email";
   private static String cloasefriend = "isclosefriend";
   private static String userimage = "user_image";

    public Database(Context context) {
        super(context,database,null,1);
        this.context = context;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists "+table);
        onCreate(db);
    }

    public void savecontact(ContactModel contactModel){




        // open database
        SQLiteDatabase database1 = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        // put values
        contentValues.put(firstname,contactModel.getFirstname());
        contentValues.put(lastname,contactModel.getLastname());
        contentValues.put(phonenumber,contactModel.getPhonenumber());
        contentValues.put(email,contactModel.getEmail());
        contentValues.put(gender,contactModel.getGender());
        contentValues.put(cloasefriend,contactModel.getIsclose());
        contentValues.put(userimage,"https://freesvg.org/img/abstract-user-flat-3.png");

        // insert data
        database1.insert(table,null,contentValues);

        database1.close();
    }


    public void updateContact(ContactModel contactModel){

        // open database
        SQLiteDatabase database1 = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        // put values
        contentValues.put(firstname,contactModel.getFirstname());
        contentValues.put(lastname,contactModel.getLastname());
        contentValues.put(phonenumber,contactModel.getPhonenumber());
        contentValues.put(email,contactModel.getEmail());
        contentValues.put(gender,contactModel.getGender());
        contentValues.put(cloasefriend,contactModel.getIsclose());
        contentValues.put(userimage,"https://freesvg.org/img/abstract-user-flat-3.png");

        // insert data
        database1.update(table,contentValues,id + " = ?",new String[] {Common.currentContact.getId()});

        database1.close();
    }

    public List<ContactModel> getAllContact(){

        SQLiteDatabase database1 = this.getWritableDatabase();

        List<ContactModel> list = new ArrayList<>();


        Cursor cursor = database1.rawQuery("select * from "+table,null);

        if (cursor.moveToFirst()){

            do {
                ContactModel model = new ContactModel();

                model.setId(cursor.getString(0));
                model.setFirstname(cursor.getString(1));
                model.setLastname(cursor.getString(2));
                model.setGender(cursor.getString(3));
                model.setPhonenumber(cursor.getString(4));
                model.setEmail(cursor.getString(5));
                model.setUserimage(cursor.getString(7));
                model.setIsclose(cursor.getInt(6));

                list.add(model);

            }while (cursor.moveToNext());



        }else {

            Toast.makeText(context, "No Contact In Database", Toast.LENGTH_SHORT).show();

        }


        return list;
    }

    public void delete(String ids){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete(table,id + " = ?",new String[] {ids});

        sqLiteDatabase.close();

    }

}
