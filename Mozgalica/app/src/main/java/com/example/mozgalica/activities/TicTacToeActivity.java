package com.example.mozgalica.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mozgalica.R;
import com.example.mozgalica.database.DatabaseHelper;

public class TicTacToeActivity extends AppCompatActivity {

    private TextView[][] cells = new TextView[3][3];
    private boolean playerXTurn = true;
    private int roundCount = 0;
    private TextView tvPlayerTurn;
    private DatabaseHelper dbHelper;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tic_tac_toe);

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
        if (currentUser == null || currentUser.isEmpty()) {
            currentUser = "Guest";
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));

        findViewById(R.id.navGames).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));

        findViewById(R.id.navResults).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        tvPlayerTurn = findViewById(R.id.tvPlayerTurn);
        dbHelper = new DatabaseHelper(this);

        initCells();

        findViewById(R.id.btnReset).setOnClickListener(v -> resetBoard());

        tvPlayerTurn.setText(getString(R.string.your_move));
    }

    public void onSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void initCells() {
        int[][] ids = {
                {R.id.btn00, R.id.btn01, R.id.btn02},
                {R.id.btn10, R.id.btn11, R.id.btn12},
                {R.id.btn20, R.id.btn21, R.id.btn22}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = findViewById(ids[i][j]);
                int finalI = i;
                int finalJ = j;
                cells[i][j].setOnClickListener(v -> handleMove(finalI, finalJ));
            }
        }
    }

    private void handleMove(int row, int col) {
        if (!cells[row][col].getText().toString().equals("")) return;

        cells[row][col].setText(playerXTurn ? "X" : "O");
        roundCount++;

        if (checkWin()) {
            String winner = playerXTurn ? currentUser : "System";

            if (playerXTurn) {
                dbHelper.saveGameResultAsync(currentUser, "Tic Tac Toe", "Win", 100);
                Toast.makeText(this, getString(R.string.you_win), Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.saveGameResultAsync(currentUser, "Tic Tac Toe", "Loss", 0);
                Toast.makeText(this, getString(R.string.you_lose), Toast.LENGTH_SHORT).show();
            }

            disableBoard();
        } else if (roundCount == 9) {
            dbHelper.saveGameResultAsync(currentUser, "Tic Tac Toe", "Draw", 50);
            tvPlayerTurn.setText(getString(R.string.draw));
        } else {
            playerXTurn = !playerXTurn;

            if (playerXTurn) {
                tvPlayerTurn.setText(getString(R.string.your_move));
            }

            if (!playerXTurn) {
                new android.os.Handler().postDelayed(this::systemMove, 500);
            }
        }
    }

    private void systemMove() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j].getText().toString().isEmpty()) {
                    cells[i][j].setText("O");
                    if (checkWin()) {
                        cells[i][j].setText("");
                        handleMove(i, j);
                        return;
                    }
                    cells[i][j].setText("");
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j].getText().toString().isEmpty()) {
                    cells[i][j].setText("X");
                    if (checkWin()) {
                        cells[i][j].setText("");
                        handleMove(i, j);
                        return;
                    }
                    cells[i][j].setText("");
                }
            }
        }

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (cells[i][j].getText().toString().isEmpty()) {
                    handleMove(i, j);
                    tvPlayerTurn.setText(getString(R.string.your_move));
                    return;
                }
    }

    private boolean checkWin() {
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                field[i][j] = cells[i][j].getText().toString();

        for (int i = 0; i < 3; i++)
            if (!field[i][0].equals("") && field[i][0].equals(field[i][1]) && field[i][1].equals(field[i][2]))
                return true;

        for (int j = 0; j < 3; j++)
            if (!field[0][j].equals("") && field[0][j].equals(field[1][j]) && field[1][j].equals(field[2][j]))
                return true;

        return (!field[0][0].equals("") && field[0][0].equals(field[1][1]) && field[1][1].equals(field[2][2])) ||
                (!field[0][2].equals("") && field[0][2].equals(field[1][1]) && field[1][1].equals(field[2][0]));
    }

    private void disableBoard() {
        for (TextView[] row : cells)
            for (TextView cell : row)
                cell.setEnabled(false);
    }

    private void resetBoard() {
        for (TextView[] row : cells)
            for (TextView cell : row) {
                cell.setText("");
                cell.setEnabled(true);
            }

        roundCount = 0;
        playerXTurn = true;
        tvPlayerTurn.setText(getString(R.string.your_move));
    }
}
