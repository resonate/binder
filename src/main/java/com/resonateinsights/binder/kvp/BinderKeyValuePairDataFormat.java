/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.resonateinsights.binder.kvp;


import com.resonateinsights.binder.BinderAbstractDataFormat;
import com.resonateinsights.binder.BinderAbstractFactory;
import com.resonateinsights.binder.BinderKeyValuePairFactory;
import com.resonateinsights.binder.ObjectHelper;
import com.resonateinsights.binder.spi.Context;
import com.resonateinsights.binder.spi.DataFormat;
import com.resonateinsights.binder.spi.PackageScanClassResolver;
import com.resonateinsights.binder.util.ConverterUtils;
import com.resonateinsights.binder.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

/**
 * A <a href="http://camel.apache.org/data-format.html">data format</a> (
 * {@link DataFormat}) using Binder to marshal to and from CSV files
 */
public class BinderKeyValuePairDataFormat extends BinderAbstractDataFormat {

  private static final Logger LOG = LoggerFactory.getLogger(BinderKeyValuePairDataFormat.class);

  public BinderKeyValuePairDataFormat() {
  }

  public BinderKeyValuePairDataFormat(String... packages) {
    super(packages);
  }

  public BinderKeyValuePairDataFormat(Class<?> type) {
    super(type);
  }

  @SuppressWarnings("unchecked")
  public void marshal(Context context, Object body, OutputStream outputStream) throws Exception {
    BinderAbstractFactory factory = getFactory(context.getPackageScanClassResolver());
    List<Map<String, Object>> models = (ArrayList<Map<String, Object>>)body;
    byte[] crlf;

    // Get CRLF
    crlf = ConverterUtils.getByteReturn(factory.getCarriageReturn());

    for (Map<String, Object> model : models) {
      String result = factory.unbind(model);
      byte[] bytes =context.getTypeConverter().convertTo(byte[].class, context, result);
      outputStream.write(bytes);

      // Add a carriage return
      outputStream.write(crlf);
    }
  }

  public Object unmarshal(Context context, InputStream inputStream) throws Exception {
    BinderKeyValuePairFactory factory = (BinderKeyValuePairFactory)getFactory(context.getPackageScanClassResolver());

    // List of Pojos
    List<Map<String, Object>> models = new ArrayList<Map<String, Object>>();

    // Pojos of the model
    Map<String, Object> model;

    // Map to hold the model @OneToMany classes while binding
    Map<String, List<Object>> lists = new HashMap<String, List<Object>>();

    InputStreamReader in = new InputStreamReader(inputStream, IOHelper.getCharsetName(context));

    // Scanner is used to read big file
    Scanner scanner = new Scanner(in);

    // Retrieve the pair separator defined to split the record
    ObjectHelper.notNull(factory.getPairSeparator(), "The pair separator property of the annotation @Message");
    String separator = factory.getPairSeparator();

    int count = 0;
    try {
      while (scanner.hasNextLine()) {
        // Read the line
        String line = scanner.nextLine().trim();

        if (ObjectHelper.isEmpty(line)) {
          // skip if line is empty
          continue;
        }

        // Increment counter
        count++;

        // Create POJO
        model = factory.factory();

        // Split the message according to the pair separator defined in
        // annotated class @Message
        List<String> result = Arrays.asList(line.split(separator));

        if (result.size() == 0 || result.isEmpty()) {
          throw new java.lang.IllegalArgumentException("No records have been defined in the KVP");
        }

        if (result.size() > 0) {
          // Bind data from message with model classes
          // Counter is used to detect line where error occurs
          factory.bind(result, model, count, lists);

          // Link objects together
          factory.link(model);

          // Add objects graph to the list
          models.add(model);

          LOG.debug("Graph of objects created: {}", model);
        }
      }

      // Test if models list is empty or not
      // If this is the case (correspond to an empty stream, ...)
      if (models.size() == 0) {
        throw new java.lang.IllegalArgumentException("No records have been defined in the CSV");
      } else {
        return extractUnmarshalResult(models);
      }

    } finally {
      scanner.close();
      IOHelper.close(in, "in", LOG);
    }
  }

  protected BinderAbstractFactory createModelFactory(PackageScanClassResolver resolver) throws Exception {
    if (getClassType() != null) {
      return new BinderKeyValuePairFactory(resolver, getClassType());
    } else {
      return new BinderKeyValuePairFactory(resolver, getPackages());
    }
  }
}