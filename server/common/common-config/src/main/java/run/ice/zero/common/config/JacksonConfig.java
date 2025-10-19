package run.ice.zero.common.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author DaoDao
 */
@Configuration
public class JacksonConfig {

    /**
     * 解决 Long 类型序列化精度丢失问题
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
//    }

}
