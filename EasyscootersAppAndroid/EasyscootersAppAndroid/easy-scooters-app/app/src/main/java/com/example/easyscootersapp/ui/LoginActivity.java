package com.example.easyscootersapp.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;

import com.example.easyscootersapp.R;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends BaseActivity {

    private Button btn_send;
    private TextInputEditText pseudo,pass;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findAndInitViews();

        if (Build.VERSION.SDK_INT> 9){//faire en sorte que la connexion fonctionne sur tout les appareils et sur toutes les versions
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        presenter = new LoginPresenter(this);
        presenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }

    protected void findAndInitViews() {
        super.findAndInitViews();
        pseudo = findViewById(R.id.pseudo);

        pass = findViewById(R.id.pass);
        pass.setOnClickListener(view -> {

        });
        btn_send = (Button) findViewById(R.id.btn_send);

        btn_send.setOnClickListener(view -> {
            presenter.onLoginRequest(pseudo.getText().toString(), pass.getText().toString());
            // connexion();
        });

    }

}