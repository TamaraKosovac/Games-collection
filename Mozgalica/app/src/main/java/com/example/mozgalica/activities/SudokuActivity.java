package com.example.mozgalica.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sudoku);
        currentUser = getIntent().getStringExtra("USERNAME");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

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
        builder.setTitle("Unesi broj");
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

        dbHelper.saveGameResultAsync(currentUser, "Sudoku", "Win", 100);
        new AlertDialog.Builder(this)
                .setTitle("Bravo!")
                .setMessage("Uspješno si riješio Sudoku.")
                .setPositiveButton("OK", (d, w) -> finish())
                .show();
    }

    public void onSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
