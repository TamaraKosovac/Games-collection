package com.example.mozgalica.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mozgalica.R;
import com.example.mozgalica.database.DatabaseHelper;
import android.text.InputType;
import android.widget.ImageView;


public class LoginActivity extends AppCompatActivity {

    private EditText etLoginUsername, etLoginPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private DatabaseHelper dbHelper;
    private ImageView ivToggleLoginPassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(com.example.mozgalica.utils.LocaleHelper.setLocale(newBase, com.example.mozgalica.utils.LocaleHelper.getLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        }

        dbHelper = new DatabaseHelper(this);

        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        ivToggleLoginPassword = findViewById(R.id.ivToggleLoginPassword);

        ivToggleLoginPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleLoginPassword.setImageResource(R.drawable.visibility_off);
            } else {
                etLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleLoginPassword.setImageResource(R.drawable.visibility);
            }
            etLoginPassword.setSelection(etLoginPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        btnLogin.setOnClickListener(view -> {
            String username = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_credentials), Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.checkUser(username, password)) {
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }

        });

        tvGoToRegister.setText(getString(R.string.go_to_register));
        tvGoToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
