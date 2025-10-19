package run.ice.zero.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import run.ice.zero.common.constant.AppConstant;

/**
 * @author DaoDao
 */
@SpringBootApplication(scanBasePackages = {AppConstant.BASE_PACKAGE})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
