package com.example.mozgalica.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mozgalica.R;
import com.example.mozgalica.database.DatabaseHelper;
import com.example.mozgalica.models.User;
import com.example.mozgalica.utils.Validator;
import android.text.InputType;
import android.widget.ImageView;


public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etUsername, etPassword;
    private Button btnRegister;
    private DatabaseHelper dbHelper;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        }

        dbHelper = new DatabaseHelper(this);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.visibility_off);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.visibility);
            }
            etPassword.setSelection(etPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        btnRegister.setOnClickListener(view -> {
            String fullName = etFullName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (fullName.isEmpty()) {
                Toast.makeText(this, getString(R.string.name_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.empty_fields_error), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Validator.isPasswordValid(password)) {
                Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_LONG).show();
                return;
            }

            String hashedPassword = Validator.hashPassword(password);
            User user = new User(username, hashedPassword, fullName);

            boolean success = dbHelper.addUser(user);

            if (success) {
                Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(this, getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
            }
        });

        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvGoToLogin.setText(getString(R.string.go_to_login));
        tvGoToLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}
