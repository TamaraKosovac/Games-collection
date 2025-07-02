package com.example.mozgalica.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etGame = findViewById(R.id.etGame);
        EditText etResult = findViewById(R.id.etResult);
        EditText etMinScore = findViewById(R.id.etMinScore);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String game = etGame.getText().toString().trim();
            String result = etResult.getText().toString().trim();
            String minScoreStr = etMinScore.getText().toString().trim();
            int minScore = minScoreStr.isEmpty() ? 0 : Integer.parseInt(minScoreStr);

            resultsContainer.removeAllViews();

            new Thread(() -> {
                List<GameResult> results = dbHelper.getFilteredResults(username, game, result, minScore);

                runOnUiThread(() -> {
                    for (GameResult r : results) {
                        View card = LayoutInflater.from(this).inflate(R.layout.card_result, resultsContainer, false);

                        ((TextView) card.findViewById(R.id.tvUsername)).setText(r.getUsername());
                        ((TextView) card.findViewById(R.id.tvGame)).setText(r.getGame());
                        ((TextView) card.findViewById(R.id.tvResult)).setText(r.getResult());
                        ((TextView) card.findViewById(R.id.tvScore)).setText(String.valueOf(r.getScore()));
                        ((TextView) card.findViewById(R.id.tvTimestamp)).setText(String.valueOf(r.getTimestamp()));

                        resultsContainer.addView(card);
                    }
                });
            }).start();
        });
        loadResults("", "", "", 0);
    }

    private void loadResults(String username, String game, String result, int minScore) {
        resultsContainer.removeAllViews();

        new Thread(() -> {
            List<GameResult> results = dbHelper.getFilteredResults(username, game, result, minScore);

            runOnUiThread(() -> {
                for (GameResult r : results) {
                    View card = LayoutInflater.from(this).inflate(R.layout.card_result, resultsContainer, false);

                    ((TextView) card.findViewById(R.id.tvUsername)).setText(r.getUsername());
                    ((TextView) card.findViewById(R.id.tvGame)).setText(r.getGame());
                    ((TextView) card.findViewById(R.id.tvResult)).setText(r.getResult());
                    ((TextView) card.findViewById(R.id.tvScore)).setText(String.valueOf(r.getScore()));
                    ((TextView) card.findViewById(R.id.tvTimestamp)).setText(String.valueOf(r.getTimestamp()));

                    resultsContainer.addView(card);
                }
            });
        }).start();
    }
}
