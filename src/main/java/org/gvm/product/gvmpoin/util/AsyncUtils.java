package org.gvm.product.gvmpoin.util;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncUtils<T> {

  private AsyncUtils() {
  }

  public static <T> DeferredResult<T> doAsyncProcess(Supplier<T> supplier) {
    final DeferredResult<T> deferredResult = new DeferredResult<>();

    CompletableFuture.supplyAsync(supplier).whenCompleteAsync((result, throwable) -> {
      if (throwable != null) {
        deferredResult.setErrorResult(throwable.getCause());
      } else {
        deferredResult.setResult(result);
      }
    });

    return deferredResult;
  }
}
