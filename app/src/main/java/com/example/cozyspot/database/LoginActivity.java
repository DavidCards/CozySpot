package com.example.cozyspot.database;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.User;
import com.example.cozyspot.database.creator.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean firstLoginAttempt = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextTextEmailAddress2);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        buttonLogin = findViewById(R.id.button10);

        TextView textViewRegister = findViewById(R.id.textViewRegister);
        String fullText = getString(R.string.no_account_register);
        SpannableString spannableString = new SpannableString(fullText);
        int start = fullText.indexOf("Registe-se");
        int end = start + "Registe-se".length();
        if (start == -1) {
            // fallback para "Register now" se não encontrar "Registe-se"
            start = fullText.indexOf("Register now");
            end = start + "Register now".length();
        }
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        };
        if (start != -1 && end > start) {
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewRegister.setText(spannableString);
        textViewRegister.setMovementMethod(LinkMovementMethod.getInstance());

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                executor.execute(() -> {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    User user = db.userDao().findByEmailAndPassword(email, password);
                    runOnUiThread(() -> {
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("USER_ID", user.getId());
                            intent.putExtra("USER_ROLE", user.getRole());
                            startActivity(intent);
                            finish();
                        } else {
                            if (firstLoginAttempt) {
                                firstLoginAttempt = false;
                                Toast.makeText(LoginActivity.this, getString(R.string.database_preparation), Toast.LENGTH_SHORT).show();
                                buttonLogin.setEnabled(false);
                                buttonLogin.postDelayed(() -> {
                                    buttonLogin.setEnabled(true);
                                    buttonLogin.performClick();
                                }, 2000);
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.invalid_email_or_password), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
