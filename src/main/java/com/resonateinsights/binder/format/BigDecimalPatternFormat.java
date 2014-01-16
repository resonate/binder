package com.resonateinsights.binder.format;

import java.math.BigDecimal;
import java.util.Locale;

public class BigDecimalPatternFormat extends NumberPatternFormat<BigDecimal> {

  public BigDecimalPatternFormat(String pattern, Locale locale) {
    super(pattern, locale);
  }

  @Override
  public BigDecimal parse(String string) throws Exception {
    if (getNumberFormat() != null) {
      return new BigDecimal(getNumberFormat().parse(string).doubleValue());
    } else {
      return new BigDecimal(string);
    }
  }
}
