package com.resonateinsights.binder.format;

import com.resonateinsights.binder.Format;

public class BooleanFormat implements Format<Boolean> {

  @Override
  public String format(Boolean object) throws Exception {
    return object.toString();
  }

  @Override
  public Boolean parse(String string) throws Exception {
    return Boolean.parseBoolean(string);
  }
}
