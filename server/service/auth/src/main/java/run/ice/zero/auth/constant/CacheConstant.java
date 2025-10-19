package run.ice.zero.auth.constant;

public class CacheConstant {

    private static final String DELIMITER = ":";

    private static final String PREFIX = "auth" + DELIMITER;

    public static final String PERM = PREFIX + "perm" + DELIMITER;

    public static final String ROLE = PREFIX + "role" + DELIMITER;

    public static final String USER = PREFIX + "user" + DELIMITER;

    public static final String GROUP = PREFIX + "group" + DELIMITER;

    private CacheConstant() {
    }

}
