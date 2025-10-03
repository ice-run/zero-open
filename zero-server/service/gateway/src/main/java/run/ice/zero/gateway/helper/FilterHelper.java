package run.ice.zero.gateway.helper;

import io.opentelemetry.api.trace.Span;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.error.AppError;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;
import run.ice.zero.common.model.Serializer;
import tools.jackson.core.type.TypeReference;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.UUID;

@Slf4j
@Component
public class FilterHelper {

    @Value("${zero.service.zero.base:base.zero}")
    private String base;

    @Resource
    private WebClient webClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public Mono<LinkedHashMap<String, Object>> aesSelect(String id) {
        if (null == id || id.isEmpty()) {
            return Mono.empty();
        }
        String key = "gateway" + ":" + "secret-aes" + ":" + id;
        return Mono.justOrEmpty(stringRedisTemplate.opsForValue().get(key))
                .map(value -> Serializer.ofJson(value, new TypeReference<LinkedHashMap<String, Object>>() {
                }))
                .switchIfEmpty(Mono.defer(() -> {
                    LinkedHashMap<String, Object> param = new LinkedHashMap<>();
                    param.put("id", id);
                    String url = (base.matches("^https?://.*") ? base : "http://" + base) + "/api/aes-select";
                    ParameterizedTypeReference<Response<LinkedHashMap<String, Object>>> typeReference = new ParameterizedTypeReference<>() {
                    };
                    return inner(url, new Request<>(param), typeReference)
                            .flatMap(response -> {
                                if (Response.isOk(response)) {
                                    LinkedHashMap<String, Object> data = response.getData();
                                    String value = Serializer.toJson(data);
                                    stringRedisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10L));
                                    return Mono.just(data);
                                } else {
                                    return Mono.error(new AppException(response.getCode(), response.getMessage()));
                                }
                            });
                }));
    }

    public Mono<LinkedHashMap<String, Object>> rsaSelect(String id) {
        if (null == id || id.isEmpty()) {
            return Mono.empty();
        }
        String key = "gateway" + ":" + "secret-rsa" + ":" + id;
        return Mono.justOrEmpty(stringRedisTemplate.opsForValue().get(key))
                .map(value -> Serializer.ofJson(value, new TypeReference<LinkedHashMap<String, Object>>() {
                }))
                .switchIfEmpty(Mono.defer(() -> {
                    LinkedHashMap<String, Object> param = new LinkedHashMap<>();
                    param.put("id", id);
                    String url = (base.matches("^https?://.*") ? base : "http://" + base) + "/api/rsa-select";
                    ParameterizedTypeReference<Response<LinkedHashMap<String, Object>>> typeReference = new ParameterizedTypeReference<>() {
                    };
                    return inner(url, new Request<>(param), typeReference)
                            .flatMap(response -> {
                                if (Response.isOk(response)) {
                                    LinkedHashMap<String, Object> data = response.getData();
                                    String value = Serializer.toJson(data);
                                    stringRedisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10L));
                                    return Mono.just(data);
                                } else {
                                    return Mono.error(new AppException(response.getCode(), response.getMessage()));
                                }
                            });
                }));
    }

    private <P, D> Mono<Response<D>> inner(String url, Request<P> request, ParameterizedTypeReference<Response<D>> typeReference) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return inner(url, httpHeaders, request, typeReference);
    }

    private <P, D> Mono<Response<D>> inner(String url, HttpHeaders httpHeaders, Request<P> request, ParameterizedTypeReference<Response<D>> typeReference) {

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(AppConstant.X_TIME, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
        String traceId = Span.current().getSpanContext().isValid() ? Span.current().getSpanContext().getTraceId() : UUID.randomUUID().toString().replace("-", "");
        httpHeaders.set(AppConstant.X_TRACE, traceId);

        return webClient.post()
                .uri(url)
                .headers(headers -> headers.addAll(httpHeaders))
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("HTTP Status: {} : {} : {}", clientResponse.statusCode(), url, request.toJson());
                            return Mono.error(new AppException(AppError.HTTP_STATUS_IS_NOT_2XX, clientResponse.statusCode() + " : " + url + " : " + request.toJson()));
                        })
                )
                .bodyToMono(typeReference)
                .flatMap(response -> {
                    if (response == null) {
                        log.error("HTTP Response is null: {} : {}", url, request.toJson());
                        return Mono.error(new AppException(AppError.HTTP_RESPONSE_IS_NULL, url + " : " + request.toJson()));
                    }
                    if (!AppError.OK.code.equals(response.getCode())) {
                        log.error("Response error: {} : {}", response.getCode(), response.getMessage());
                        return Mono.error(new AppException(response.getCode(), response.getMessage()));
                    }
                    return Mono.just(response);
                })
                .doOnError(e -> log.error(e.getMessage(), e));
    }

}
