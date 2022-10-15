package com.example.easyscootersapp.ui;

import android.content.Intent;

import com.example.easyscootersapp.data.DataRepository;
import com.example.easyscootersapp.data.ErrorCode;
import com.example.easyscootersapp.data.EsError;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenter {
    LoginActivity view;

    CompositeDisposable disposables;
    DataRepository dataRepository;

    LoginPresenter(LoginActivity view) {
        this.view = view;
        this.dataRepository = DataRepository.getInstance();
        this.disposables = new CompositeDisposable();
    }

    public void start() {


    }

    public void onLoginRequest(String email, String password) {
        view.showLoading(true);

        disposables.add(dataRepository
                .login(email, password)
                .delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    view.showLoading(false);
                    view.startActivity(new Intent(view, CommentsActivity.class));
                    view.finish();

                }, error -> {
                    if (error instanceof EsError
                            && ((EsError) error).code
                            == ErrorCode.userNotFound)
                        view.showMessage("utilisateur non trouvé");
                    else if (error instanceof EsError
                            && ((EsError) error).code
                            == ErrorCode.wrongPassword)
                        view.showMessage("Les informations d'identification invalides");
                    else
                        view.showMessage("Une erreur est survenue : " + error.getMessage());
                    view.showLoading(false);
                })
        );
    }

    public void stop() {
        view = null;
        disposables.dispose();
        dataRepository = null;
    }

}