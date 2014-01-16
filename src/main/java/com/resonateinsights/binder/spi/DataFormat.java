package com.resonateinsights.binder.spi;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a
 * <a href="http://camel.apache.org/data-format.html">data format</a>
 * used to marshal objects to and from streams
 * such as Java Serialization or using JAXB2 to encode/decode objects using XML
 * or using SOAP encoding.
 *
 * @version $Revision: 738604 $
 */
public interface DataFormat {

  /**
   * Marshals the object to the given Stream.
   *
   * @param context  the current context of operation
   * @param graph     the object to be marshalled
   * @param stream    the output stream to write the marshalled rersult to
   * @throws Exception can be thrown
   */
  void marshal(Context context, Object graph, OutputStream stream) throws Exception;

  /**
   * Unmarshals the given stream into an object.
   * <p/>
   * <b>Notice:</b> The result is set as body on the exchange OUT message.
   * It is possible to mutate the OUT message provided in the given exchange parameter.
   * For instance adding headers to the OUT message will be preserved.
   *
   * @param context    the current context of operation
   * @param stream      the input stream with the object to be unmarshalled
   * @return            the unmarshalled object
   * @throws Exception can be thrown
   */
  Object unmarshal(Context context, InputStream stream) throws Exception;
}