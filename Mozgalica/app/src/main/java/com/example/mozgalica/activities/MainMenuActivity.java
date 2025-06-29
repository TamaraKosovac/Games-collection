package com.example.mozgalica.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mozgalica.R;

public class MainMenuActivity extends AppCompatActivity {

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

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));

        findViewById(R.id.navGames).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));

        findViewById(R.id.navResults).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
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

    public void onSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onTicTacToeDetails(View view) {
        Intent i = new Intent(this, GameDetailsActivity.class);
        i.putExtra("GAME_NAME", "TicTacToe");
        startActivity(i);
    }

    public void onMemoryMatchDetails(View view) {
        Intent i = new Intent(this, GameDetailsActivity.class);
        i.putExtra("GAME_NAME", "MemoryMatch");
        startActivity(i);
    }

    public void onSudokuDetails(View view) {
        Intent i = new Intent(this, GameDetailsActivity.class);
        i.putExtra("GAME_NAME", "Sudoku");
        startActivity(i);
    }
}
