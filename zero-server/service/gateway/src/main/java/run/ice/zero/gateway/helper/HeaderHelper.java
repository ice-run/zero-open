package run.ice.zero.gateway.helper;

import io.opentelemetry.api.trace.Span;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.error.AppException;
import run.ice.zero.gateway.config.AppConfig;
import run.ice.zero.gateway.constant.FilterConstant;
import run.ice.zero.gateway.error.GatewayError;
import run.ice.zero.common.util.security.AesUtil;
import run.ice.zero.common.util.security.RsaUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class HeaderHelper {

    @Resource
    private AppConfig appConfig;

    @Resource
    private FilterHelper filterHelper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 开发网关
     *
     * @param exchange ServerWebExchange
     * @param chain    GatewayFilterChain
     * @return Mono
     */
    public Mono<Void> filterX(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        log(request);

        HttpHeaders headers = request.getHeaders();

        String xTrace = headers.getFirst(AppConstant.X_TRACE);
        if (Span.current().getSpanContext().isValid()) {
            String traceId = Span.current().getSpanContext().getTraceId();
            exchange.getAttributes().put(AppConstant.X_TRACE, traceId);
        } else if (null != xTrace) {
            exchange.getAttributes().put(AppConstant.X_TRACE, xTrace);
        }

        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);

        if (null == contentType || !contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE)) {
            exchange.getAttributes().put(FilterConstant.NO_FILTER, Boolean.TRUE);
            return chain.filter(exchange);
        }

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

        }));

    }

    /**
     * 新版 web 和 app 网关
     */
    public Mono<Void> filterY(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        log(request);

        HttpMethod method = request.getMethod();

        HttpHeaders headers = request.getHeaders();

        Map<String, String> requestHeaderMap = (Map<String, String>) exchange.getAttributes().get(FilterConstant.REQUEST_HEADER_MAP);
        if (null == requestHeaderMap) {
            requestHeaderMap = new HashMap<>();
            exchange.getAttributes().put(FilterConstant.REQUEST_HEADER_MAP, requestHeaderMap);
        }

        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        if (null != contentType) {
            exchange.getAttributes().put(FilterConstant.REQUEST_CONTENT_TYPE, contentType);
        }

        String xClient = headers.getFirst(AppConstant.X_CLIENT);
        if (null != xClient) {
            exchange.getAttributes().put(AppConstant.X_CLIENT, xClient);
        }

        String xTrace = headers.getFirst(AppConstant.X_TRACE);
        if (Span.current().getSpanContext().isValid()) {
            String traceId = Span.current().getSpanContext().getTraceId();
            exchange.getAttributes().put(AppConstant.X_TRACE, traceId);
        } else if (null != xTrace) {
            exchange.getAttributes().put(AppConstant.X_TRACE, xTrace);
        }

        String xTime = headers.getFirst(AppConstant.X_TIME);
        if (null != xTime) {
            exchange.getAttributes().put(AppConstant.X_TIME, xTime);
        }

        String xSecurity = headers.getFirst(AppConstant.X_SECURITY);
        if (null != xSecurity) {
            exchange.getAttributes().put(AppConstant.X_SECURITY, xSecurity);
        }

        String xHash = headers.getFirst(AppConstant.X_HASH);
        if (null != xHash) {
            exchange.getAttributes().put(AppConstant.X_HASH, xHash);
        }

        /*
         * oauth2 token
         */
        String oAuth2Token = oAuth2Token(headers);
        if (null != oAuth2Token) {
            exchange.getAttributes().put(FilterConstant.OAUTH2_TOKEN, oAuth2Token);
        }

        if (HttpMethod.POST.equals(method)) {
            if (null == contentType || contentType.isEmpty()) {
                throw new AppException(GatewayError.REQUIRED_CONTENT_TYPE, contentType);
            }
            if ((null == xSecurity || xSecurity.isEmpty())) {
                throw new AppException(GatewayError.REQUIRED_X_SECURITY, xSecurity);
            }
            if (!AppConstant.AES.equals(xSecurity)) {
                throw new AppException(GatewayError.X_SECURITY_ERROR, xSecurity);
            }
            if (null == xClient || xClient.isEmpty()) {
                throw new AppException(GatewayError.REQUIRED_X_CLIENT, xClient);
            }
            if ((null == xHash || xHash.isEmpty()) && contentType.toLowerCase().contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                throw new AppException(GatewayError.REQUIRED_X_HASH, xHash);
            }
            if (null == xTrace || xTrace.isEmpty()) {
                throw new AppException(GatewayError.REQUIRED_X_TRACE, xTrace);
            } else {
                checkXTrace(xTrace);
            }
            if (null == xTime || xTime.isEmpty()) {
                throw new AppException(GatewayError.REQUIRED_X_TIME, xTime);
            } else {
                checkXTime(xTime);
            }
        }

        if (AppConstant.AES.equals(xSecurity)) {
            Mono<LinkedHashMap<String, Object>> aesData = filterHelper.aesSelect(xClient);
            Map<String, String> finalRequestHeaderMap = requestHeaderMap;
            final String[] finalXHash = {xHash};
            return aesData.flatMap(data -> {
                if (data == null) {
                    return Mono.error(new AppException(GatewayError.FIND_CLIENT_AES_KEY_ERROR, xClient));
                } else {
                    String aesKey = data.get("aesKey").toString();
                    String aesIv = data.get("aesIv").toString();
                    exchange.getAttributes().put(FilterConstant.AES_KEY, aesKey);
                    exchange.getAttributes().put(FilterConstant.AES_IV, aesIv);

                    if (contentType != null && contentType.toLowerCase().contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                        if (finalXHash[0] == null || finalXHash[0].isEmpty()) {
                            return Mono.error(new AppException(GatewayError.REQUIRED_X_HASH, finalXHash[0]));
                        }
                        try {
                            finalXHash[0] = AesUtil.decrypt(aesKey, aesIv, finalXHash[0], AesUtil.Encode.BASE64);
                        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException |
                                 IllegalBlockSizeException |
                                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                            log.error(e.getMessage(), e);
                            return Mono.error(new AppException(GatewayError.REQUEST_HEADER_HASH_CIPHER_DECRYPT_EXCEPTION, e));
                        }
                        exchange.getAttributes().put(AppConstant.X_HASH, finalXHash[0]);
                        finalRequestHeaderMap.put(AppConstant.X_HASH, finalXHash[0]);
                    }

                    return chain.filter(exchange);
                }
            });
        }

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

        }));
    }


    public Mono<Void> filterZ(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        log(request);

        HttpMethod method = request.getMethod();

        HttpHeaders headers = request.getHeaders();

        Map<String, String> requestHeaderMap = (Map<String, String>) exchange.getAttributes().get(FilterConstant.REQUEST_HEADER_MAP);
        if (null == requestHeaderMap) {
            requestHeaderMap = new HashMap<>();
            exchange.getAttributes().put(FilterConstant.REQUEST_HEADER_MAP, requestHeaderMap);
        }

        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        if (null != contentType) {
            exchange.getAttributes().put(FilterConstant.REQUEST_CONTENT_TYPE, contentType);
        }

        String xTime = headers.getFirst(AppConstant.X_TIME);
        if (null != xTime) {
            exchange.getAttributes().put(AppConstant.X_TIME, xTime);
        }

        String xTrace = headers.getFirst(AppConstant.X_TRACE);
        if (Span.current().getSpanContext().isValid()) {
            String traceId = Span.current().getSpanContext().getTraceId();
            exchange.getAttributes().put(AppConstant.X_TRACE, traceId);
        } else if (null != xTrace) {
            exchange.getAttributes().put(AppConstant.X_TRACE, xTrace);
        }

        String xClient = headers.getFirst(AppConstant.X_CLIENT);
        if (null != xClient) {
            exchange.getAttributes().put(AppConstant.X_CLIENT, xClient);
        }

        String xSecurity = headers.getFirst(AppConstant.X_SECURITY);
        if (null != xSecurity) {
            exchange.getAttributes().put(AppConstant.X_SECURITY, xSecurity);
        }

        String xHash = headers.getFirst(AppConstant.X_HASH);
        if (null != xHash) {
            exchange.getAttributes().put(AppConstant.X_HASH, xHash);
        }

        String xSign = headers.getFirst(AppConstant.X_SIGN);
        if (null != xSign) {
            exchange.getAttributes().put(AppConstant.X_SIGN, xSign);
        }

        if (HttpMethod.POST.equals(method)) {
            if (null == contentType || contentType.isEmpty()) {
                throw new AppException(GatewayError.REQUIRED_CONTENT_TYPE, contentType);
            }
            if (null == xTime || xTime.isEmpty()) {
                throw new AppException(GatewayError.REQUIRED_X_TIME, xTime);
            } else {
                checkXTime(xTime);
            }

            if (null == xTrace || xTrace.isEmpty()) {
                throw new AppException(GatewayError.REQUIRED_X_TRACE, xTrace);
            } else {
                checkXTrace(xTrace);
            }

            if (null == xClient || xClient.isEmpty()) {
                throw new AppException(GatewayError.REQUIRED_X_CLIENT, xClient);
            }

            if ((null == xSecurity || xSecurity.isEmpty())) {
                throw new AppException(GatewayError.REQUIRED_X_SECURITY, xSecurity);
            }
            if (!AppConstant.RSA.equals(xSecurity)) {
                throw new AppException(GatewayError.X_SECURITY_ERROR, xSecurity);
            }
            if (null == xSign || xSign.isEmpty()) {
                throw new AppException(GatewayError.REQUIRED_X_SIGN, xSign);
            }
            if ((null == xHash || xHash.isEmpty()) && contentType.toLowerCase().contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                throw new AppException(GatewayError.REQUIRED_X_HASH, xSign);
            }
        }

        if (null != xClient && !xClient.isEmpty()) {
            if (AppConstant.RSA.equals(xSecurity)) {
                Mono<LinkedHashMap<String, Object>> rsaData = filterHelper.rsaSelect(xClient);
                Map<String, String> finalRequestHeaderMap = requestHeaderMap;
                final String[] finalXHash = {xHash};
                return rsaData.flatMap(data -> {
                    if (data == null) {
                        return Mono.error(new AppException(GatewayError.FIND_AGENCY_SECRET_KEY_ERROR, xClient));
                    } else {
                        String serverPrivateKey = (String) data.get("serverPrivateKey");
                        String clientPublicKey = (String) data.get("clientPublicKey");
                        exchange.getAttributes().put(FilterConstant.SERVER_PRIVATE_KEY, serverPrivateKey);
                        exchange.getAttributes().put(FilterConstant.CLIENT_PUBLIC_KEY, clientPublicKey);

                        if (contentType != null && contentType.toLowerCase().contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                            if (finalXHash[0] == null || finalXHash[0].isEmpty()) {
                                return Mono.error(new AppException(GatewayError.REQUIRED_X_HASH, xSign));
                            }
                            try {
                                boolean verify = RsaUtil.verify(clientPublicKey, finalXHash[0], xSign);
                                if (!verify) {
                                    return Mono.error(new AppException(GatewayError.REQUEST_HEADER_HASH_SIGNATURE_VERIFY_ERROR, xSign));
                                }
                            } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException |
                                     InvalidKeyException | SignatureException e) {
                                log.error(e.getMessage(), e);
                                return Mono.error(new AppException(GatewayError.REQUEST_HEADER_HASH_SIGNATURE_VERIFY_EXCEPTION, e));
                            }
                            try {
                                PrivateKey privateKey = RsaUtil.privateKey(serverPrivateKey);
                                finalXHash[0] = RsaUtil.decrypt(privateKey, finalXHash[0]);
                            } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                                     BadPaddingException | InvalidKeyException | InvalidKeySpecException |
                                     IOException e) {
                                log.error(e.getMessage(), e);
                                return Mono.error(new AppException(GatewayError.REQUEST_HEADER_HASH_CIPHER_DECRYPT_EXCEPTION, e));
                            }
                            exchange.getAttributes().put(AppConstant.X_HASH, finalXHash[0]);
                            finalRequestHeaderMap.put(AppConstant.X_HASH, finalXHash[0]);
                        }

                        return chain.filter(exchange);
                    }
                });
            }
        }

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

        }));
    }

    private void checkXTime(String xTime) {
        /*
         * 尝试从 request header 中获取请求时间 X-Time
         * 以下这行代码是 设置 Time 的方式
         * headers.setZonedDateTime(AppConstant.X_TIME, ZonedDateTime.now(ZoneOffset.UTC));
         */
        if (null != xTime && !xTime.isEmpty()) {
            ZonedDateTime requestZonedDateTime;
            /*
             * Time 格式错误，502
             */
            try {
                requestZonedDateTime = ZonedDateTime.parse(xTime, DateTimeFormatter.RFC_1123_DATE_TIME);
            } catch (DateTimeParseException e) {
                log.error(e.getMessage(), e);
                throw new AppException(GatewayError.X_TIME_FORMAT_ERROR, xTime + " : " + "RFC-1123");
            }
            ZonedDateTime currentZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
            Duration duration = Duration.between(requestZonedDateTime, currentZonedDateTime);
            /*
             * 请求超时，502
             */
            Duration xTimeDuration = appConfig.getXTimeDuration();
            if (duration.compareTo(xTimeDuration) > 0) {
                throw new AppException(GatewayError.X_TIME_EXPIRED, xTime);
            }
        } else {
            throw new AppException(GatewayError.REQUIRED_X_TIME, xTime);
        }

    }

    private void checkXTrace(String xTrace) {
        /*
         * 判断请求是否重复
         */
        if (null != xTrace && !xTrace.isEmpty()) {
            String regex = "^[0-9a-f]{32}$";
            if (!xTrace.matches(regex)) {
                throw new AppException(GatewayError.X_TRACE_FORMAT_ERROR, xTrace + " : " + regex);
            }
            String key = "gateway:x-trace:" + xTrace;
            String value = stringRedisTemplate.opsForValue().get(key);
            Duration duration = appConfig.getXTraceDuration();
            if (null == value) {
                stringRedisTemplate.opsForValue().set(key, xTrace, duration);
            } else {
                throw new AppException(GatewayError.X_TRACE_DUPLICATE, xTrace);
            }
        } else {
            throw new AppException(GatewayError.REQUIRED_X_TRACE, xTrace);
        }

    }

    private static String oAuth2Token(HttpHeaders headers) {
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (null != authorization) {
            return authorization.replace("Bearer ", "");
        }
        return null;
    }

    private void log(ServerHttpRequest request) {

        URI uri = request.getURI();
        HttpMethod method = request.getMethod();
        log.info("S < : {} {}", method, uri);

        MultiValueMap<String, String> parameterMap = request.getQueryParams();
        if (!parameterMap.isEmpty()) {
            try {
                log.info("S < : {}", new ObjectMapper().writeValueAsString(parameterMap));
            } catch (JacksonException e) {
                log.error(e.getMessage(), e);
            }
        }

        HttpHeaders headers = request.getHeaders();
        log.info("S < : {}", headers);
    }

}
