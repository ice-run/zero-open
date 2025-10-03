package run.ice.zero.gateway.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.error.AppException;
import run.ice.zero.gateway.config.AppConfig;
import run.ice.zero.gateway.constant.FilterConstant;
import run.ice.zero.gateway.error.GatewayError;
import run.ice.zero.common.model.No;
import run.ice.zero.common.model.Ok;
import run.ice.zero.common.model.Response;
import run.ice.zero.common.util.security.AesUtil;
import run.ice.zero.common.util.security.HashUtil;
import run.ice.zero.common.util.security.RsaUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ErrorHelper {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AppConfig appConfig;

    /**
     * 开发测试网关
     *
     * @param exchange ServerWebExchange
     * @param ex       Throwable
     * @return Mono<Void>
     */
    public Mono<Void> handleX(ServerWebExchange exchange, Throwable ex) {

        ServerHttpResponse httpResponse = httpResponse(exchange, ex);

        Response<Ok> response = responseOk(ex);

        httpResponse.setStatusCode(HttpStatus.OK);
        HttpHeaders httpResponseHeaders = httpResponse.getHeaders();
        httpResponseHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpResponseHeaders.set(AppConstant.X_TIME, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
        httpResponseHeaders.set(AppConstant.X_TRACE, exchange.getAttribute(AppConstant.X_TRACE));

        return response(httpResponse, response);
    }

    /**
     * 对 web & app 网关
     *
     * @param exchange ServerWebExchange
     * @param ex       Throwable
     * @return Mono<Void>
     */
    public Mono<Void> handleY(ServerWebExchange exchange, Throwable ex) {

        ServerHttpResponse httpResponse = httpResponse(exchange, ex);

        Response<Ok> responseOk = responseOk(ex);

        String message = responseOk.getMessage();

        Response<String> response = new Response<>();
        response.setCode(responseOk.getCode());

        String xSecurity = exchange.getAttribute(AppConstant.X_SECURITY);
        String xSign = null;
        String data = null;
        String plains;
        try {
            plains = objectMapper.writeValueAsString(new No());
        } catch (JacksonException e) {
            log.error(e.getMessage(), e);
            plains = "{}";
        }
        if (AppConstant.AES.equals(xSecurity)) {
            try {
                String aesKey = exchange.getAttribute(FilterConstant.AES_KEY);
                String aesIv = exchange.getAttribute(FilterConstant.AES_IV);
                data = AesUtil.encrypt(aesKey, aesIv, plains, AesUtil.Encode.BASE64);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                message = message + " : " + e.getMessage();
            }
            xSign = HashUtil.sha256(data);
        }
        response.setMessage(message);
        response.setData(data);

        httpResponse.setStatusCode(HttpStatus.OK);
        HttpHeaders httpResponseHeaders = httpResponse.getHeaders();
        httpResponseHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpResponseHeaders.set(AppConstant.X_SECURITY, xSecurity);
        httpResponseHeaders.set(AppConstant.X_SIGN, xSign);
        httpResponseHeaders.set(AppConstant.X_TIME, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
        httpResponseHeaders.set(AppConstant.X_TRACE, exchange.getAttribute(AppConstant.X_TRACE));

        return response(httpResponse, response);
    }

    /**
     * 对外网关
     *
     * @param exchange ServerWebExchange
     * @param ex       Throwable
     * @return Mono<Void>
     */
    public Mono<Void> handleZ(ServerWebExchange exchange, Throwable ex) {

        ServerHttpResponse httpResponse = httpResponse(exchange, ex);

        Response<Ok> responseOk = responseOk(ex);

        String message = responseOk.getMessage();

        Response<String> response = new Response<>();
        response.setCode(responseOk.getCode());

        String xSecurity = exchange.getAttribute(AppConstant.X_SECURITY);
        String xClient = exchange.getAttribute(AppConstant.X_CLIENT);
        String xSign = null;
        String data = null;
        String plains;
        try {
            plains = objectMapper.writeValueAsString(new No());
        } catch (JacksonException e) {
            log.error(e.getMessage(), e);
            message = message + " : " + e.getMessage();
            plains = "{}";
        }
        if (AppConstant.RSA.equals(xSecurity)) {
            String clientPublicKey = exchange.getAttribute(FilterConstant.CLIENT_PUBLIC_KEY);
            String serverPrivateKey = exchange.getAttribute(FilterConstant.SERVER_PRIVATE_KEY);
            try {
                assert clientPublicKey != null;
                data = RsaUtil.encrypt(clientPublicKey, plains);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                message = message + " : " + e.getMessage();
            }
            try {
                assert data != null;
                assert serverPrivateKey != null;
                xSign = RsaUtil.sign(serverPrivateKey, data);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                message = message + " : " + e.getMessage();
            }
        }
        response.setMessage(message);
        response.setData(data);

        httpResponse.setStatusCode(HttpStatus.OK);
        HttpHeaders httpResponseHeaders = httpResponse.getHeaders();
        httpResponseHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpResponseHeaders.set(AppConstant.X_CLIENT, xClient);
        httpResponseHeaders.set(AppConstant.X_SECURITY, xSecurity);
        httpResponseHeaders.set(AppConstant.X_SIGN, xSign);
        httpResponseHeaders.set(AppConstant.X_TIME, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
        httpResponseHeaders.set(AppConstant.X_TRACE, exchange.getAttribute(AppConstant.X_TRACE));

        return response(httpResponse, response);
    }

    private ServerHttpResponse httpResponse(ServerWebExchange exchange, Throwable ex) {
        log.error(ex.getMessage(), ex);
        return exchange.getResponse();
    }

    private Response<Ok> responseOk(Throwable ex) {
        Response<Ok> response;
        if (ex instanceof AppException) {
            response = new Response<>((AppException) ex);
        } else {
            response = new Response<>(new AppException(GatewayError.ERROR, ex.getMessage()));
        }
        Ok data = response.getData();
        if (null == data) {
            data = new Ok();
            response.setData(data);
        }
        return response;
    }

    private Mono<Void> response(ServerHttpResponse httpResponse, Response<?> response) {
        log(httpResponse.getHeaders(), response.toJson().getBytes(StandardCharsets.UTF_8));
        return httpResponse.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = httpResponse.bufferFactory();
            try {
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(response));
            } catch (JacksonException e) {
                log.error(e.getMessage(), e);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }

    private void log(HttpHeaders headers, byte[] body) {
        log.info("S > : {}", headers);

        int limit = 1024 * 10;
        String responseBody = body.length > limit ? (new String(body, 0, limit) + " ...") : new String(body);
        log.info("S > : {}", responseBody);
    }

}
