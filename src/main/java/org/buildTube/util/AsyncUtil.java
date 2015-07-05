package org.buildTube.util;

import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class AsyncUtil {

  public <T> FlatMap<T> flatMapCommands(List<ListenableFuture<T>> futureCommands) {
    FlatMap flatMap = new FlatMap(futureCommands);
    flatMap.subscribeToFutures();
    return flatMap;
  }

  public <S, T> ListenableFuture<T> adaptFuture(ListenableFuture<S> futureToAdapt,
                                                FunctionalFutureAdapter<S, T> adapter) {
    return new ListenableFutureAdapter<T, S>(futureToAdapt) {
      @Override
      protected T adapt(S objectToAdapt) throws ExecutionException {
        return adapter.adapt(objectToAdapt);
      }
    };
  }
}
