package run.ice.zero.common.util.logger;

import lombok.Data;

/**
 * app.logger.config.rules=[{"regex":"\\\\?\\\\?\\\\?","replace":"!!!"},{"regex":"猫猫","replace":"狗狗"},{"regex":"abc","replace":"123"}]
 *
 * @author DaoDao
 */
@Data
public class LoggerRule {

    private String regex;

    private String replace;

}
