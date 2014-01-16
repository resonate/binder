package com.resonateinsights.binder.spi;

public interface Context {

  TypeConverter getTypeConverter();

  PackageScanClassResolver getPackageScanClassResolver();

  /**
   * Returns the name of the Charset
   *
   * @return the name of the Charset
   */
  String getCharsetName();

}
