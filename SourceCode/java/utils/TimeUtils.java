package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    private static DateTimeFormatter simpleDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter simpleDateDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String toSimpleString(LocalDateTime ldt) {
        if(ldt == null) {
            return "0000-00-00 00:00:00";
        }
        return ldt.format(simpleDtf);
    }

    public static String toSimpleDateString(LocalDateTime ldt) {
        if(ldt == null) {
            return "0000-00-00";
        }
        return ldt.format(simpleDateDtf);
    }
}
