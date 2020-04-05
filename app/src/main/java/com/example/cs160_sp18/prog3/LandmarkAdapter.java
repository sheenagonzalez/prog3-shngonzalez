package com.example.cs160_sp18.prog3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LandmarkAdapter extends RecyclerView.Adapter {

    private ArrayList<Landmark> landmarks;
    private Context context;
    private Location currLocation;
    private String username;

    public LandmarkAdapter(Context context, ArrayList<Landmark> landmarks, Location currLocation, String username) {
        this.context = context;
        this.landmarks = landmarks;
        this.currLocation = currLocation;
        this.username = username;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.landmark_cell_layout, parent, false);
        return new LandmarkViewHolder(context, view, currLocation, username);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Landmark landmark = landmarks.get(position);
        ((LandmarkViewHolder) holder).bind(landmark);
    }

    @Override
    public int getItemCount() {
        return landmarks.size();
    }
}

class LandmarkViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout linearLayout;
    public TextView landmarkNameTextView;
    public TextView distanceTextView;
    public ImageView landmarkImageView;
    public Context context;
    public double distance;
    public Location currLocation;
    public String username;

    public LandmarkViewHolder(Context context, View itemView, Location currLocation, String username) {
        super(itemView);
        this.context = context;
        this.linearLayout = itemView.findViewById(R.id.linearLayout);
        this.landmarkNameTextView = itemView.findViewById(R.id.landmark_name_text_view);
        this.distanceTextView = itemView.findViewById(R.id.distance_text_view);
        this.landmarkImageView = itemView.findViewById(R.id.landmark_image_view);
        this.currLocation = currLocation;
        this.username = username;
    }

    void bind(Landmark landmark) {
        landmarkNameTextView.setText(landmark.getLandmarkName());
        distance = landmark.getDistance(currLocation);
        distanceTextView.setText(((int)distance) + " meters away");
        int resID = context.getResources().getIdentifier(landmark.getFilename(), "drawable", context.getPackageName());
        landmarkImageView.setImageResource(resID);

        if (distance > 10) {
            linearLayout.setAlpha(0.6f);
            landmarkNameTextView.setTextColor(Color.GRAY);
        } else {
            linearLayout.setAlpha(1f);
            landmarkNameTextView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (distance <= 10) {
                    AlphaAnimation onClickAnimation = new AlphaAnimation(1.0f, 0.4f);
                    onClickAnimation.setDuration(100);
                    view.startAnimation(onClickAnimation);
                    Intent goToCommentFeedActivityIntent = new Intent(context, CommentFeedActivity.class);
                    goToCommentFeedActivityIntent.putExtra("landmark_name", landmarkNameTextView.getText());
                    goToCommentFeedActivityIntent.putExtra("username", username);
                    context.startActivity(goToCommentFeedActivityIntent);
                } else {
                    Toast.makeText(context, "Bear must be at most 10 meters away!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}