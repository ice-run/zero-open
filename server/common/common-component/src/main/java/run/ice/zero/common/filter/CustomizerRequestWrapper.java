package run.ice.zero.common.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

@Slf4j
public class CustomizerRequestWrapper extends HttpServletRequestWrapper {

    private final HttpServletRequest request;

    public CustomizerRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        String method = this.request.getMethod();
        String contentType = this.request.getContentType();
        int contentLength = this.request.getContentLength();

        if (!HttpMethod.POST.matches(method) || null == contentType || !contentType.contains(MediaType.APPLICATION_JSON_VALUE) || contentLength <= 0) {
            return super.getInputStream();
        }

        String uri = this.request.getRequestURI();
        log.info("S < : {} {}", method, uri);

        Map<String, String[]> parameterMap = this.request.getParameterMap();
        if (!parameterMap.isEmpty()) {
            log.info("S < : {}", new ObjectMapper().writeValueAsString(parameterMap));
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        this.request.getHeaderNames().asIterator().forEachRemaining(name -> {
            Enumeration<String> headers = this.request.getHeaders(name);
            while (headers.hasMoreElements()) {
                String value = headers.nextElement();
                httpHeaders.add(name, value);
            }
        });
        log.info("S < : {}", httpHeaders);

        int limit = 1024 * 10;

        ServletInputStream inputStream = super.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        inputStream.transferTo(outputStream);
        byte[] bytes = outputStream.toByteArray();
        String requestBody = bytes.length > limit ? (new String(bytes, 0, limit) + " ...") : new String(bytes);
        log.info("S < : {}", requestBody);

        return new CustomizerInputStream(bytes);
    }

    private static class CustomizerInputStream extends ServletInputStream {

        private final InputStream inputStream;

        public CustomizerInputStream(byte[] body) {
            this.inputStream = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }

        @Override
        public int read() throws IOException {
            return this.inputStream.read();
        }

    }

}
