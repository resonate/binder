package com.resonateinsights.binder.spi;

import java.nio.charset.Charset;

public class DefaultContext implements Context {
  private String charSet;

  @Override
  public TypeConverter getTypeConverter() {
    return new DefaultTypeConverter();
  }

  @Override
  public PackageScanClassResolver getPackageScanClassResolver() {
    return new DefaultPackageScanClassResolver();
  }

  @Override
  public String getCharsetName() {
    if(charSet != null)
      return charSet;

    return charSet = Charset.defaultCharset().name();
  }
}
