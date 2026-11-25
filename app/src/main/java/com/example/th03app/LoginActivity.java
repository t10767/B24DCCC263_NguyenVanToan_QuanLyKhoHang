package com.example.th03app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.th03app.database.DatabaseHelper;
import com.example.th03app.utils.InsetsHelper;
import com.example.th03app.utils.SimpleTextWatcher;

/**
 * Màn hình 1: Đăng nhập. Tài khoản được kiểm tra bằng truy vấn bảng users.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Biểu tượng thanh trạng thái màu trắng vì header phía sau có nền xanh
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_login);
        InsetsHelper.applySystemBarInsets(findViewById(R.id.main), findViewById(R.id.header));

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> checkLogin());

        // Nhấn "Xong" trên bàn phím ở ô mật khẩu cũng đăng nhập
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkLogin();
                return true;
            }
            return false;
        });

        // Người dùng nhập lại thì ẩn thông báo lỗi
        etUsername.addTextChangedListener(SimpleTextWatcher.onChanged(this::hideError));
        etPassword.addTextChangedListener(SimpleTextWatcher.onChanged(this::hideError));
    }

    private void checkLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Không cho đăng nhập khi bỏ trống
        if (username.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_login_empty));
            if (username.isEmpty()) {
                etUsername.requestFocus();
            } else {
                etPassword.requestFocus();
            }
            return;
        }

        if (dbHelper.checkLogin(username, password)) {
            hideError();
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Nhấn Back ở màn hình danh sách sẽ không quay lại màn hình đăng nhập
        } else {
            showError(getString(R.string.error_login_wrong));
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
