package com.example.mozgalica.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mozgalica.R;
import com.example.mozgalica.database.DatabaseHelper;
import com.example.mozgalica.models.GameResult;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout resultsContainer;
    private boolean isSearchVisible = false;
    private TextView tvNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        resultsContainer = findViewById(R.id.resultsContainer);
        tvNoResults = findViewById(R.id.tvNoResults);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etGame = findViewById(R.id.etGame);
        EditText etResult = findViewById(R.id.etResult);
        EditText etMinScore = findViewById(R.id.etMinScore);
        Button btnSearch = findViewById(R.id.btnSearch);

        loadResults("", "", "", 0);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        LinearLayout searchFieldsContainer = findViewById(R.id.searchFieldsContainer);
        ImageView btnSearchIcon = findViewById(R.id.btnSearchIcon);

        btnSearchIcon.setOnClickListener(v -> {
            isSearchVisible = !isSearchVisible;
            searchFieldsContainer.setVisibility(isSearchVisible ? View.VISIBLE : View.GONE);

            if (!isSearchVisible) {
                etUsername.setText("");
                etGame.setText("");
                etResult.setText("");
                etMinScore.setText("");
                loadResults("", "", "", 0);
            }
        });

        btnSearch.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String game = etGame.getText().toString().trim();
            String result = etResult.getText().toString().trim();
            String minScoreStr = etMinScore.getText().toString().trim();
            int minScore = minScoreStr.isEmpty() ? 0 : Integer.parseInt(minScoreStr);

            resultsContainer.removeAllViews();
            tvNoResults.setVisibility(View.GONE);
            searchFieldsContainer.setVisibility(View.GONE);
            isSearchVisible = false;

            new Thread(() -> {
                List<GameResult> results = dbHelper.getFilteredResults(username, game, result, minScore);

                runOnUiThread(() -> {
                    if (results.isEmpty()) {
                        tvNoResults.setVisibility(View.VISIBLE);
                    } else {
                        tvNoResults.setVisibility(View.GONE);
                        for (GameResult r : results) {
                            View card = LayoutInflater.from(this).inflate(R.layout.card_result, resultsContainer, false);

                            ((TextView) card.findViewById(R.id.tvUsername)).setText(r.getUsername());
                            ((TextView) card.findViewById(R.id.tvGame)).setText(r.getGame());
                            ((TextView) card.findViewById(R.id.tvResult)).setText(r.getResult());
                            ((TextView) card.findViewById(R.id.tvScore)).setText(String.valueOf(r.getScore()));
                            ((TextView) card.findViewById(R.id.tvTimestamp)).setText(String.valueOf(r.getTimestamp()));

                            resultsContainer.addView(card);
                        }
                    }
                });
            }).start();
        });

        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));

        findViewById(R.id.navGames).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));

        findViewById(R.id.navResults).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }

    private void loadResults(String username, String game, String result, int minScore) {
        resultsContainer.removeAllViews();
        tvNoResults.setVisibility(View.GONE);

        new Thread(() -> {
            List<GameResult> results = dbHelper.getFilteredResults(username, game, result, minScore);

            runOnUiThread(() -> {
                if (results.isEmpty()) {
                    tvNoResults.setVisibility(View.VISIBLE);
                } else {
                    tvNoResults.setVisibility(View.GONE);
                    for (GameResult r : results) {
                        View card = LayoutInflater.from(this).inflate(R.layout.card_result, resultsContainer, false);

                        ((TextView) card.findViewById(R.id.tvUsername)).setText(r.getUsername());
                        ((TextView) card.findViewById(R.id.tvGame)).setText(r.getGame());
                        ((TextView) card.findViewById(R.id.tvResult)).setText(r.getResult());
                        ((TextView) card.findViewById(R.id.tvScore)).setText(String.valueOf(r.getScore()));
                        ((TextView) card.findViewById(R.id.tvTimestamp)).setText(String.valueOf(r.getTimestamp()));

                        resultsContainer.addView(card);
                    }
                }
            });
        }).start();
    }
}
