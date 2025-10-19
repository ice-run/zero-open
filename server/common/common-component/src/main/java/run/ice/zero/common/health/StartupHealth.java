package run.ice.zero.common.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public interface StartupHealth {

    default Health health() {
        Status status = Status.UP;
        Map<String, Object> details = new HashMap<>();
        details.put("startup", "success");
        return new Health.Builder(status, details).build();
    }

}
