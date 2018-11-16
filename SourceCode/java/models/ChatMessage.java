package models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

@DatabaseTable(tableName = ChatMessage.TABLE_CHAT_MESSAGE)
public class ChatMessage {
    public static final String TABLE_CHAT_MESSAGE = "chat_message";
    public static final String FIELD_MESSAGE_ID = "message_id";
    public static final String FIELD_FROM_USER_ID = "sender";
    public static final String FIELD_TO_USER_ID = "receiver";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_TIME = "time";

    @DatabaseField(generatedId = true, columnName = FIELD_MESSAGE_ID)
    private int messageId;

    @DatabaseField(columnName=FIELD_FROM_USER_ID, foreign = true, foreignAutoRefresh = true)
    private User sender;

    @DatabaseField(columnName=FIELD_TO_USER_ID, foreign = true, foreignAutoRefresh = true)
    private User receiver;

    @DatabaseField(columnName=FIELD_CONTENT, canBeNull = false)
    private String content;

    @DatabaseField(columnName=FIELD_TIME, canBeNull = false, dataType = DataType.TIME_STAMP)
    private Timestamp time;

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

    public Timestamp getTime() {
        return time;
    }

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.ofInstant(time.toInstant(), TimeZone.getDefault().toZoneId());
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setTime(LocalDateTime time) {
        this.time = Timestamp.valueOf(time);
    }
}
