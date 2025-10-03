package run.ice.zero.common.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.error.AppError;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
public class InnerHelper {

    @Value("${zero.service.zero.gateway:gateway.zero}")
    private String zeroGateway;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private TraceHelper traceHelper;

    public <P, D> Response<D> zero(String uri, Request<P> request, ParameterizedTypeReference<Response<D>> typeReference) {
        return inner(zeroGateway, uri, request, typeReference);
    }

    private <P, D> Response<D> inner(String gateway, String uri, Request<P> request, ParameterizedTypeReference<Response<D>> typeReference) {
        String url = gateway.matches("http(s)?://.*") ? gateway + uri : "http://" + gateway + uri;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(AppConstant.X_TIME, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
        String traceId = traceHelper.traceId();
        if (null == traceId || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        httpHeaders.set(AppConstant.X_TRACE, traceId);
        HttpEntity<Request<P>> httpEntity = new HttpEntity<>(request, httpHeaders);

        ResponseEntity<Response<D>> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException("cloud inner gateway : [" + uri + "] " + e.getMessage());
        }
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        if (!HttpStatus.OK.equals(httpStatusCode)) {
            throw new AppException(AppError.HTTP_STATUS_IS_NOT_2XX + "cloud inner gateway : [" + uri + "] : " + request + " : " + httpStatusCode);
        }

        Response<D> response = responseEntity.getBody();
        if (null == response) {
            throw new AppException(AppError.HTTP_RESPONSE_IS_NULL, "cloud inner gateway : [" + uri + "] ");
        }

        if (!AppError.OK.code.equals(response.getCode())) {
            throw new AppException(response.getCode(), response.getMessage());
        }

        return response;
    }

}
