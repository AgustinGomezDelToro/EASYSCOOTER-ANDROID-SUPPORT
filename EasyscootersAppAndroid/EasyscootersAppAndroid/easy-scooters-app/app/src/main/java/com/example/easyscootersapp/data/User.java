package com.example.easyscootersapp.data;

import java.io.Serializable;

public class User implements Serializable {
    public final String id;
    public final String firstname;
    public final String lastname;
    public final String password;

    public User(String id, String firstname, String lastname, String password) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
    }
}
