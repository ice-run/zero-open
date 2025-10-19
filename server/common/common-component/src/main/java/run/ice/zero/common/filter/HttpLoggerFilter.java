package run.ice.zero.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Component
public class HttpLoggerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*
         * 不建议直接使用 HttpServletRequestWrapper，因为它会导致 getInputStream() 和 getReader() 只能调用一次
         * 也不建议使用 ContentCachingRequestWrapper，因为它需要在 filterChain.doFilter() 之后调用 getInputStream() 才会缓存请求体
         * 这样会导致在 filterChain.doFilter() 之前无法获取请求体，即无法提前打印请求体
         */
        CustomizerRequestWrapper customizerWrapper = new CustomizerRequestWrapper(request);
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(customizerWrapper, 0);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String uri = requestWrapper.getRequestURI();
        String method = requestWrapper.getMethod();
        String contentType = requestWrapper.getContentType();

        if (HttpMethod.POST.matches(method) && null != contentType && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            /*
             * 不再在此处打印日志，因为已经在 CustomizerRequestWrapper 中执行过了。
             */
            log.trace("S < : {} {}", method, uri);
        }

        filterChain.doFilter(requestWrapper, responseWrapper);

        contentType = responseWrapper.getContentType();

        if (null != contentType && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {

            int limit = 1024 * 10;
            int status = responseWrapper.getStatus();
            HttpStatus httpStatus = HttpStatus.valueOf(status);
            if (httpStatus.isError()) {
                log.error("S > : {} {}", httpStatus.value(), httpStatus.getReasonPhrase());
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            responseWrapper.getHeaderNames().forEach(name -> {
                Collection<String> headers = responseWrapper.getHeaders(name);
                headers.forEach(value -> httpHeaders.add(name, value));
            });
            log.info("S > : {}", httpHeaders);

            byte[] responseBodyBytes = responseWrapper.getContentAsByteArray();
            String responseBody = responseBodyBytes.length > limit ? (new String(responseBodyBytes, 0, limit) + " ...") : new String(responseBodyBytes);
            log.info("S > : {}", responseBody);
        }

        responseWrapper.copyBodyToResponse();
    }

}
