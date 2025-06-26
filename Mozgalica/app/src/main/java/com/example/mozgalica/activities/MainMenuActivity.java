package com.example.mozgalica.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mozgalica.R;

public class MainMenuActivity extends AppCompatActivity {

    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        }

        tvWelcome = findViewById(R.id.tvWelcome);
        String username = getIntent().getStringExtra("USERNAME");

        tvWelcome.setText(getString(R.string.welcome, username));
    }
    public void onPlayTicTacToe(View view) {
        startActivity(new Intent(this, TicTacToeActivity.class));
    }

    public void onPlayMemoryMatch(View view) {
        startActivity(new Intent(this, MemoryMatchActivity.class));
    }

    public void onPlaySudoku(View view) {
        startActivity(new Intent(this, SudokuActivity.class));
    }

    public void onShowHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME"));
        startActivity(intent);
    }

    public void onSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onLogout(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // očisti backstack
        startActivity(intent);
        finish();
    }
}
