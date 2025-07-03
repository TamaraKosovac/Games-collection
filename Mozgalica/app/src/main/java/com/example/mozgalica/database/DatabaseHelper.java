package com.example.mozgalica.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.widget.Toast;

import com.example.mozgalica.models.GameResult;
import com.example.mozgalica.models.User;
import com.example.mozgalica.utils.Validator;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Mozgalica.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FULLNAME = "fullname";

    private final Context context;
    private final Handler mainHandler;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(this.context.getMainLooper());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_FULLNAME + " TEXT,"
                + COLUMN_USERNAME + " TEXT PRIMARY KEY,"
                + COLUMN_PASSWORD + " TEXT"
                + ")";

        String CREATE_RESULTS_TABLE = "CREATE TABLE results (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "game TEXT," +
                "result TEXT," +
                "score INTEGER," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_RESULTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS results");
        onCreate(db);
    }

    public void addUserAsync(User user, Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            if (isUsernameTakenSync(user.getUsername())) {
                mainHandler.post(onFailure);
            } else {
                SQLiteDatabase db = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_USERNAME, user.getUsername());
                values.put(COLUMN_PASSWORD, user.getPassword());
                values.put(COLUMN_FULLNAME, user.getFullName());

                long result = db.insert(TABLE_USERS, null, values);
                db.close();

                if (result != -1) {
                    mainHandler.post(onSuccess);
                } else {
                    mainHandler.post(onFailure);
                }
            }
        }).start();
    }

    public void checkUserAsync(String username, String password, Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            String hashedPassword = Validator.hashPassword(password);
            SQLiteDatabase db = getReadableDatabase();

            Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME},
                    COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                    new String[]{username, hashedPassword}, null, null, null);

            boolean exists = (cursor != null && cursor.moveToFirst());
            if (cursor != null) cursor.close();

            mainHandler.post(() -> {
                if (exists) onSuccess.run();
                else onFailure.run();
            });
        }).start();
    }

    public void saveGameResultAsync(String username, String game, String result, int score) {
        new Thread(() -> {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("game", game);
            values.put("result", result);
            values.put("score", score);
            db.insert("results", null, values);
            db.close();

        }).start();
    }

    private boolean isUsernameTakenSync(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME},
                COLUMN_USERNAME + "=?", new String[]{username},
                null, null, null);
        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        return exists;
    }

    public List<GameResult> getFilteredResults(String username, String game, String result, int minScore) {
        List<GameResult> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM results WHERE 1=1";
        List<String> args = new ArrayList<>();

        if (!username.isEmpty()) {
            query += " AND username LIKE ?";
            args.add("%" + username + "%");
        }
        if (!game.isEmpty()) {
            query += " AND game LIKE ?";
            args.add("%" + game + "%");
        }
        if (!result.isEmpty()) {
            query += " AND result LIKE ?";
            args.add("%" + result + "%");
        }
        query += " AND score >= ?";
        args.add(String.valueOf(minScore));

        Cursor cursor = db.rawQuery(query, args.toArray(new String[0]));
        if (cursor.moveToFirst()) {
            do {
                GameResult r = new GameResult(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("game")),
                        cursor.getString(cursor.getColumnIndexOrThrow("result")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("score")),
                        cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
                );
                results.add(r);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return results;
    }
}
