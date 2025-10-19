package run.ice.zero.common.util.security;

import run.ice.zero.common.annotation.Mask.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 掩码工具类
 *
 * @author DaoDao
 */
public class MaskUtil {

    /**
     * 对字符串进行掩码处理
     *
     * @param type   掩码类型
     * @param origin 原始字符串
     * @return 掩码后的字符串
     */
    public static String mask(Type type, String origin) {
        if (null == origin || origin.isEmpty()) {
            return origin;
        }
        String mask;
        if (type.equals(Type.NAME)) {
            mask = nameMask(origin);
            return mask;
        }
        Matcher matcher = Pattern.compile(type.getRegex()).matcher(origin);
        mask = matcher.replaceAll(type.getReplace());
        return mask;
    }

    /**
     * 对姓名进行掩码处理
     *
     * @param origin 原始姓名
     * @return 掩码后的姓名
     */
    private static String nameMask(String origin) {
        if (null == origin || origin.isEmpty()) {
            return origin;
        }
        int length = origin.length();
        String mask;
        if (1 == length) {
            mask = origin;
        } else if (2 == length) {
            mask = "*" + origin.charAt(length - 1);
        } else {
            mask = origin.charAt(0) + "*".repeat(Math.min(length - 2, 4)) + origin.charAt(length - 1);
        }
        return mask;
    }

}
