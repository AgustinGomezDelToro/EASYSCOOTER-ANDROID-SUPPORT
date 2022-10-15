package com.example.easyscootersapp.data;

public class Comment {
    public final User user;
    public final String scooterId;
    public final String text;
    public final String imageUrl;

    public Comment(User user, String scooterId, String text, String imageUrl) {
        this.user = user;
        this.scooterId = scooterId;
        this.text = text;
        this.imageUrl = imageUrl;
    }
}
