package com.example.cozyspot.database;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.User;
import com.example.cozyspot.database.creator.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.editTextText9);
        editTextEmail = findViewById(R.id.editTextTextEmailAddress2);
        editTextPassword = findViewById(R.id.editTextTextPassword2);
        buttonRegister = findViewById(R.id.button7);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                executor.execute(() -> {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    User existing = null;
                    for (User u : db.userDao().getAll()) {
                        if (u.getEmail().equalsIgnoreCase(email)) {
                            existing = u;
                            break;
                        }
                    }
                    if (existing != null) {
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Email já registado", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                    User user = new User(username, email, password, "guest", "default_avatar_user.jpg");
                    db.userDao().insert(user);
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Registo efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
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
