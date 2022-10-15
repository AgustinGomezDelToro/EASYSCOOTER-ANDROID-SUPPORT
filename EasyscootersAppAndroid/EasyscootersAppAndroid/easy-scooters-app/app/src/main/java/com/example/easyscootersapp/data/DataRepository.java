package com.example.easyscootersapp.data;


import android.util.Log;

import com.example.easyscootersapp.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class DataRepository {
    private static final String URL = "jdbc:mysql://145.14.152.151:3306/u453178966_easy_scooter";
    private static final String USER = "u453178966_easy_scooter_u";
    private static final String PASS = "m]AK4ACdc8";

    private static DataRepository instance;

    private BehaviorSubject<User> userBehaviorSubject;
    private User user;
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    private DataRepository() {

    }

    public static DataRepository getInstance() {
        if (instance == null)
            instance = new DataRepository();
        return instance;
    }

    public Observable<User> login(String email, String password) {
        if (user == null) {
            userBehaviorSubject = BehaviorSubject.create();
            subscriptions.add(Observable.fromCallable(() -> {

                                Log.d("Agustin", "This should never be printed!");
                                Map<String, String> userData = new HashMap<>();

                                try {
                                    Connection connection = DriverManager.getConnection(URL, USER, PASS);
                                    String sql = "SELECT idUser, firstname, lastname, CAST(AES_DECRYPT(passwd, 'AIzaSyA-8z3Sutga4WGbP183_mxmgFaT399y9oI') AS CHAR) as passwd FROM users WHERE email = '" + email + "'";
                                    //String sql = "SELECT idUser, AES_DECRYPT(passwd, 'AIzaSyA-8z3Sutga4WGbP183_mxmgFaT399y9oI') as passwd, firstname FROM users WHERE email = '" + email + "'";
                                    PreparedStatement statement = connection.prepareStatement(sql);
                                    ResultSet resultSet = statement.executeQuery();
                                    if (resultSet.next()) {
                                        userData.put("passwd", resultSet.getString("passwd"));
                                        userData.put("firstname", resultSet.getString("firstname"));
                                        userData.put("lastname", resultSet.getString("lastname"));

                                        userData.put("idUser", resultSet.getString("idUser"));
                                    }
                                } catch (Exception e) {
                                    Log.e("Agustin", "Error reading user information", e);
                                    throw e;
                                }

                                if (userData.containsKey("passwd") && Objects.equals(userData.get("passwd"), password))
                                    return new User(userData.get("idUser"),
                                            userData.get("firstname"),
                                            userData.get("lastname"),
                                            userData.get("password")
                                    );
                                else {
                                    EsError error;
                                    if (!Objects.equals(userData.get("passwd"), password))
                                        error =
                                                new EsError(ErrorCode.wrongPassword);
                                    else
                                        error = new EsError(
                                                ErrorCode.userNotFound);
                                    userBehaviorSubject.onError(error);
                                    user = null;
                                    throw error;
                                }

                            })
                            .onErrorComplete()
                            .subscribe(userBehaviorSubject::onNext)

            );
        }
        return userBehaviorSubject;
    }

    public Observable<Boolean> isSignedIn() {
        return Observable.just(userBehaviorSubject != null && userBehaviorSubject.hasValue());
    }

    public Observable<User> getSignedInUser() {
        return userBehaviorSubject;
    }

    public Observable<Boolean> signOut() {
        userBehaviorSubject = null;
        user = null;
        return Observable.just(true);
    }

    public Observable<List<Comment>> getComments() {
        return getSignedInUser().flatMap(user -> Observable.fromCallable(() -> {
            List<Comment> comments
                    = new ArrayList<>();
            try {
                Connection connection = DriverManager.getConnection(URL, USER, PASS);
                String sql = "SELECT id, comments.idScooter as idScooter, comments.idUser, comment, images, firstname, lastname FROM comments left join scooters on comments.idScooter = scooters.idScooter left join users on comments.idUser = users.idUser;";
                //String sql = "SELECT idUser, AES_DECRYPT(passwd, 'AIzaSyA-8z3Sutga4WGbP183_mxmgFaT399y9oI') as passwd, firstname FROM users WHERE email = '" + email + "'";
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    comments.add(new Comment(
                            new User(resultSet.getString("idUser"),
                                    resultSet.getString("firstname"),
                                    resultSet.getString("lastname"),
                                    "null"
                            ),
                            resultSet.getString("idScooter"),
                            resultSet.getString("comment"),
                            resultSet.getString("images")));

                }
            } catch (Exception e) {
                Log.e("Agustin", "Error reading user information", e);
                throw e;
            }
            return comments;
        }));
    }

    public Observable<Boolean> sendComment(User user, String scooterId, String comment) {
        return Observable.fromCallable(() -> {
            List<Comment> comments
                    = new ArrayList<>();
            try {
                Connection connection = DriverManager.getConnection(URL, USER, PASS);
                String sql = "insert into comments (id, `comment`, `idScooter`, `idUser`) values (null, \"${comment}\", ${idScooter}, ${idUser});";
                sql = sql.replace("${comment}", comment);
                sql = sql.replace("${idScooter}", scooterId);
                sql = sql.replace("${idUser}", user.id);
                PreparedStatement statement = connection.prepareStatement(sql);
                int result = statement.executeUpdate();
                Log.d("Agustin", "insert comment returns " + result);
            } catch (Exception e) {
                Log.e("Agustin", "Error reading user information", e);
                throw e;
            }
            return true;
        }).map(sent -> {
            Properties props = new Properties();

            props.put("mail.smtp.host", "smtp.hostinger.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            //Utilisateur d'envoie
            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        //Authenticating the password
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(Utils.EMAIL, Utils.PASSWORD);
                        }
                    });

            try {
                //Creatio de l'objet du message
                MimeMessage mm = new MimeMessage(session);
                String message = "Trotinnette numéro " + scooterId.toString() + "\n \n" + "Description du problème: " + " \n \n" + comment;//corp du mail
                String subject = "Support : " + user.id.toString() + " - " + user.firstname + " " + user.lastname; // Objet du mail

                //Expediteur
                mm.setFrom(new InternetAddress(Utils.EMAIL));
                //Destinataire
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress("support-scooter@agustingomezdeltoro.tech"));
                //Objet
                mm.setSubject(subject);
                //ajout du message
                mm.setText(message);
                //envoie du mail
                Transport.send(mm);


            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return true;
        });

    }
}
