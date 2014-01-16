package com.resonateinsights.binder.spi;

public interface PackageScanFilter {

  /**
   * Does the given class match
   *
   * @param type the class
   * @return true to include this class, false to skip it.
   */
  boolean matches(Class<?> type);
}
