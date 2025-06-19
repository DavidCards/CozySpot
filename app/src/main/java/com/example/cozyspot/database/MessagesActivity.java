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
            List<Message> messagesToShow = db.messageDao().getMessagesForUser(userId);
            messagesToShow.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));
            Map<Integer, String> userNamesCache = new HashMap<>();
            allUsersList = db.userDao().getAll();
            for (com.example.cozyspot.database.Classes.User u : allUsersList) {
                userNamesCache.put(u.getId(), u.getUserName());
            }
            List<Integer> receivers = db.messageDao().getReceiversForUser(userId);
            List<Integer> senders = db.messageDao().getSendersForUser(userId);
            java.util.Set<Integer> contactIds = new java.util.HashSet<>();
            contactIds.addAll(receivers);
            contactIds.addAll(senders);
            contactIds.remove(userId);
            List<String> userNamesForSpinner = new ArrayList<>();
            userIdsForSpinner.clear();
            for (com.example.cozyspot.database.Classes.User u : allUsersList) {
                if (contactIds.contains(u.getId())) {
                    userNamesForSpinner.add(u.getUserName());
                    userIdsForSpinner.add(u.getId());
                }
            }
            runOnUiThread(() -> {
                ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(MessagesActivity.this, android.R.layout.simple_spinner_item, userNamesForSpinner);
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUsers.setAdapter(adapterSpinner);
            });
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
                Toast.makeText(this, getString(R.string.write_message), Toast.LENGTH_SHORT).show();
                return;
            }
            int userIdReply = getIntent().getIntExtra("USER_ID", -1);
            int receiverId;
            if (selectedMessageSenderId != -1) {
                receiverId = selectedMessageSenderId;
            } else {
                int pos = spinnerUsers.getSelectedItemPosition();
                if (pos < 0 || pos >= userIdsForSpinner.size()) {
                    Toast.makeText(this, getString(R.string.select_recipient), Toast.LENGTH_SHORT).show();
                    return;
                }
                receiverId = userIdsForSpinner.get(pos);
            }
            String timestamp = "2024-06-15 12:00:00";
            Message replyMessage = new Message(userIdReply, receiverId, reply, timestamp);
            executorService.execute(() -> {
                db.messageDao().insert(replyMessage);
                List<Message> messagesToShow = db.messageDao().getMessagesForUser(userIdReply);
                messagesToShow.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));
                Map<Integer, String> userNamesCache = new HashMap<>();
                List<com.example.cozyspot.database.Classes.User> allUsers = db.userDao().getAll();
                for (com.example.cozyspot.database.Classes.User u : allUsers) {
                    userNamesCache.put(u.getId(), u.getUserName());
                }
                runOnUiThread(() -> {
                    editTextReply.setText("");
                    selectedMessageSenderId = -1;
                    Toast.makeText(this, getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
                    MessagesSimpleAdapter adapter = new MessagesSimpleAdapter(MessagesActivity.this, messagesToShow, userIdReply, userNamesCache);
                    adapter.setOnMessageClickListener(message -> {
                        selectedMessageSenderId = message.getSenderId();
                        editTextReply.setHint("Responder para " + userNamesCache.getOrDefault(selectedMessageSenderId, ""));
                    });
                    recyclerView.setAdapter(adapter);
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
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("USER_ROLE", userRole);
            intent.putExtra("NAVIGATION_ITEM_ID", item.getItemId());
            startActivity(intent);
            finish();
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
