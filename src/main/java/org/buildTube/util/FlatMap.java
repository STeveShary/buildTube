package org.buildTube.util;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FlatMap<T> extends SettableListenableFuture<List<T>> {

  private final List<? extends ListenableFuture<T>> futuresToCombine;
  private final Map<Integer, T> combinedResults;
  private final List<Throwable> exceptions;
  private final int futuresSize;

  public FlatMap(List<? extends ListenableFuture<T>> futuresToCombine) {
    this.futuresToCombine = futuresToCombine;
    combinedResults = new ConcurrentHashMap<>();
    exceptions = new Vector<>();
    futuresSize = futuresToCombine.size();
  }

  public void subscribeToFutures() {
    if (futuresSize == 0) {
      finishFuture();
    } else {
      registerFutures();
    }
  }


  private void registerFutures() {
    for (ListenableFuture<T> future : futuresToCombine) {
      final int futurePosition = futuresToCombine.indexOf(future);
      future.addCallback(
          result -> {
            combinedResults.put(futurePosition, result);
            if (combinedResults.size() + exceptions.size() == this.futuresSize) {
              finishFuture();
            }
          },
          (e) -> {
            exceptions.add(e);
            if (combinedResults.size() + exceptions.size() == this.futuresSize) {
              finishFuture();
            }
          });
    }
  }

  private void finishFuture() {
    ArrayList<Integer> sortedKeys = new ArrayList<>(combinedResults.keySet());
    Collections.sort(sortedKeys);
    this.set(sortedKeys.stream().map(combinedResults::get).collect(Collectors.toList()));
  }
}
