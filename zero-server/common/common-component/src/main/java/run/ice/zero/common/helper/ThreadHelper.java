package run.ice.zero.common.helper;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
@Component
public class ThreadHelper {

    @SafeVarargs
    public final <I, O> CompletableFuture<O> virtual(Function<I[], O> function, I... i) {
        Context context = Context.current();
        CompletableFuture<O> future = new CompletableFuture<>();
        Thread.ofVirtual().start(() -> {
            try (Scope ignored = context.makeCurrent()) {
                O result = function.apply(i);
                future.complete(result);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @SafeVarargs
    public final <I, O> CompletableFuture<O> platform(Function<I[], O> function, I... i) {
        Context context = Context.current();
        CompletableFuture<O> future = new CompletableFuture<>();
        Thread.ofPlatform().start(() -> {
            try (Scope ignored = context.makeCurrent()) {
                O result = function.apply(i);
                future.complete(result);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

}
