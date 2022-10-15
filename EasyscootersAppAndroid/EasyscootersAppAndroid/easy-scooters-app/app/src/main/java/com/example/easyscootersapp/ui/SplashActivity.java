package com.example.easyscootersapp.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.easyscootersapp.R;
import com.example.easyscootersapp.data.DataRepository;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findAndInitViews();

        Observable.just(true).delay(3000, TimeUnit.SECONDS)
                .flatMap(value -> DataRepository.getInstance().isSignedIn())
                .subscribe(isSignedIn -> {
            startActivity(new Intent(this, isSignedIn?CommentsActivity.class:LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void findAndInitViews() {
        super.findAndInitViews();

    }
}