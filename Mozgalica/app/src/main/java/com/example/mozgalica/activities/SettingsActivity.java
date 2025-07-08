package com.example.mozgalica.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mozgalica.R;
import com.example.mozgalica.utils.LocaleHelper;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
            getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white));
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnChangeLanguage = findViewById(R.id.btnChangeLanguage);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnChangeLanguage.setOnClickListener(v -> {
            String[] languages = {"English", "Srpski"};
            String[] languageCodes = {"en", "sr"};

            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.select_language)
                    .setItems(languages, (dialog, which) -> {
                        LocaleHelper.setLocale(SettingsActivity.this, languageCodes[which]);

                        Intent intent = new Intent(SettingsActivity.this, MainMenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .show();
        });


        btnLogout.setOnClickListener(v -> {
            Toast.makeText(SettingsActivity.this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class)));

        findViewById(R.id.navGames).setOnClickListener(v ->
                startActivity(new Intent(this, MainMenuActivity.class)));

        findViewById(R.id.navResults).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }
}
