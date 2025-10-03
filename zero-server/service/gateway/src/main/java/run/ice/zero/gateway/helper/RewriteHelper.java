package run.ice.zero.gateway.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.error.AppException;
import run.ice.zero.gateway.config.AppConfig;
import run.ice.zero.gateway.constant.FilterConstant;
import run.ice.zero.gateway.error.GatewayError;
import run.ice.zero.common.util.security.AesUtil;
import run.ice.zero.common.util.security.HashUtil;
import run.ice.zero.common.util.security.RsaUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Slf4j
@Component
public class RewriteHelper {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AppConfig appConfig;

    @SuppressWarnings("unchecked")
    public Mono<Void> filterX(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        String contentType = exchange.getAttribute(FilterConstant.REQUEST_CONTENT_TYPE);
        if (null == contentType || !contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return chain.filter(exchange);
        }

        /*
         * 1. 尝试获取 ServerWebExchange 中缓存的 request body
         */
        byte[] requestBodyBytes = exchange.getAttribute(FilterConstant.REQUEST_BODY_BYTES);
        /*
         * 2. 如果已经缓存过，略过
         */
        if (null != requestBodyBytes) {
            return chain.filter(exchange);
        }
        /*
         * 3. 如果没有缓存过，获取字节数组存入 ServerWebExchange 的自定义属性中
         * 此处使用 spring 5 中的核心 WebFlux 中的工具类来收集数据流，避免数据断流或信息不完整
         */
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).defaultIfEmpty(new byte[0])
                .doOnNext(this::log)
                .doOnNext(bytes -> exchange.getAttributes().put(FilterConstant.REQUEST_BODY_BYTES, bytes))
                .doOnNext(bytes -> {

                    if (bytes.length > appConfig.getRequestBodySizeLimit()) {
                        throw new AppException(GatewayError.REQUEST_BODY_TOO_LARGE, String.valueOf(bytes.length));
                    }

                    try {
                        LinkedHashMap<String, Object> requestBodyMap = objectMapper.readValue(bytes, LinkedHashMap.class);
                        exchange.getAttributes().put(FilterConstant.REQUEST_BODY_MAP, requestBodyMap);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.ERROR, e.getMessage());
                    }
                })
                .doOnNext(bytes -> {
                    LinkedHashMap<String, Object> requestBodyMap = exchange.getAttribute(FilterConstant.REQUEST_BODY_MAP);
                    byte[] newBytes;
                    try {
                        newBytes = objectMapper.writeValueAsBytes(requestBodyMap);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.ERROR, e.getMessage());
                    }
                    exchange.getAttributes().put(FilterConstant.REQUEST_BODY_BYTES, newBytes);
                })
                .then(chain.filter(exchange));
    }

    @SuppressWarnings("unchecked")
    public Mono<Void> filterY(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        String contentType = exchange.getAttribute(FilterConstant.REQUEST_CONTENT_TYPE);
        if (null == contentType || !contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return chain.filter(exchange);
        }

        /*
         * 1. 尝试获取 ServerWebExchange 中缓存的 request body
         */
        byte[] requestBodyBytes = exchange.getAttribute(FilterConstant.REQUEST_BODY_BYTES);
        /*
         * 2. 如果已经缓存过，略过
         */
        if (null != requestBodyBytes) {
            return chain.filter(exchange);
        }
        /*
         * 3. 如果没有缓存过，获取字节数组存入 ServerWebExchange 的自定义属性中
         * 此处使用 spring 5 中的核心 WebFlux 中的工具类来收集数据流，避免数据断流或信息不完整
         */
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).defaultIfEmpty(new byte[0])
                .doOnNext(this::log)
                .doOnNext(bytes -> exchange.getAttributes().put(FilterConstant.REQUEST_BODY_BYTES, bytes))
                .doOnNext(bytes -> {
                    try {
                        /*
                         * 4. 将 json 反序列化一次，存入 ServerWebExchange 的自定义属性中，方便后续的 “对象操作”
                         * Tips：此处不可直接强制反序列化 Request ，因为 Request 中带有一个 T 的未知确切类型的属性 params
                         * 会导致反序列化失败
                         * 尝试使用 LinkedHashMap 去接收 json 反序列化的键值
                         * 此后的 验签加签，追加修改属性，都是基于 LinkedHashMap 的第一级属性操作
                         * 而原 BaseParams，不做任何操作，原样序列化为 json ，提交给路由转发 http 请求
                         */
                        /// Request request = objectMapper.readValue(bytes, Request.class);
                        LinkedHashMap<Object, Object> requestBodyMap = objectMapper.readValue(bytes, LinkedHashMap.class);

                        if (bytes.length > appConfig.getRequestBodySizeLimit()) {
                            throw new AppException(GatewayError.REQUEST_BODY_TOO_LARGE, String.valueOf(bytes.length));
                        }

                        Object paramObject = requestBodyMap.get("param");
                        if (null == paramObject) {
                            throw new AppException(GatewayError.REQUEST_BODY_PARAM_CAN_NOT_BE_NULL);
                        }
                        String xSign = exchange.getAttribute(AppConstant.X_SIGN);
                        String sign;
                        String security = exchange.getAttribute(AppConstant.X_SECURITY);
                        String paramCipher = null;
                        String paramPlains = null;
                        JsonNode paramNode;
                        if (null != security && !security.isEmpty()) {
                            try {
                                paramCipher = (String) paramObject;
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                throw new AppException(GatewayError.REQUEST_BODY_PARAM_CIPHER_ERROR, e.getMessage());
                            }
                        }
                        if (AppConstant.AES.equals(security)) {
                            String aesKey = exchange.getAttribute(FilterConstant.AES_KEY);
                            String aesIv = exchange.getAttribute(FilterConstant.AES_IV);
                            sign = HashUtil.sha256(paramCipher);
                            if (null != xSign && !xSign.isEmpty()) {
                                if (!xSign.equalsIgnoreCase(sign)) {
                                    throw new AppException(GatewayError.X_SIGN_ERROR, xSign);
                                }
                            }

                            try {
                                paramPlains = AesUtil.decrypt(aesKey, aesIv, paramCipher, AesUtil.Encode.BASE64);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                throw new AppException(GatewayError.REQUEST_BODY_PARAM_CIPHER_DECRYPT_EXCEPTION, e.getMessage());
                            }

                        }

                        if (null != security && !security.isEmpty()) {
                            try {
                                paramNode = objectMapper.readTree(paramPlains);
                            } catch (JacksonException e) {
                                log.error(e.getMessage(), e);
                                throw new AppException(GatewayError.REQUEST_BODY_PARAM_PLAINS_DESERIALIZE_ERROR, e.getMessage());
                            }
                            if (paramNode.isArray()) {
                                ArrayList<Object> paramData = objectMapper.readValue(paramPlains, ArrayList.class);
                                requestBodyMap.put("param", paramData);
                            } else if (paramNode.isObject()) {
                                LinkedHashMap<String, Object> paramData = objectMapper.readValue(paramPlains, LinkedHashMap.class);
                                requestBodyMap.put("param", paramData);
                            } else {
                                throw new AppException(GatewayError.REQUEST_BODY_SERIALIZE_ERROR);
                            }
                        }

                        exchange.getAttributes().put(FilterConstant.REQUEST_BODY_MAP, requestBodyMap);

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.REQUEST_BODY_REWRITE_ERROR, e.getMessage());
                    }
                })
                .doOnNext(bytes -> {
                    LinkedHashMap<Object, Object> requestBodyMap = exchange.getAttribute(FilterConstant.REQUEST_BODY_MAP);
                    byte[] newBytes;
                    try {
                        newBytes = objectMapper.writeValueAsBytes(requestBodyMap);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.REQUEST_BODY_SERIALIZE_ERROR, e.getMessage());
                    }
                    if (null != newBytes) {
                        exchange.getAttributes().put(FilterConstant.REQUEST_BODY_BYTES, newBytes);
                    } else {
                        throw new AppException(GatewayError.REQUEST_BODY_SERIALIZE_ERROR);
                    }
                })
                .then(chain.filter(exchange));
    }

    @SuppressWarnings("unchecked")
    public Mono<Void> filterZ(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        String contentType = exchange.getAttribute(FilterConstant.REQUEST_CONTENT_TYPE);
        if (null == contentType || !contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return chain.filter(exchange);
        }

        /*
         * 1. 尝试获取 ServerWebExchange 中缓存的 request body
         */
        byte[] requestBodyBytes = exchange.getAttribute(FilterConstant.REQUEST_BODY_BYTES);
        /*
         * 2. 如果已经缓存过，略过
         */
        if (null != requestBodyBytes) {
            return chain.filter(exchange);
        }
        /*
         * 3. 如果没有缓存过，获取字节数组存入 ServerWebExchange 的自定义属性中
         * 此处使用 spring 5 中的核心 WebFlux 中的工具类来收集数据流，避免数据断流或信息不完整
         */
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).defaultIfEmpty(new byte[0])
                .doOnNext(this::log)
                .doOnNext(bytes -> exchange.getAttributes().put(FilterConstant.REQUEST_BODY_BYTES, bytes))
                .doOnNext(bytes -> {

                    if (bytes.length > appConfig.getRequestBodySizeLimit()) {
                        throw new AppException(GatewayError.REQUEST_BODY_TOO_LARGE, String.valueOf(bytes.length));
                    }

                    LinkedHashMap<String, Object> requestBodyMap;
                    try {
                        requestBodyMap = objectMapper.readValue(bytes, LinkedHashMap.class);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.REQUEST_BODY_DESERIALIZE_ERROR, e.getMessage());
                    }

                    if (!requestBodyMap.containsKey("param") || null == requestBodyMap.get("param")) {
                        throw new AppException(GatewayError.REQUEST_BODY_REQUIRED_PARAM_PROPERTIES);
                    }

                    String xClient = exchange.getAttribute(AppConstant.X_CLIENT);
                    String xSecurity = exchange.getAttribute(AppConstant.X_SECURITY);
                    String xSign = exchange.getAttribute(AppConstant.X_SIGN);
                    String serverPrivateKey = exchange.getAttribute(FilterConstant.SERVER_PRIVATE_KEY);
                    String clientPublicKey = exchange.getAttribute(FilterConstant.CLIENT_PUBLIC_KEY);

                    if (AppConstant.RSA.equals(xSecurity)) {
                        assert xClient != null;
                        String param;
                        try {
                            param = ((String) requestBodyMap.get("param"));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            throw new AppException(GatewayError.REQUEST_BODY_PARAM_CIPHER_ERROR, e.getMessage());
                        }

                        boolean verify = false;
                        try {
                            if (null != xSign && !xSign.isEmpty()) {
                                assert clientPublicKey != null;
                                verify = RsaUtil.verify(clientPublicKey, param, xSign);
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            throw new AppException(GatewayError.X_SIGN_VERIFY_EXCEPTION, e.getMessage());
                        }
                        if (!verify) {
                            throw new AppException(GatewayError.X_SIGN_VERIFY_ERROR, xSign);
                        }

                        try {
                            assert serverPrivateKey != null;
                            param = RsaUtil.decrypt(serverPrivateKey, param);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            throw new AppException(GatewayError.REQUEST_BODY_PARAM_CIPHER_DECRYPT_EXCEPTION, e.getMessage());
                        }

                        JsonNode paramNode;
                        try {
                            paramNode = objectMapper.readTree(param);
                        } catch (JacksonException e) {
                            log.error(e.getMessage(), e);
                            log.error("param: {}", param);
                            throw new AppException(GatewayError.REQUEST_BODY_PARAM_PLAINS_DESERIALIZE_ERROR, e.getMessage());
                        }
                        if (paramNode.isArray()) {
                            ArrayList<Object> paramData;
                            try {
                                paramData = objectMapper.readValue(param, ArrayList.class);
                            } catch (JacksonException e) {
                                log.error(e.getMessage(), e);
                                throw new AppException(GatewayError.REQUEST_BODY_PARAM_PLAINS_DESERIALIZE_ERROR, e.getMessage());
                            }
                            requestBodyMap.put("param", paramData);
                        } else if (paramNode.isObject()) {
                            LinkedHashMap<String, Object> paramData;
                            try {
                                paramData = objectMapper.readValue(param, LinkedHashMap.class);
                            } catch (JacksonException e) {
                                log.error(e.getMessage(), e);
                                throw new AppException(GatewayError.REQUEST_BODY_PARAM_PLAINS_DESERIALIZE_ERROR, e.getMessage());
                            }
                            requestBodyMap.put("param", paramData);
                        } else {
                            throw new AppException(GatewayError.REQUEST_BODY_SERIALIZE_ERROR);
                        }
                    }

                    exchange.getAttributes().put(FilterConstant.REQUEST_BODY_MAP, requestBodyMap);
                })
                .doOnNext(bytes -> {
                    LinkedHashMap<String, Object> requestBodyMap = exchange.getAttribute(FilterConstant.REQUEST_BODY_MAP);
                    byte[] newBytes;
                    try {
                        newBytes = objectMapper.writeValueAsBytes(requestBodyMap);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.REQUEST_REWRITE_ERROR, e.getMessage());
                    }
                    exchange.getAttributes().put(FilterConstant.REQUEST_BODY_BYTES, newBytes);
                })
                .then(chain.filter(exchange));
    }

    private void log(byte[] body) {
        int limit = 1024 * 10;
        String requestBody = body.length > limit ? (new String(body, 0, limit) + " ...") : new String(body);
        log.info("S < : {}", requestBody);
    }

}
