package models;

import com.j256.ormlite.field.DatabaseField;
import utils.HashUtils;

import javax.print.DocFlavor;

public class InviteCode {
    public static final String FIELD_USER_NAME = "used_by";
    public static final String FIELD_CODE = "code";


    @DatabaseField(columnName = FIELD_USER_NAME, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "varchar not null references \"user\"(username)")
    private User usedBy;

    @DatabaseField(columnName = FIELD_CODE, canBeNull = false, id = true)
    private static String code;

    public InviteCode() {
        this.code = HashUtils.toHex(HashUtils.getRandomSalt());
    }

    public User getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(User usedBy) {
        this.usedBy = usedBy;
    }

    public static String getCode() {
        return code;
    }
}
