package com.example.easyscootersapp.ui;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyscootersapp.R;
import com.example.easyscootersapp.data.Comment;
import com.example.easyscootersapp.data.User;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> comments;
    private User user;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView user;
        public final TextView scooter;
        public final ShapeableImageView image;
        public final TextView text;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            user = view.findViewById(R.id.user);
            scooter = view.findViewById(R.id.scooter);
            image = view.findViewById(R.id.image);
            text = view.findViewById(R.id.commentText);
        }

    }

    public CommentsAdapter(List<Comment> comments, User user) {
        this.comments = comments;
        this.user = user;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comment_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Comment comment = comments.get(position);
        String imageUrl = comment.imageUrl;
        viewHolder.user.setText(comment.user.firstname + " ");
        viewHolder.scooter.setText("Votre numéro " + comment.scooterId);
        viewHolder.text.setText(comment.text);
        Glide.with(viewHolder.itemView).load(imageUrl)
                .centerCrop()
                //.placeholder(R.drawable.loading_spinner)
                .into(viewHolder.image);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return comments.size();
    }
}
