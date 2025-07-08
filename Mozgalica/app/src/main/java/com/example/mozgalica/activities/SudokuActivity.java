package com.example.mozgalica.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mozgalica.R;
import com.example.mozgalica.database.DatabaseHelper;

public class SudokuActivity extends AppCompatActivity {

    private int[][] solutionBoard;
    private int[][] initialBoard;
    private TextView[][] cells = new TextView[9][9];
    private DatabaseHelper dbHelper;
    private String currentUser = "";
    private long startTime;
    private TextView tvTimer;
    private android.os.Handler timerHandler = new android.os.Handler();
    private Runnable timerRunnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sudoku);
        startTime = System.currentTimeMillis();

        currentUser = getIntent().getStringExtra("USERNAME");

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

        dbHelper = new DatabaseHelper(this);
        tvTimer = findViewById(R.id.tvTimer);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedMillis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                tvTimer.setText(String.format("%d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);


        GridLayout sudokuGrid = findViewById(R.id.sudokuGrid);
        int cellSize = getResources().getDimensionPixelSize(R.dimen.game_button_top_margin);

        generateSudokuBoard();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextView cell = new TextView(this);
                cell.setId(View.generateViewId());
                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(16);
                cell.setBackgroundResource(R.drawable.sudoku_cell_background);
                cell.setLayoutParams(new GridLayout.LayoutParams(
                        new ViewGroup.LayoutParams(cellSize, cellSize)
                ));

                int value = initialBoard[row][col];
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                    cell.setEnabled(false);
                    cell.setTextColor(getColor(android.R.color.black));
                } else {
                    int finalRow = row;
                    int finalCol = col;
                    cell.setOnClickListener(v -> showNumberInput(finalRow, finalCol));
                }

                cells[row][col] = cell;
                sudokuGrid.addView(cell);
            }
        }
        findViewById(R.id.btnReset).setOnClickListener(v -> {
            Intent intent = new Intent(this, SudokuActivity.class);
            intent.putExtra("USERNAME", currentUser);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));

        findViewById(R.id.navGames).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));

        findViewById(R.id.navResults).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }

    private void generateSudokuBoard() {
        solutionBoard = new int[][]{
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };

        initialBoard = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(solutionBoard[i], 0, initialBoard[i], 0, 9);
        }

        for (int i = 0; i < 40; i++) {
            int r = (int) (Math.random() * 9);
            int c = (int) (Math.random() * 9);
            initialBoard[r][c] = 0;
        }
    }

    private void showNumberInput(int row, int col) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_number));
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        builder.setItems(numbers, (dialog, which) -> {
            int selectedNumber = which + 1;
            cells[row][col].setText(String.valueOf(selectedNumber));

            if (selectedNumber == solutionBoard[row][col]) {
                cells[row][col].setTextColor(getColor(R.color.colorOnPrimary));
                checkIfGameCompleted();
            } else {
                cells[row][col].setTextColor(getColor(R.color.colorError));
            }
        });
        builder.show();
    }

    private void checkIfGameCompleted() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String cellText = cells[row][col].getText().toString();
                if (cellText.isEmpty() || Integer.parseInt(cellText) != solutionBoard[row][col]) {
                    return;
                }
            }
        }

        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        long minutes = durationMillis / (1000 * 60);

        int score;
        if (minutes < 5) {
            score = 100;
        } else if (minutes < 10) {
            score = 80;
        } else if (minutes < 15) {
            score = 60;
        } else {
            score = 40;
        }

        dbHelper.saveGameResultAsync(currentUser, "Sudoku", "Win", score);

        timerHandler.removeCallbacks(timerRunnable);

        Toast.makeText(this, getString(R.string.sudoku_win), Toast.LENGTH_SHORT).show();
        finish();
    }


    public void onSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
