package run.ice.zero.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Hooks;
import run.ice.zero.common.constant.AppConstant;

/**
 * @author DaoDao
 */
@EnableScheduling
//@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {AppConstant.BASE_PACKAGE})
public class GatewayApplication {

    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(GatewayApplication.class, args);
    }

}
