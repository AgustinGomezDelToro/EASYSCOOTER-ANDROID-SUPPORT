package com.example.easyscootersapp.ui;

import android.content.Intent;
import android.util.Pair;

import com.example.easyscootersapp.data.Comment;
import com.example.easyscootersapp.data.DataRepository;
import com.example.easyscootersapp.data.User;

import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CommentsPresenter {
    CommentsActivity view;

    CompositeDisposable disposables;
    DataRepository dataRepository;

    CommentsPresenter(CommentsActivity view) {
        this.view = view;
        this.dataRepository = DataRepository.getInstance();
        this.disposables = new CompositeDisposable();
    }

    public void start() {
        getComments();
    }

    public void onRefreshRequest() {
        getComments();
    }

    private void getComments() {
        view.showLoading(true);

        disposables.add(dataRepository
                .getComments()
                .zipWith(dataRepository.getSignedInUser(), Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Pair<List<Comment>, User> commentsAndUser) -> {
                    if (commentsAndUser.first.isEmpty())
                        view.showContentMessage("Bonjour " + commentsAndUser.second.firstname
                                + ", nous avons trouvé " + commentsAndUser.first.size() + " commentaires");
                    else {
                        view.showMessage("Bonjour " + commentsAndUser.second.firstname
                                + ", nous avons trouvé " + commentsAndUser.first.size() + " commentaires");
                        Collections.reverse(commentsAndUser.first);
                        view.showComments(commentsAndUser.second,
                                commentsAndUser.first);
                    }

                    view.showLoading(false);
                }, error -> {
                    view.showContentMessage("Une erreur est survenue : " + error.getMessage());
                    view.showLoading(false);
                })
        );
    }

    public void stop() {
        view = null;
        disposables.dispose();
        dataRepository = null;
    }

    public void onLogoutRequest() {
        disposables.add(dataRepository
                .signOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signedOut -> {
                    view.startActivity(new Intent(view, LoginActivity.class));
                    view.finish();
                }));
    }
}