package run.ice.zero.common.model;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * 模型序列化接口
 *
 * @author DaoDao
 */
public interface Serializer extends Serializable {

    ObjectMapper objectMapper = new ObjectMapper();

    default String toJson() {
        String json;
        try {
            json = objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    static String toJson(Object o) {
        String json;
        try {
            json = objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    /**
     * 此方法不支持泛型，泛型请使用 {@link #ofJson(String, TypeReference)}
     *
     * @param <T>  T
     * @param json json
     * @return T
     */
    @SuppressWarnings("unchecked")
    default <T> T ofJson(String json) {
        T t;
        Class<? extends Serializer> clazz = this.getClass();
        try {
            t = (T) objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    static <T> T ofJson(String json, TypeReference<T> typeReference) {
        T t;
        try {
            t = objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return t;
    }

}
