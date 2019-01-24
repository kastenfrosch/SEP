import org.junit.Test;
import utils.HashUtils;
import utils.TimeUtils;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class UtilsTest {
    @Test
    public void testDateFormatter() {
        String str = "2018-01-01";
        LocalDate ld  = TimeUtils.localDateFromString(str);
        assertEquals(ld.getYear(), 2018);
        assertEquals(ld.getMonth(), Month.JANUARY);
        assertEquals(ld.getDayOfMonth(), 1);
    }

    @Test
    public void testAES() {
        String clear = "foo", key = "bar";
        String encrypted = HashUtils.encryptAES(clear, key);
        try {
            String decrypted = HashUtils.decryptAES(encrypted, key);
            assertEquals(decrypted, clear);
        } catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
