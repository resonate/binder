package com.resonateinsights.binder.format;

import com.resonateinsights.binder.PatternFormat;

public class BooleanPatternFormat implements PatternFormat<Boolean> {
  private String pattern;
  private String falsePattern;
  private String truePattern;

  public BooleanPatternFormat(String pattern) {
    this.setPattern(pattern);
  }

  @Override
  public String format(Boolean value) throws Exception {
    return value == null ? ""
                         : value ? truePattern
                                 : falsePattern;

  }

  @Override
  public Boolean parse(String value) throws Exception {
    String trimmedValue = value.trim();
    if(truePattern.equalsIgnoreCase(trimmedValue)){
      return Boolean.TRUE;
    }
    if (falsePattern.equalsIgnoreCase(trimmedValue)){
      return Boolean.FALSE;
    }
    return null;
  }

  @Override
  public String getPattern() {
    return pattern;
  }

  /**
   * Sets the pattern
   *
   * @param pattern the pattern
   */
  public void setPattern(String pattern) {
    String[] valuePatterns = pattern.split(":");
    if (valuePatterns.length != 2) {
      throw new IllegalArgumentException("Unable to create boolean format from " + pattern + ". The pattern must contain a single ':' character");
    }
    this.truePattern = valuePatterns[0];
    this.falsePattern = valuePatterns[1];
    this.pattern = pattern;
  }
}
