package com.example.mozgalica.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mozgalica.R;
import com.example.mozgalica.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.widget.TextView;
import android.widget.Toast;


public class MemoryMatchActivity extends AppCompatActivity {

    private String currentUser = "";
    private DatabaseHelper dbHelper;

    private ImageButton[] cards = new ImageButton[16];
    private int[] cardOrder = new int[16];
    private boolean[] matched = new boolean[16];

    private int firstCardIndex = -1;
    private int moves = 0;
    private TextView tvMoves;


    private int[] cardImages = {
            R.drawable.ic_card_1, R.drawable.ic_card_2, R.drawable.ic_card_3, R.drawable.ic_card_4,
            R.drawable.ic_card_5, R.drawable.ic_card_6, R.drawable.ic_card_7, R.drawable.ic_card_8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memory_match);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentUser = getIntent().getStringExtra("USERNAME");
        dbHelper = new DatabaseHelper(this);
        tvMoves = findViewById(R.id.tvMoves);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));

        findViewById(R.id.navGames).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));

        findViewById(R.id.navResults).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
        findViewById(R.id.btnReset).setOnClickListener(v -> shuffleCards());

        initializeCards();
        shuffleCards();
    }

    private void initializeCards() {
        for (int i = 0; i < 16; i++) {
            String idName = "card_" + (i / 4) + (i % 4);
            int resId = getResources().getIdentifier(idName, "id", getPackageName());
            cards[i] = findViewById(resId);
            int finalI = i;
            cards[i].setOnClickListener(v -> onCardClick(finalI));
        }
    }

    private void shuffleCards() {
        List<Integer> imageIndices = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            imageIndices.add(i);
            imageIndices.add(i);
        }
        Collections.shuffle(imageIndices);
        for (int i = 0; i < 16; i++) {
            cardOrder[i] = imageIndices.get(i);
            matched[i] = false;
            cards[i].setBackgroundResource(0);
            cards[i].setImageResource(R.drawable.card_back);
            cards[i].setEnabled(true);
        }
        firstCardIndex = -1;
        moves = 0;
        if (tvMoves != null) {
            tvMoves.setText(getString(R.string.moves_default) + " 0");
        }
    }

    private void onCardClick(int index) {
        if (matched[index] || cards[index].getDrawable() != null && index == firstCardIndex) return;

        flipCard(cards[index], cardImages[cardOrder[index]], true);

        if (firstCardIndex == -1) {
            firstCardIndex = index;
        } else {
            int secondIndex = index;
            moves++;
            tvMoves.setText(getString(R.string.moves_default) + " " + moves);
            cards[index].postDelayed(() -> {
                if (cardOrder[secondIndex] == cardOrder[firstCardIndex]) {
                    matched[secondIndex] = true;
                    matched[firstCardIndex] = true;
                    checkWin();
                } else {
                    flipCard(cards[secondIndex], R.drawable.card_back, false);
                    flipCard(cards[firstCardIndex], R.drawable.card_back, false);
                }
                firstCardIndex = -1;
            }, 800);
        }
    }

    private void flipCard(ImageButton card, int imageRes, boolean showWhiteBackground) {
        card.animate().scaleX(0f).setDuration(150).withEndAction(() -> {
            if (showWhiteBackground) {
                card.setBackgroundColor(getResources().getColor(android.R.color.white));
            } else {
                card.setBackgroundResource(0);
            }
            card.setImageResource(imageRes);
            card.setScaleX(0f);
            card.animate().scaleX(1f).setDuration(150).start();
        }).start();
    }

    private void checkWin() {
        for (boolean m : matched) {
            if (!m) return;
        }

        int score;
        if (moves >= 16 && moves <= 20) {
            score = 100;
        } else if (moves <= 25) {
            score = 80;
        } else if (moves <= 30) {
            score = 60;
        } else {
            score = 40;
        }

        dbHelper.saveGameResultAsync(currentUser, "MemoryMatch", "Win", score);

        Toast.makeText(this, getString(R.string.sudoku_win), Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("USERNAME", currentUser);
        startActivity(intent);
    }
}
