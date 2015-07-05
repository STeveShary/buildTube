package org.buildTube.util;


@FunctionalInterface
public interface FunctionalFutureAdapter<R, S> {
  S adapt(R input);
}
