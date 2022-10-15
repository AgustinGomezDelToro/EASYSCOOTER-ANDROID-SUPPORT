package com.example.easyscootersapp.ui;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.easyscootersapp.R;

public class BaseActivity extends FragmentActivity {
    ProgressBar progressBar;

    protected void findAndInitViews() {

        progressBar = findViewById(R.id.progress);
    }

    public void showLoading(boolean loading) {
        progressBar.setVisibility(loading? View.VISIBLE: View.GONE);
    }

    public void showMessage(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }
}
