package com.example.easyscootersapp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyscootersapp.R;
import com.example.easyscootersapp.data.Comment;
import com.example.easyscootersapp.data.DataRepository;
import com.example.easyscootersapp.data.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CommentsActivity extends BaseActivity implements DialogInterface.OnDismissListener {
    private RecyclerView recyclerView;
    private TextView messageTextView;

    private CommentsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        findAndInitViews();
        presenter = new CommentsPresenter(this);
        presenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }

    @Override
    protected void findAndInitViews() {
        super.findAndInitViews();
        recyclerView = findViewById(R.id.recyclerView);
        messageTextView = findViewById(R.id.message);
        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(view -> {
            DataRepository.getInstance().getSignedInUser().subscribe(user -> {
                FragmentManager fm = getSupportFragmentManager();
                NewCommentDialogFragment newCommentDialogFragment = NewCommentDialogFragment.newInstance(user);
                newCommentDialogFragment.show(fm, "new_comment_fragment");

            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.comments_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                onRefreshClicked(null);
                return true;
            }
            case R.id.action_logout: {
                onLogoutClicked(null);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onRefreshClicked(MenuItem item) {
        presenter.onRefreshRequest();
    }

    public void onLogoutClicked(MenuItem item) {
        presenter.onLogoutRequest();
    }

    public void showContentMessage(String message) {
        recyclerView.setVisibility(View.GONE);
        messageTextView.setVisibility(View.VISIBLE);
        messageTextView.setText(message);
    }

    public void showComments(User user, List<Comment> comments) {
        messageTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new CommentsAdapter(comments, user));
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        presenter.onRefreshRequest();
    }
}