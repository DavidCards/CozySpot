package com.example.cozyspot.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cozyspot.database.Classes.Message;
import com.example.cozyspot.R;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesSimpleAdapter extends RecyclerView.Adapter<MessagesSimpleAdapter.MessageViewHolder> {
    private final List<Message> messages;
    private final int loggedUserId;
    private final Map<Integer, String> userNamesCache;
    private OnMessageClickListener onMessageClickListener;

    public interface OnMessageClickListener {
        void onMessageClick(Message message);
    }

    public void setOnMessageClickListener(OnMessageClickListener listener) {
        this.onMessageClickListener = listener;
    }

    public MessagesSimpleAdapter(Context context, List<Message> messages, int loggedUserId, Map<Integer, String> userNamesCache) {
        this.messages = messages;
        this.loggedUserId = loggedUserId;
        this.userNamesCache = userNamesCache;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        String from = (message.getSenderId() == loggedUserId) ? "Eu" : userNamesCache.getOrDefault(message.getSenderId(), "?");
        String to = (message.getReceiverId() == loggedUserId) ? "Eu" : userNamesCache.getOrDefault(message.getReceiverId(), "?");
        holder.textViewContent.setText(message.getContent());
        holder.textViewFromTo.setText("De: " + from + "   Para: " + to);
        holder.textViewTimestamp.setText("Data: " + message.getTimestamp());
        holder.itemView.setOnClickListener(v -> {
            if (onMessageClickListener != null) {
                onMessageClickListener.onMessageClick(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContent, textViewFromTo, textViewTimestamp;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContent = itemView.findViewById(R.id.textViewMessageContent);
            textViewFromTo = itemView.findViewById(R.id.textViewMessageFromTo);
            textViewTimestamp = itemView.findViewById(R.id.textViewMessageTimestamp);
        }
    }
}
