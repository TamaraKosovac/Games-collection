package com.example.mozgalica.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mozgalica.R;

public class GameDetailsActivity extends AppCompatActivity {

    private TextView tvTitle, tvRules, tvScoring;
    private Button btnWatchVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
            getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white));
        }

        tvTitle = findViewById(R.id.tvTitle);
        tvRules = findViewById(R.id.tvRules);
        tvScoring = findViewById(R.id.tvScoring);
        btnWatchVideo = findViewById(R.id.btnWatchVideo);

        String game = getIntent().getStringExtra("GAME_NAME");

        switch (game) {
            case "TicTacToe":
                tvTitle.setText(R.string.tic_tac_toe);
                tvRules.setText(R.string.tic_tac_toe_rules);
                tvScoring.setText(R.string.tic_tac_toe_scoring);
                btnWatchVideo.setOnClickListener(v -> openYoutube("https://www.youtube.com/watch?v=3qzcAMShotQ"));
                break;

            case "MemoryMatch":
                tvTitle.setText(R.string.memory_match);
                tvRules.setText(R.string.memory_rules);
                tvScoring.setText(R.string.memory_scoring);
                btnWatchVideo.setOnClickListener(v -> openYoutube("https://www.youtube.com/watch?v=oFfYmrGeTPs"));
                break;

            case "Sudoku":
                tvTitle.setText(R.string.sudoku);
                tvRules.setText(R.string.sudoku_rules);
                tvScoring.setText(R.string.sudoku_scoring);
                btnWatchVideo.setOnClickListener(v -> openYoutube("https://www.youtube.com/watch?v=8zRXDsGydeQ"));
                break;
        }
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnSettingsIcon).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));

        findViewById(R.id.navGames).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));

        findViewById(R.id.navResults).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }

    private void openYoutube(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }
}
