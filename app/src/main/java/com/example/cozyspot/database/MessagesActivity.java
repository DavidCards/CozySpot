package com.example.cozyspot.database;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.Message;
import com.example.cozyspot.database.creator.AppDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.widget.Toast;

public class MessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Message> messageList = new ArrayList<>();
    private AppDatabase db;
    private ExecutorService executorService;
    private EditText editTextReply;
    private Button buttonSendReply;
    private int selectedMessageSenderId = -1;
    private Spinner spinnerUsers;
    private List<com.example.cozyspot.database.Classes.User> allUsersList = new ArrayList<>();
    private List<Integer> userIdsForSpinner = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editTextReply = findViewById(R.id.editTextReply);
        buttonSendReply = findViewById(R.id.buttonSendReply);
        spinnerUsers = findViewById(R.id.spinnerUsers);
        editTextReply.setVisibility(View.VISIBLE);
        buttonSendReply.setVisibility(View.VISIBLE);
        editTextReply.setHint("Digite sua mensagem ou use id:mensagem para enviar para alguém específico");
        selectedMessageSenderId = -1;

        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        int userId = getIntent().getIntExtra("USER_ID", -1);
        String userRole = getIntent().getStringExtra("USER_ROLE");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<Message> messagesToShow = new ArrayList<>();
            Map<Integer, String> userNamesCache = new HashMap<>();
            allUsersList = db.userDao().getAll();
            for (com.example.cozyspot.database.Classes.User u : allUsersList) {
                userNamesCache.put(u.getId(), u.getUserName());
            }
            List<String> userNamesForSpinner = new ArrayList<>();
            userIdsForSpinner.clear();
            for (com.example.cozyspot.database.Classes.User u : allUsersList) {
                if (u.getId() != userId && !userNamesForSpinner.contains(u.getUserName())) {
                    userNamesForSpinner.add(u.getUserName());
                    userIdsForSpinner.add(u.getId());
                }
            }
            runOnUiThread(() -> {
                ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(MessagesActivity.this, android.R.layout.simple_spinner_item, userNamesForSpinner);
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUsers.setAdapter(adapterSpinner);
            });
            for (Message m : db.messageDao().getAll()) {
                if (m.getSenderId() == userId || m.getReceiverId() == userId) messagesToShow.add(m);
            }
            runOnUiThread(() -> {
                MessagesSimpleAdapter adapter = new MessagesSimpleAdapter(MessagesActivity.this, messagesToShow, userId, userNamesCache);
                adapter.setOnMessageClickListener(message -> {
                    selectedMessageSenderId = message.getSenderId();
                    editTextReply.setHint("Responder para " + userNamesCache.getOrDefault(selectedMessageSenderId, ""));
                });
                recyclerView.setAdapter(adapter);
            });
        });

        buttonSendReply.setOnClickListener(v -> {
            String reply = editTextReply.getText().toString().trim();
            if (reply.isEmpty()) {
                Toast.makeText(this, "Escreva uma mensagem.", Toast.LENGTH_SHORT).show();
                return;
            }
            int userIdReply = getIntent().getIntExtra("USER_ID", -1);
            int receiverId;
            if (selectedMessageSenderId != -1) {
                receiverId = selectedMessageSenderId;
            } else {
                int pos = spinnerUsers.getSelectedItemPosition();
                if (pos < 0 || pos >= userIdsForSpinner.size()) {
                    Toast.makeText(this, "Selecione o destinatário.", Toast.LENGTH_SHORT).show();
                    return;
                }
                receiverId = userIdsForSpinner.get(pos);
            }
            String timestamp = "2024-06-15 12:00:00";
            Message replyMessage = new Message(userIdReply, receiverId, reply, timestamp);
            executorService.execute(() -> {
                db.messageDao().insert(replyMessage);
                runOnUiThread(() -> {
                    editTextReply.setText("");
                    selectedMessageSenderId = -1;
                    Toast.makeText(this, "Mensagem enviada!", Toast.LENGTH_SHORT).show();
                });
            });
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                startActivity(new Intent(this, MainActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
            } else if (itemId == R.id.menu_profile) {
                startActivity(new Intent(this, Profile.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
            } else if (itemId == R.id.menu_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
            } else if (itemId == R.id.menu_messages) {
                startActivity(new Intent(this, MessagesActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
            } else if (itemId == R.id.menu_bookings) {
                drawerLayout.closeDrawers();
                drawerLayout.postDelayed(() -> {
                    Intent intent = new Intent(this, MyBookingsActivity.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("USER_ROLE", userRole);
                    startActivity(intent);
                }, 250);
                return true;
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }
}
