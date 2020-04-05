package com.example.cs160_sp18.prog3;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button loginButton;
    private Intent goToLandmarkFeedActivityIntent;
    public String username;
    private static final String TAG = LandmarkFeedActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText usernameInput = findViewById(R.id.usernameInput);
        loginButton = findViewById(R.id.loginButton);
        toolbar = findViewById(R.id.titleToolbar);
        toolbar.setTitle("   " + getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        loginButton.setAlpha(.4f);
        loginButton.setClickable(false);

        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                username = editable.toString();
                loginButton.setClickable(true);
                loginButton.setAlpha(1f);
            }
        });

        // Citation:
        // Used code from this post to handle "Enter" on keyboard
        // https://stackoverflow.com/questions/1489852/android-handle-enter-in-an-edittext
        usernameInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    // Perform send on "Enter"
                    loginButton.performClick();
                    return true;
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        if (username.length() == 0 || username.length() > 16) {
            loginButton.setAlpha(.4f);
            loginButton.setClickable(false);
            Toast.makeText(MainActivity.this, "Enter a username between 1 to 16 characters", Toast.LENGTH_SHORT).show();
        } else {
            goToLandmarkFeedActivityIntent = new Intent(MainActivity.this, LandmarkFeedActivity.class);
            goToLandmarkFeedActivityIntent.putExtra("username", username);
            startActivity(goToLandmarkFeedActivityIntent);
        }
    }
}
