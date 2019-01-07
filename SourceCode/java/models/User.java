package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=User.TABLE_USER)
public class User {
    public static final String TABLE_USER = "user";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PERSON_ID = "person_id";
    public static final String FIELD_PASSWORD_HASH = "password_hash";
    public static final String FIELD_SALT = "salt";
    public static final String FIELD_LAST_TAB = "last_tab";
    public static final String FIELD_LAST_ITEM = "last_item";
    public static final String FIELD_MAIL_IMAP_HOST = "mail_imap_host";
    public static final String FIELD_MAIL_SMTP_HOST = "mail_smtp_host";
    public static final String FIELD_MAIL_USER = "mail_user";
    public static final String FIELD_MAIL_PASSWORD = "mail_password";
    public static final String FIELD_MAIL_IMAP_PORT = "mail_imap_port";
    public static final String FIELD_MAIL_SMTP_PORT = "mail_smtp_port";
    public static final String FIELD_MAIL_AUTH_ENABLED = "mail_auth";
    public static final String FIELD_MAIL_TLS_ENABLED = "mail_tls";


    @DatabaseField(id = true, columnName = FIELD_USERNAME)
    private String username;

    @DatabaseField(columnName = FIELD_PASSWORD_HASH)
    private String passwordHash;


    //Note: The column defintions are currently constants. I have not found a way to have them generated yet.
    @DatabaseField(foreign=true, columnName=FIELD_PERSON_ID, foreignAutoRefresh = true, foreignAutoCreate = true,
    columnDefinition = "integer not null references person(person_id) on delete restrict", canBeNull = false)
    private Person person;

    @DatabaseField(columnName=FIELD_SALT, canBeNull = false)
    private String salt;

    @DatabaseField(columnName=FIELD_LAST_TAB)
    private String lastTab;

    @DatabaseField(columnName=FIELD_LAST_ITEM)
    private String lastItem;

    @DatabaseField(columnName = FIELD_MAIL_IMAP_HOST)
    private String mailImapHost;

    @DatabaseField(columnName = FIELD_MAIL_SMTP_HOST)
    private String mailSmtpHost;

    @DatabaseField(columnName = FIELD_MAIL_USER)
    private String mailUser;

    @DatabaseField(columnName = FIELD_MAIL_PASSWORD)
    private String mailPassword;

    @DatabaseField(columnName = FIELD_MAIL_IMAP_PORT)
    private int mailImapPort;

    @DatabaseField(columnName = FIELD_MAIL_SMTP_PORT)
    private int mailSmtpPort;

    @DatabaseField(columnName = FIELD_MAIL_AUTH_ENABLED)
    private boolean mailAuthEnabled;

    @DatabaseField(columnName = FIELD_MAIL_TLS_ENABLED)
    private boolean mailTLSEnabled;



    public User() {}


    public User(String username, String passwordHash, String salt, Person person) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.person = person;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getLastTab() {
        return lastTab;
    }

    public void setLastTab(String lastTab) {
        this.lastTab = lastTab;
    }

    public String getLastItem() {
        return lastItem;
    }

    public void setLastItem(String lastItem) {
        this.lastItem = lastItem;
    }

    public String getMailImapHost() {
        return mailImapHost;
    }

    public void setMailImapHost(String mailImapHost) {
        this.mailImapHost = mailImapHost;
    }

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public int getMailImapPort() {
        return mailImapPort;
    }

    public void setMailImapPort(int mailImapPort) {
        this.mailImapPort = mailImapPort;
    }

    public boolean isMailAuthEnabled() {
        return mailAuthEnabled;
    }

    public void setMailAuthEnabled(boolean mailAuthEnabled) {
        this.mailAuthEnabled = mailAuthEnabled;
    }

    public boolean isMailTLSEnabled() {
        return mailTLSEnabled;
    }

    public void setMailTLSEnabled(boolean mailTLSEnabled) {
        this.mailTLSEnabled = mailTLSEnabled;
    }

    public int getMailSmtpPort() {
        return mailSmtpPort;
    }

    public void setMailSmtpPort(int mailSmtpPort) {
        this.mailSmtpPort = mailSmtpPort;
    }

    public String getMailSmtpHost() {
        return mailSmtpHost;
    }

    public void setMailSmtpHost(String mailSmtpHost) {
        this.mailSmtpHost = mailSmtpHost;
    }

    @Override
    public String toString() {
        return this.getUsername();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof User)) {
            return false;
        }

        return this.username.equals(((User) other).getUsername());
    }


}
