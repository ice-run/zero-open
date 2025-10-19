package run.ice.zero.gateway.helper;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.error.AppError;
import run.ice.zero.common.error.AppException;
import run.ice.zero.gateway.config.AppConfig;
import run.ice.zero.gateway.constant.FilterConstant;
import run.ice.zero.gateway.error.GatewayError;
import run.ice.zero.common.model.No;
import run.ice.zero.common.util.security.AesUtil;
import run.ice.zero.common.util.security.RsaUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Component
public class ResponseHelper {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AppConfig appConfig;

    @SuppressWarnings("unchecked")
    public Mono<Void> filterX(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
        ServerHttpResponseDecorator serverHttpResponseDecorator = new ServerHttpResponseDecorator(serverHttpResponse) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpStatusCode httpStatus = getStatusCode();
                if (null == httpStatus) {
                    throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_HTTP_STATUS_IS_NULL);
                }
                if (httpStatus.isError()) {
                    log.error("httpStatus : {}", httpStatus);
                    throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_HTTP_STATUS_IS_ERROR, httpStatus.toString());
                }

                if (!(body instanceof Flux)) {
                    throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_IS_NOT_FLUX);
                }

                String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                if (null == originalResponseContentType || !originalResponseContentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                    return super.writeWith(body);
                }

                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                    List<byte[]> bytes = new ArrayList<>();
                    dataBuffers.forEach(dataBuffer -> {
                        try {
                            byte[] c = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(c);
                            DataBufferUtils.release(dataBuffer);
                            bytes.add(c);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_RELEASE_EXCEPTION, e.getMessage());
                        }
                    });
                    byte[] content = {};
                    for (byte[] b : bytes) {
                        content = merge(content, b);
                    }
                    log(content);

                    if (content.length > appConfig.getResponseBodySizeLimit()) {
                        String response = new String(content, Charset.defaultCharset());
                        response = response.substring(0, response.length() < appConfig.getPrintBodySizeLimit() ? response.length() : appConfig.getPrintBodySizeLimit());
                        log.error("!!! response body too large : {} :\n{}", content.length, response);
                        throw new AppException(GatewayError.RESPONSE_BODY_TOO_LARGE, String.valueOf(content.length));
                    }

                    LinkedHashMap<Object, Object> bodyMap;
                    try {
                        bodyMap = objectMapper.readValue(content, LinkedHashMap.class);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        log.error("http response body : {}", new String(content, StandardCharsets.UTF_8));
                        throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_DESERIALIZE_EXCEPTION, e.getMessage());
                    }

                    byte[] uppedContent;
                    try {
                        uppedContent = objectMapper.writeValueAsBytes(bodyMap);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.RESPONSE_BODY_SERIALIZE_EXCEPTION, e.getMessage());
                    }

                    serverHttpResponse.getHeaders().setContentLength(uppedContent.length);
                    serverHttpResponse.getHeaders().set(AppConstant.X_TIME, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
                    serverHttpResponse.getHeaders().set(AppConstant.X_TRACE, exchange.getAttribute(AppConstant.X_TRACE));

                    log(serverHttpResponse.getHeaders(), uppedContent);

                    return dataBufferFactory.wrap(uppedContent);
                }));
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }

        };

        return chain.filter(exchange.mutate().response(serverHttpResponseDecorator).build());

    }

    @SuppressWarnings("unchecked")
    public Mono<Void> filterY(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
        ServerHttpResponseDecorator serverHttpResponseDecorator = new ServerHttpResponseDecorator(serverHttpResponse) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpStatusCode httpStatus = getStatusCode();
                if (null == httpStatus) {
                    throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_HTTP_STATUS_IS_NULL);
                }
                if (httpStatus.isError()) {
                    log.error("httpStatus : {}", httpStatus);
                    throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_HTTP_STATUS_IS_ERROR, httpStatus.toString());
                }

                if (!(body instanceof Flux)) {
                    throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_IS_NOT_FLUX);
                }

                String xSecurity = exchange.getAttribute(AppConstant.X_SECURITY);
                String aesKey = exchange.getAttribute(FilterConstant.AES_KEY);
                String aesIv = exchange.getAttribute(FilterConstant.AES_IV);

                String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);

                if (null != originalResponseContentType && originalResponseContentType.contains(HttpHeaders.CONTENT_DISPOSITION)) {
                    List<String> cds = serverHttpResponse.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION);
                    if (null != cds && !cds.isEmpty() && cds.stream().anyMatch(s -> s.startsWith("attachment;"))) {
                        List<String> hs = serverHttpResponse.getHeaders().get(AppConstant.X_HASH);
                        if (null != hs && !hs.isEmpty()) {
                            String hash = hs.getFirst();
                            if (null != hash && !hash.isEmpty()) {
                                if (AppConstant.AES.equals(xSecurity)) {
                                    try {
                                        hash = AesUtil.encrypt(aesKey, aesIv, hash, AesUtil.Encode.BASE64);
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                        throw new AppException(GatewayError.RESPONSE_HASH_ENCRYPT_ERROR, e.getMessage());
                                    }
                                    serverHttpResponse.getHeaders().set(AppConstant.X_HASH, hash);
                                }
                            }
                        }
                        return super.writeWith(body);
                    }
                }

                if (null == originalResponseContentType || !originalResponseContentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                    return super.writeWith(body);
                }

                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                    List<byte[]> bytes = new ArrayList<>();
                    dataBuffers.forEach(dataBuffer -> {
                        try {
                            byte[] c = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(c);
                            DataBufferUtils.release(dataBuffer);
                            bytes.add(c);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_RELEASE_EXCEPTION, e.getMessage());
                        }
                    });
                    byte[] content = {};
                    for (byte[] b : bytes) {
                        content = merge(content, b);
                    }
                    log(content);

                    if (content.length > appConfig.getResponseBodySizeLimit()) {
                        String response = new String(content, Charset.defaultCharset());
                        response = response.substring(0, response.length() < appConfig.getPrintBodySizeLimit() ? response.length() : appConfig.getPrintBodySizeLimit());
                        log.error("!!! response body too large : {} :\n{}", content.length, response);
                        throw new AppException(GatewayError.RESPONSE_BODY_TOO_LARGE, String.valueOf(content.length));
                    }

                    LinkedHashMap<Object, Object> bodyMap;
                    try {
                        bodyMap = objectMapper.readValue(content, LinkedHashMap.class);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        log.error("http response body : {}", new String(content, StandardCharsets.UTF_8));
                        throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_DESERIALIZE_EXCEPTION, e.getMessage());
                    }

                    Object code = bodyMap.get("code");
                    if (null == code || String.valueOf(code).isEmpty()) {
                        bodyMap.put("code", AppError.ERROR.code);
                    }

                    Object message = bodyMap.get("message");
                    if (null == message || String.valueOf(message).isEmpty()) {
                        bodyMap.put("message", AppError.ERROR.message);
                    }

                    Object dataObject = bodyMap.computeIfAbsent("data", k -> new No());

                    try {
                        String dataPlains = null;
                        String dataCipher = null;

                        if (null != xSecurity && !xSecurity.isEmpty()) {
                            try {
                                dataPlains = objectMapper.writeValueAsString(dataObject);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                throw new AppException(GatewayError.RESPONSE_BODY_SERIALIZE_EXCEPTION, e.getMessage());
                            }
                        }

                        if (AppConstant.AES.equals(xSecurity)) {
                            try {
                                dataCipher = AesUtil.encrypt(aesKey, aesIv, dataPlains, AesUtil.Encode.BASE64);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                throw new AppException(GatewayError.RESPONSE_BODY_ENCRYPT_ERROR, e.getMessage());
                            }
                        }

                        if (null != xSecurity && !xSecurity.isEmpty()) {
                            bodyMap.put("data", dataCipher);
                        }

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.RESPONSE_BODY_ENCRYPT_ERROR, e.getMessage());
                    }

                    byte[] uppedContent;
                    try {
                        uppedContent = objectMapper.writeValueAsBytes(bodyMap);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.RESPONSE_BODY_SERIALIZE_EXCEPTION, e.getMessage());
                    }
                    serverHttpResponse.getHeaders().setContentLength(uppedContent.length);
                    String security = exchange.getAttribute(AppConstant.X_SECURITY);
                    if (null != security && !security.isEmpty()) {
                        serverHttpResponse.getHeaders().set(AppConstant.X_SECURITY, security);
                    }

                    serverHttpResponse.getHeaders().set(AppConstant.X_TIME, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
                    serverHttpResponse.getHeaders().set(AppConstant.X_TRACE, exchange.getAttribute(AppConstant.X_TRACE));

                    log(serverHttpResponse.getHeaders(), uppedContent);

                    return dataBufferFactory.wrap(uppedContent);
                }));
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }

        };

        return chain.filter(exchange.mutate().response(serverHttpResponseDecorator).build());
    }


    @SuppressWarnings("unchecked")
    public Mono<Void> filterZ(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
        ServerHttpResponseDecorator serverHttpResponseDecorator = new ServerHttpResponseDecorator(serverHttpResponse) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpStatusCode httpStatus = getStatusCode();
                if (null == httpStatus) {
                    throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_HTTP_STATUS_IS_NULL);
                }
                if (httpStatus.isError()) {
                    log.error("httpStatus : {}", httpStatus);
                    throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_HTTP_STATUS_IS_ERROR, httpStatus.toString());
                }

                if (!(body instanceof Flux)) {
                    throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_IS_NOT_FLUX);
                }
                String xSecurity = exchange.getAttribute(AppConstant.X_SECURITY);
                String clientPublicKey = exchange.getAttribute(FilterConstant.CLIENT_PUBLIC_KEY);
                String serverPrivateKey = exchange.getAttribute(FilterConstant.SERVER_PRIVATE_KEY);

                String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);

                if (null != originalResponseContentType && originalResponseContentType.contains(HttpHeaders.CONTENT_DISPOSITION)) {
                    List<String> cds = serverHttpResponse.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION);
                    if (null != cds && !cds.isEmpty() && cds.stream().anyMatch(s -> s.startsWith("attachment;"))) {
                        List<String> hs = serverHttpResponse.getHeaders().get(AppConstant.X_HASH);
                        if (null != hs && !hs.isEmpty()) {
                            String hash = hs.getFirst();
                            if (null != hash && !hash.isEmpty()) {
                                if (AppConstant.RSA.equals(xSecurity)) {
                                    try {
                                        hash = RsaUtil.encrypt(clientPublicKey, hash);
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                        throw new AppException(GatewayError.RESPONSE_HASH_ENCRYPT_ERROR, e.getMessage());
                                    }
                                    String xSign;
                                    try {
                                        xSign = RsaUtil.sign(serverPrivateKey, hash);
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                        throw new AppException(GatewayError.RESPONSE_HASH_SIGN_ERROR, e.getMessage());
                                    }
                                    serverHttpResponse.getHeaders().set(AppConstant.X_HASH, hash);
                                    serverHttpResponse.getHeaders().set(AppConstant.X_SIGN, xSign);
                                }
                            }
                        }
                        return super.writeWith(body);
                    }
                }

                if (null == originalResponseContentType || !originalResponseContentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                    return super.writeWith(body);
                }

                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                    List<byte[]> bytes = new ArrayList<>();
                    dataBuffers.forEach(dataBuffer -> {
                        try {
                            byte[] c = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(c);
                            DataBufferUtils.release(dataBuffer);
                            bytes.add(c);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_RELEASE_EXCEPTION, e.getMessage());
                        }
                    });
                    byte[] content = {};
                    for (byte[] b : bytes) {
                        content = merge(content, b);
                    }
                    log(content);

                    if (content.length > appConfig.getResponseBodySizeLimit()) {
                        String response = new String(content, Charset.defaultCharset());
                        response = response.substring(0, response.length() < appConfig.getPrintBodySizeLimit() ? response.length() : appConfig.getPrintBodySizeLimit());
                        log.error("!!! response body too large : {} :\n{}", content.length, response);
                        throw new AppException(GatewayError.RESPONSE_BODY_TOO_LARGE, String.valueOf(content.length));
                    }

                    LinkedHashMap<Object, Object> bodyMap;
                    try {
                        bodyMap = objectMapper.readValue(content, LinkedHashMap.class);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        log.error("http response body : {}", new String(content, StandardCharsets.UTF_8));
                        throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_DESERIALIZE_EXCEPTION, e.getMessage());
                    }

                    Object code = bodyMap.get("code");
                    if (null == code || String.valueOf(code).isEmpty()) {
                        bodyMap.put("code", AppError.ERROR.code);
                    }

                    Object message = bodyMap.get("message");
                    if (null == message || String.valueOf(message).isEmpty()) {
                        bodyMap.put("message", AppError.ERROR.message);
                    }

                    Object o = bodyMap.computeIfAbsent("data", k -> new No());

                    String xSign = null;
                    try {
                        if (null != xSecurity && !xSecurity.isEmpty()) {

                            String data;
                            try {
                                data = objectMapper.writeValueAsString(o);
                            } catch (JacksonException e) {
                                log.error(e.getMessage(), e);
                                throw new AppException(GatewayError.INNER_SERVICE_RESPONSE_BODY_DATA_SERIALIZE_EXCEPTION, e.getMessage());
                            }

                            if (AppConstant.RSA.equals(xSecurity)) {
                                try {
                                    assert clientPublicKey != null;
                                    data = RsaUtil.encrypt(clientPublicKey, data);
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                    throw new AppException(GatewayError.RESPONSE_BODY_DATA_PLAINS_ENCRYPT_EXCEPTION, e.getMessage());
                                }

                                try {
                                    assert serverPrivateKey != null;
                                    xSign = RsaUtil.sign(serverPrivateKey, data);
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                    throw new AppException(GatewayError.RESPONSE_BODY_DATA_CIPHER_SIGN_EXCEPTION, e.getMessage());
                                }
                            }

                            bodyMap.put("data", data);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.RESPONSE_BODY_ENCRYPT_ERROR, e.getMessage());
                    }

                    byte[] uppedContent;
                    try {
                        uppedContent = objectMapper.writeValueAsBytes(bodyMap);
                    } catch (JacksonException e) {
                        log.error(e.getMessage(), e);
                        throw new AppException(GatewayError.RESPONSE_BODY_SERIALIZE_EXCEPTION, e.getMessage());
                    }
                    serverHttpResponse.getHeaders().setContentLength(uppedContent.length);
                    String xClient = exchange.getAttribute(AppConstant.X_CLIENT);
                    if (null != xClient && !xClient.isEmpty()) {
                        serverHttpResponse.getHeaders().set(AppConstant.X_CLIENT, xClient);
                    }
                    String security = exchange.getAttribute(AppConstant.X_SECURITY);
                    if (null != security && !security.isEmpty()) {
                        serverHttpResponse.getHeaders().set(AppConstant.X_SECURITY, security);
                    }
                    if (null != xSign && !xSign.isEmpty()) {
                        serverHttpResponse.getHeaders().set(AppConstant.X_SIGN, xSign);
                    }
                    serverHttpResponse.getHeaders().set(AppConstant.X_TIME, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
                    serverHttpResponse.getHeaders().set(AppConstant.X_TRACE, exchange.getAttribute(AppConstant.X_TRACE));

                    log(serverHttpResponse.getHeaders(), uppedContent);

                    return dataBufferFactory.wrap(uppedContent);
                }));
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }

        };

        return chain.filter(exchange.mutate().response(serverHttpResponseDecorator).build());

    }


    private static byte[] merge(byte[] array1, byte[] array2) {
        byte[] mergedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);
        return mergedArray;
    }

    private void log(HttpHeaders headers, byte[] body) {
        log.info("S > : {}", headers);

        int limit = 1024 * 10;
        String responseBody = body.length > limit ? (new String(body, 0, limit) + " ...") : new String(body);
        log.info("S > : {}", responseBody);
    }

    private void log(byte[] body) {
        int limit = 1024 * 10;
        String responseBody = body.length > limit ? (new String(body, 0, limit) + " ...") : new String(body);
        log.info("X > : {}", responseBody);
    }

}
