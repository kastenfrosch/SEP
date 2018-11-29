package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    private static DateTimeFormatter simpleDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String toSimpleString(LocalDateTime ldt) {
        if(ldt == null) {
            return "0000-00-00 00:00:00";
        }
        return ldt.format(simpleDtf);
    }
}
