package com.example.cs160_sp18.prog3;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Displays a list of comments for a particular landmark.
public class CommentFeedActivity extends AppCompatActivity {

    private static final String TAG = CommentFeedActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Comment> mComments;

    // UI elements
    EditText commentInputBox;
    RelativeLayout layout;
    Button sendButton;
    Toolbar mToolbar;
    Intent goToCommentFeedActivityIntent;
    String username;
    String landmarkName;

    FirebaseDatabase database;
    DatabaseReference commentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_feed);

        // Setup a database instance and reference to comments
        database = FirebaseDatabase.getInstance();
        commentDatabase = database.getReference("comments");

        goToCommentFeedActivityIntent = getIntent();
        landmarkName = goToCommentFeedActivityIntent.getStringExtra("landmark_name");
        username = goToCommentFeedActivityIntent.getStringExtra("username");

        mComments = new ArrayList<>();
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(landmarkName + " Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // hook up UI elements
        layout = (RelativeLayout) findViewById(R.id.comment_layout);
        commentInputBox = (EditText) layout.findViewById(R.id.comment_input_edit_text);
        sendButton = (Button) layout.findViewById(R.id.send_button);

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Citation:
        // Used code from this post to handle "Enter" on keyboard
        // https://stackoverflow.com/questions/1489852/android-handle-enter-in-an-edittext
        commentInputBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    // Perform send on "Enter"
                    sendButton.performClick();
                    return true;
                }
                return false;
            }
        });

        // create an onclick for the send button
        setOnClickForSendButton();

        // Read from the database
        commentDatabase.child(landmarkName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String username_data = ds.child("username").getValue(String.class);
                    Date date_data = ds.child("date").getValue(Date.class);
                    String text_data = ds.child("text").getValue(String.class);
                    mComments.add(new Comment(text_data, username_data, date_data));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read comment", error.toException());
            }
        });

        // use the comments in mComments to create an adapter. This will populate mRecyclerView
        // with a custom cell (with comment_cell_layout) for each comment in mComments
        setAdapterAndUpdateData();

        int numComments = mRecyclerView.getAdapter().getItemCount();
        if (numComments > 0) {
            // scroll to position
            mRecyclerView.scrollToPosition(numComments - 1);
        }
    }

    private void setOnClickForSendButton() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentInputBox.getText().toString();
                if (TextUtils.isEmpty(comment)) {
                    // don't do anything if nothing was added
                    commentInputBox.requestFocus();
                } else {
                    // clear edit text, post comment
                    commentInputBox.setText("");
                    postNewComment(comment);
                }
            }
        });
    }

    private void setAdapterAndUpdateData() {
        // create a new adapter with the updated mComments array
        // this will "refresh" our recycler view
        mAdapter = new CommentAdapter(this, mComments);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void postNewComment(String commentText) {
        Comment newComment = new Comment(commentText, username, new Date());
        mComments.add(newComment);
        setAdapterAndUpdateData();

        // Add comment to database
        // Create new comment at "$landmarkName_comments/$commentID"
        String commentID = commentDatabase.child(landmarkName).push().getKey();
        Map<String, Object> commentValues = newComment.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(landmarkName + "/" + commentID, commentValues);

        commentDatabase.updateChildren(childUpdates);

        int numComments = mRecyclerView.getAdapter().getItemCount();
        // scroll to position
        mRecyclerView.scrollToPosition(numComments - 1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}