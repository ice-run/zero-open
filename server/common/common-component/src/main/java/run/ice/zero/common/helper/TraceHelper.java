package run.ice.zero.common.helper;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.ice.zero.common.util.math.RandomUtil;

@Slf4j
@Component
public class TraceHelper {

    public static final String TRACE_PARENT = "traceparent";

    public static final String TRACE_VERSION = "00";

    public String traceparent() {
        try {
            Span span = Span.current();
            SpanContext spanContext = span.getSpanContext();
            if (!spanContext.isValid()) {
                log.error("spanContext is invalid");
                return null;
            }
            return TRACE_VERSION + "-" + spanContext.getTraceId() + "-" + spanContext.getSpanId() + "-" + spanContext.getTraceFlags().asHex();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取当前链路追踪的上下文中的链路追踪 id
     *
     * @return traceId
     */
    public String traceId() {
        try {
            Span span = Span.current();
            SpanContext spanContext = span.getSpanContext();
            if (!spanContext.isValid()) {
                log.error("spanContext is invalid");
                return null;
            }
            return spanContext.getTraceId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 传入一个链路追踪 id 创建一个新的 span
     *
     * @param traceId 链路追踪 id
     * @return span
     */
    public Span createSpan(String traceId) {
        if (null == traceId) {
            return null;
        }
        String regex = "^[0-9a-f]{32}$";
        if (!traceId.matches(regex)) {
            log.error("traceId is invalid");
            return null;
        }
        String spanId = RandomUtil.hex(16).toLowerCase();
        try {
            SpanContext spanContext = SpanContext.create(traceId, spanId, TraceFlags.getSampled(), TraceState.getDefault());
            return Span.wrap(spanContext);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
