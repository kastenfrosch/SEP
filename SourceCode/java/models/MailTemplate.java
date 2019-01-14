package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = MailTemplate.TABLE_MAIL_TEMPLATE)
public class MailTemplate {
    public static final String TABLE_MAIL_TEMPLATE = "mail_template";
    public static final String FIELD_TEMPLATE_ID = "template_id";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_CONTENT = "content";

    @DatabaseField(columnName = FIELD_TEMPLATE_ID, generatedId = true)
    private int templateId;

    @DatabaseField(columnName = FIELD_SUBJECT)
    private String subject;

    @DatabaseField(columnName = FIELD_CONTENT)
    private String content;

    public MailTemplate(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    public MailTemplate() {

    }

    public int getTemplateId() {
        return templateId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof MailTemplate)) {
            return false;
        }

        return ((MailTemplate) obj).getTemplateId() == getTemplateId();
    }

    @Override
    public String toString() {
        return getSubject();
    }
}
