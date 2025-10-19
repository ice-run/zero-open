package run.ice.zero.common.util.logger;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志工具类
 *
 * @author DaoDao
 */
@Slf4j
public class LoggerUtil {

    /**
     * 日志脱敏
     * o 数据对象类和属性上需要由 @Sensitive 注解才有效果
     *
     * @param o Object
     * @return String
     */
    public static String sensitive(Object o) {
        return jacksonSensitive(o);
    }

    /**
     * 正则脱敏
     * o 数据对象先按照 @Sensitive 注解脱敏，再按照给定的规则二次脱敏
     *
     * @param o       Object
     * @param regex   String
     * @param replace String
     * @return String
     */
    public static String sensitive(Object o, String regex, String replace) {
        LoggerRule loggerRule = new LoggerRule();
        loggerRule.setRegex(regex);
        loggerRule.setReplace(replace);
        return sensitive(o, loggerRule);
    }

    /**
     * 正则脱敏
     * o 数据对象先按照 @Sensitive 注解脱敏，再按照给定的规则二次脱敏
     *
     * @param o          Object
     * @param loggerRule LoggerRule
     * @return String
     */
    public static String sensitive(Object o, LoggerRule loggerRule) {
        List<LoggerRule> loggerRules = new ArrayList<>();
        loggerRules.add(loggerRule);
        return sensitive(o, loggerRules);
    }

    /**
     * 正则脱敏
     * o 数据对象先按照 @Sensitive 注解脱敏，再按照给定的规则二次脱敏
     *
     * @param o           Object
     * @param loggerRules List<LoggerRule>
     * @return String
     */
    public static String sensitive(Object o, List<LoggerRule> loggerRules) {
        if (null == o) {
            return null;
        }
        String message;
        if (o instanceof String) {
            message = (String) o;
            if (message.isEmpty()) {
                return message;
            }
        } else {
            message = sensitive(o);
        }
        for (LoggerRule loggerRule : loggerRules) {
            String regex = loggerRule.getRegex();
            String replace = loggerRule.getReplace();
            if (null != regex && !regex.isEmpty() && null != replace && !replace.isEmpty()) {
                Matcher matcher = Pattern.compile(regex).matcher(message);
                message = matcher.replaceAll(replace);
            }
        }
        return message;
    }

    /**
     * Jackson 脱敏
     *
     * @param o Object
     * @return String
     */
    private static String jacksonSensitive(Object o) {
        String s = null;
        try {
            s = new ObjectMapper().writeValueAsString(o);
        } catch (JacksonException e) {
            log.error(e.getMessage(), e);
        }
        return s;
    }

}
