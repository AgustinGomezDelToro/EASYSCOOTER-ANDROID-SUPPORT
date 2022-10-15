package com.example.easyscootersapp.ui;

import android.widget.Toast;

import com.example.easyscootersapp.data.DataRepository;
import com.example.easyscootersapp.data.User;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NewCommentPresenter {
    NewCommentDialogFragment view;

    CompositeDisposable disposables;
    DataRepository dataRepository;

    NewCommentPresenter(NewCommentDialogFragment view) {
        this.view = view;
        this.dataRepository = DataRepository.getInstance();
        this.disposables = new CompositeDisposable();
    }

    public void start() {


    }

    public void onSendRequest(User user, String scooterId, String comment) {
        view.showLoading(true);

        disposables.add(dataRepository
                .sendComment(user, scooterId, comment)
                .delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sent -> {
                    view.showLoading(false);
                    //view.startActivity(new Intent(view, CommentsActivity.class));
                    //view.finish();
                    Toast.makeText(
                            view.getActivity(), "Tout est bon !",
                            Toast.LENGTH_SHORT).show();
                    view.dismiss();
                }, error -> {
                    Toast.makeText(view.getActivity(), "Une erreur est survenue : " + error.getMessage(), Toast.LENGTH_SHORT).show();
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