package com.example.mycontactsapplication.Models;

import java.util.Comparator;

public class ContactModel {


   public String id, firstname,lastname,email,phonenumber,gender,userimage;
    int isclose;


    public ContactModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContactModel(String id, String firstname, String lastname, String email, String phonenumber, String gender, String userimage, int isclose) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phonenumber = phonenumber;
        this.gender = gender;
        this.userimage = userimage;
        this.isclose = isclose;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getIsclose() {
        return isclose;
    }

    public void setIsclose(int isclose) {
        this.isclose = isclose;
    }




    public static Comparator<ContactModel> ContactCompare = new Comparator<ContactModel>() {

        public int compare(ContactModel s1, ContactModel s2) {
            String ContactName1 = s1.getFirstname().toUpperCase();
            String ContactName2 = s2.getFirstname().toUpperCase();

            //ascending order
            return ContactName1.compareTo(ContactName2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};

    @Override
    public String toString() {
        return "[ id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", email=" + email + ", phonenumber=" + phonenumber + ", gender=" + gender.toString() + ", userimage=" + userimage + ", iscloase=" + isclose + "]";
    }
}
