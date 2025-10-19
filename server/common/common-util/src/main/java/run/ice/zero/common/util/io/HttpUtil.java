package run.ice.zero.common.util.io;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Http 工具类
 *
 * @author DaoDao
 */
@Slf4j
public class HttpUtil {

    public static Map<String, String[]> parameters(String url) {
        Map<String, String[]> parameters = new HashMap<>();
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            return parameters;
        }
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return parameters;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = idx > 0 ? pair.substring(0, idx) : pair;
            String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
            if (parameters.containsKey(key)) {
                String[] values = parameters.get(key);
                String[] newValues = new String[values.length + 1];
                System.arraycopy(values, 0, newValues, 0, values.length);
                newValues[values.length] = value;
                parameters.put(key, newValues);
            } else {
                parameters.put(key, new String[]{value});
            }
        }
        return parameters;
    }

}
