package com.example.mozgalica.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mozgalica.R;

public class MainMenuActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(com.example.mozgalica.utils.LocaleHelper.setLocale(newBase, com.example.mozgalica.utils.LocaleHelper.getLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        }

        username = getIntent().getStringExtra("USERNAME");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        findViewById(R.id.navGames).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        findViewById(R.id.navResults).setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });
    }

    public void onPlayTicTacToe(View view) {
        Intent intent = new Intent(this, TicTacToeActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    public void onPlayMemoryMatch(View view) {
        Intent intent = new Intent(this, MemoryMatchActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    public void onPlaySudoku(View view) {
        Intent intent = new Intent(this, SudokuActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    public void onSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    public void onTicTacToeDetails(View view) {
        Intent i = new Intent(this, GameDetailsActivity.class);
        i.putExtra("GAME_NAME", "TicTacToe");
        i.putExtra("USERNAME", username);
        startActivity(i);
    }

    public void onMemoryMatchDetails(View view) {
        Intent i = new Intent(this, GameDetailsActivity.class);
        i.putExtra("GAME_NAME", "MemoryMatch");
        i.putExtra("USERNAME", username);
        startActivity(i);
    }

    public void onSudokuDetails(View view) {
        Intent i = new Intent(this, GameDetailsActivity.class);
        i.putExtra("GAME_NAME", "Sudoku");
        i.putExtra("USERNAME", username);
        startActivity(i);
    }
}
