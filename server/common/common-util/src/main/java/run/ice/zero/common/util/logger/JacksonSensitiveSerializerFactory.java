package run.ice.zero.common.util.logger;

import lombok.extern.slf4j.Slf4j;
import run.ice.zero.common.annotation.Sensitive;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.SerializerFactoryConfig;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.BeanSerializerBuilder;
import tools.jackson.databind.ser.BeanSerializerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Jackson 脱敏序列化工厂
 *
 * @author DaoDao
 */
@Slf4j
public class JacksonSensitiveSerializerFactory extends BeanSerializerFactory {

    public JacksonSensitiveSerializerFactory() {
        this(null);
    }

    public JacksonSensitiveSerializerFactory(SerializerFactoryConfig config) {
        super(config);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void processViews(SerializationConfig config, BeanSerializerBuilder builder) {
        super.processViews(config, builder);
        List<BeanPropertyWriter> originalWriters = builder.getProperties();
        List<BeanPropertyWriter> writers = new ArrayList<>();
        for (BeanPropertyWriter writer : originalWriters) {
            Sensitive sensitive = writer.getAnnotation(Sensitive.class);
            if (null != sensitive) {
                Sensitive.Type type = sensitive.type();
                final String[] regexArray = {sensitive.regex()};
                final String[] replaceArray = {sensitive.replace()};
                writer.assignSerializer(new ValueSerializer() {
                    @Override
                    public void serialize(Object value, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
                        try {
                            String regex;
                            String replace;
                            if (Sensitive.Type.CUSTOM.equals(type)) {
                                regex = regexArray[0];
                                replace = replaceArray[0];
                            } else {
                                regex = type.getRegex();
                                replace = type.getReplace();
                            }
                            if (null != regex && replace != null && !regex.isEmpty() && value instanceof String v) {
                                jsonGenerator.writeString(v.replaceAll(regex, replace));
                            }
                        } catch (Exception e) {
                            log.warn("JacksonSensitiveSerializer has no field {}", value);
                        }
                    }
                });
            }
            writers.add(writer);
        }
        builder.setProperties(writers);
    }

}
