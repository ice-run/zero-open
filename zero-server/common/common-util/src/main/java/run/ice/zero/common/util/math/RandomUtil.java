package run.ice.zero.common.util.math;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RandomUtil {

    /**
     * 生成一串数字字符串，32 位
     *
     * @return 14 位日期时间字符串 + 9 位 nano seconds 纳秒 + 9 位随机数
     */
    public static String time() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String time = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String nano = String.format("%09d", localDateTime.getNano());
        String random = number(9);
        return time + nano + random;
    }

    public static String number(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(number());
        }
        return sb.toString();
    }

    public static String number() {
        String str = "0123456789";
        int index = (int) (Math.random() * str.length());
        return str.substring(index, index + 1);
    }

    public static String hex(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(hex());
        }
        return sb.toString();
    }

    public static String hex() {
        String str = "0123456789ABCDEF";
        int index = (int) (Math.random() * str.length());
        return str.substring(index, index + 1);
    }

}
