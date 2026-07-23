package com.example.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.noteapp.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        initViews();
        setupListeners();
        checkFirstTime();
    }

    private void checkFirstTime() {
        userViewModel.getAllUsers(users -> {
            runOnUiThread(() -> {
                if (users == null || users.isEmpty()) {
                    Toast.makeText(this, "Chưa có tài khoản. Vui lòng đăng ký.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
    }

    private void setupListeners() {
        findViewById(R.id.tv_register).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            if (validateInputs()) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                userViewModel.login(email, password, user -> {
                    runOnUiThread(() -> {
                        if (user != null) {
                            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("user_id", String.valueOf(user.getId()))
                                    .putString("full_name", user.getFullName())
                                    .putString("email", user.getEmail())
                                    .putString("phone", user.getPhone())
                                    .putBoolean("logged_in", true)
                                    .apply();

                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

        findViewById(R.id.tv_forgot_password).setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_google).setOnClickListener(v -> {
            Toast.makeText(this, "Đăng nhập bằng Google đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_facebook).setOnClickListener(v -> {
            Toast.makeText(this, "Đăng nhập bằng Facebook đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateInputs() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }
}
