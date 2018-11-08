package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ChatMessage.TABLE_CHAT_MESSAGE)
public class ChatMessage {
    public static final String TABLE_CHAT_MESSAGE = "chat_message";
    public static final String FIELD_MESSAGE_ID = "message_id";
    public static final String FIELD_FROM_USER_ID = "sender";
    public static final String FIELD_TO_USER_ID = "receiver";
    public static final String FIELD_CONTENT = "content";

    @DatabaseField(generatedId = true, columnName = FIELD_MESSAGE_ID)
    private int messageId;

    @DatabaseField(columnName=FIELD_FROM_USER_ID, foreign = true, foreignAutoRefresh = true)
    private User sender;

    @DatabaseField(columnName=FIELD_TO_USER_ID, foreign = true, foreignAutoRefresh = true)
    private User receiver;

    @DatabaseField(columnName=FIELD_CONTENT)
    private String content;

    public int getMessageId() {
        return this.messageId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
