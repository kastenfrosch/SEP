package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    private static DateTimeFormatter simpleDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String toSimpleString(LocalDateTime ldt) {
        return ldt.format(simpleDtf);
    }
}
