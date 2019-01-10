package utils.settings;

public enum Setting {
    DB_HOST("db_host", "localhost"),
    DB_PORT("db_port", "5432"),
    DB_USER("db_user", "sep"),
    DB_PASSWORD("db_password", "ayy1mao"),
    DB_DB("db_db", "sep2"),
    SYS_MAIL_HOST("sys_mail_host", "localhost"),
    SYS_MAIL_SMTP_PORT("sys_mail_smtp_port", "587"),
    SYS_MAIL_USER("sys_mail_user", "sep@localhost"),
    SYS_MAIL_PASSWORD("sys_mail_password", "secret");

    private String key, defaultValue;
    Setting(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return this.key;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }
}
