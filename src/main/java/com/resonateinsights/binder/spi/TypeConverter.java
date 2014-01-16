package com.resonateinsights.binder.spi;

import com.resonateinsights.binder.exception.NoTypeConversionAvailableException;

public interface TypeConverter {

  /**
   * Converts the value to the specified type
   *
   * @param type the requested type
   * @param value the value to be converted
   * @return the converted value, or <tt>null</tt> if not possible to convert
   */
  <T> T convertTo(Class<T> type, Object value);

  /**
   * Converts the value to the specified type in the context of an exchange
   * <p/>
   * Used when conversion requires extra information from the current
   * exchange (such as encoding).
   *
   * @param type the requested type
   * @param context the current context
   * @param value the value to be converted
   * @return the converted value, is never <tt>null</tt>
   * @throws NoTypeConversionAvailableException} if conversion not possible
   */
  <T> T convertTo(Class<T> type, Context context, Object value) throws NoTypeConversionAvailableException;

}
