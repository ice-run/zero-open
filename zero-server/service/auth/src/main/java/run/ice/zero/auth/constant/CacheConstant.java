package run.ice.zero.auth.constant;

public class CacheConstant {

    private static final String DELIMITER = ":";

    private static final String PREFIX = "auth" + DELIMITER;

    public static final String PERMISSION = PREFIX + "permission" + DELIMITER;

    public static final String ROLE = PREFIX + "role" + DELIMITER;

    public static final String USER = PREFIX + "user" + DELIMITER;

    private CacheConstant() {
    }

}
