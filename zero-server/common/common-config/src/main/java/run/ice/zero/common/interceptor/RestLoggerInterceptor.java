package run.ice.zero.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
public class RestLoggerInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        URI uri = request.getURI();
        HttpMethod method = request.getMethod();
        int defaultLimit = 1024 * 10;

        if (HttpMethod.POST.equals(method)) {
            log.info("C > : {} {}", method, uri);

            HttpHeaders requestHeaders = request.getHeaders();
            log.info("C > : {}", requestHeaders);

            int limit = defaultLimit;
            if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(requestHeaders.getContentType())) {
                limit = 1024;
            }

            String requestBody = body.length > limit ? (new String(body, 0, limit) + " ...") : new String(body);
            log.info("C > : {}", requestBody);
        }

        ClientHttpResponse response = execution.execute(request, body);

        if (HttpMethod.POST.equals(method)) {
            HttpStatusCode statusCode = response.getStatusCode();
            if (statusCode.isError()) {
                String statusText = response.getStatusText();
                log.error("C < : {} {}", statusCode.value(), statusText);
            }

            HttpHeaders responseHeaders = response.getHeaders();
            log.info("C < : {}", responseHeaders);

            int limit = defaultLimit;
            if (responseHeaders.containsHeader(HttpHeaders.CONTENT_DISPOSITION)) {
                limit = 256;
            }

            byte[] bytes = response.getBody().readAllBytes();
            String responseBody = bytes.length > limit ? (new String(bytes, 0, limit) + " ...") : new String(bytes);
            log.info("C < : {}", responseBody);
            return new CustomizerClientHttpResponse(response, bytes);
        }

        return response;
    }

}
