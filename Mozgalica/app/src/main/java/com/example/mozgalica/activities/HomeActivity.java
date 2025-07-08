package com.example.mozgalica.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mozgalica.R;
import android.widget.TextView;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(com.example.mozgalica.utils.LocaleHelper.setLocale(newBase, com.example.mozgalica.utils.LocaleHelper.getLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
            getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white));
        }

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        String username = getIntent().getStringExtra("USERNAME");

        if (username != null && !username.isEmpty()) {
            String welcomeText = getString(R.string.welcome) + ", " + username;
            tvWelcome.setText(welcomeText);
        }

        findViewById(R.id.btnGoToGames).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        findViewById(R.id.btnMyResults).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        findViewById(R.id.btnSettingsIcon).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));

        findViewById(R.id.navGames).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));

        findViewById(R.id.navResults).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }
}
