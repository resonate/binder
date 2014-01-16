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
package com.resonateinsights.binder;


import com.resonateinsights.binder.annotation.*;
import com.resonateinsights.binder.csv.MultiDataFieldMapper;
import com.resonateinsights.binder.format.FormatException;
import com.resonateinsights.binder.spi.PackageScanClassResolver;
import com.resonateinsights.binder.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;


/**
 * The BinderCsvFactory is the class who allows to : Generate a model associated
 * to a CSV record, bind data from a record to the POJOs, export data of POJOs
 * to a CSV record and format data into String, Date, Double, ... according to
 * the format/pattern defined
 */
public class BinderCsvFactory extends BinderAbstractFactory implements BinderFactory {

  private static final Logger LOG = LoggerFactory.getLogger(BinderCsvFactory.class);

  boolean isOneToMany;

  private Map<Integer, DataField> dataFields = new LinkedHashMap<Integer, DataField>();
  private Map<Integer, Field> annotatedFields = new LinkedHashMap<Integer, Field>();
  private Map<String, Integer> sections = new HashMap<String, Integer>();
  Map<Field, MultiDataField> multiDataFields = new HashMap<Field, MultiDataField>();
  Map<Class<?>, Field> oneToManyFields = new HashMap<Class<?>, Field>();

  private int numberOptionalFields;
  private int numberMandatoryFields;
  private int totalFields;

  private String separator;
  private boolean skipFirstLine;
  private boolean generateHeaderColumnNames;
  private boolean messageOrdered;
  private String quote;
  private boolean quoting;

  public BinderCsvFactory(PackageScanClassResolver resolver, String... packageNames) throws Exception {
    super(resolver, packageNames);

    // initialize specific parameters of the csv model
    initCsvModel();
  }

  public BinderCsvFactory(PackageScanClassResolver resolver, Class<?> type) throws Exception {
    super(resolver, type);

    // initialize specific parameters of the csv model
    initCsvModel();
  }

  /**
   * method uses to initialize the model representing the classes who will
   * bind the data. This process will scan for classes according to the
   * package name provided, check the annotated classes and fields and
   * retrieve the separator of the CSV record
   *
   * @throws Exception
   */
  public void initCsvModel() throws Exception {

    // Find annotated Datafields declared in the Model classes
    initAnnotatedFields();

    // initialize Csv parameter(s)
    // separator and skip first line from @CSVrecord annotation
    initCsvRecordParameters();
  }

  public void initAnnotatedFields() {

    int maxpos = 0;
    for (Class<?> cl : models) {
      List<Field> linkFields = new ArrayList<Field>();

      if (LOG.isDebugEnabled()) {
        LOG.debug("Class retrieved: {}", cl.getName());
      }

      for (Field field : cl.getDeclaredFields()) {
        DataField dataField = field.getAnnotation(DataField.class);
        if (dataField != null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Position defined in the class: {}, position: {}, Field: {}",
                    new Object[]{cl.getName(), dataField.pos(), dataField});
          }

          if (dataField.required()) {
            ++numberMandatoryFields;
          } else {
            ++numberOptionalFields;
          }

          int pos = dataField.pos();
          if (annotatedFields.containsKey(pos)) {
            Field f = annotatedFields.get(pos);
            LOG.warn("Potentially invalid model: existing @DataField '{}' replaced by '{}'", f.getName(), field.getName());
          }
          dataFields.put(pos, dataField);
          annotatedFields.put(pos, field);
          maxpos = Math.max(maxpos, pos);
        }

        Link linkField = field.getAnnotation(Link.class);

        if (linkField != null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Class linked: {}, Field: {}", cl.getName(), field);
          }
          linkFields.add(field);
        }

        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if(oneToMany != null){
          isOneToMany = true;
          oneToManyFields.put(cl, field);
        }

        MultiDataField multiDataField = field.getAnnotation(MultiDataField.class);
        if(multiDataField != null){
          multiDataFields.put(field, multiDataField);
        }

      }

      if (!linkFields.isEmpty()) {
        annotatedLinkFields.put(cl.getName(), linkFields);
      }

      totalFields = numberMandatoryFields + numberOptionalFields;

      if (LOG.isDebugEnabled()) {
        LOG.debug("Number of optional fields: {}", numberOptionalFields);
        LOG.debug("Number of mandatory fields: {}", numberMandatoryFields);
        LOG.debug("Total: {}", totalFields);
      }
    }

    if (annotatedFields.size() < maxpos) {
      LOG.info("Potentially incomplete model: some csv fields may not be mapped to @DataField members");
    }
  }

  public void bind(List<String> tokens, Map<String, Object> model, int line) throws Exception {

    int pos = 1;
    int counterMandatoryFields = 0;

    for (String data : tokens) {

      // Get DataField from model
      DataField dataField = dataFields.get(pos);
      ObjectHelper.notNull(dataField, "No position " + pos + " defined for the field: " + data + ", line: " + line);

      if (dataField.trim()) {
        data = data.trim();
      }

      if (dataField.required()) {
        // Increment counter of mandatory fields
        ++counterMandatoryFields;

        // Check if content of the field is empty
        // This is not possible for mandatory fields
        if (data.equals("")) {
          throw new IllegalArgumentException("The mandatory field defined at the position " + pos + " is empty for the line: " + line);
        }
      }

      // Get Field to be setted
      Field field = annotatedFields.get(pos);
      field.setAccessible(true);

      if (LOG.isDebugEnabled()) {
        LOG.debug("Pos: {}, Data: {}, Field type: {}", new Object[]{pos, data, field.getType()});
      }

      // Create format object to format the field
      Format<?> format = FormatFactory.getFormat(field.getType(), getLocale(), dataField);

      // field object to be set
      Object modelField = model.get(field.getDeclaringClass().getName());

      // format the data received
      Object value = null;

      if (!data.equals("")) {
        try {
          value = format.parse(data);
        } catch (FormatException ie) {
          throw new IllegalArgumentException(ie.getMessage() + ", position: " + pos + ", line: " + line, ie);
        } catch (Exception e) {
          throw new IllegalArgumentException("Parsing error detected for field defined at the position: " + pos + ", line: " + line, e);
        }
      } else {
        if (!dataField.defaultValue().isEmpty()) {
          value = format.parse(dataField.defaultValue());
        } else {
          value = getDefaultValueForPrimitive(field.getType());
        }
      }

      field.set(modelField, value);

      ++pos;

    }

    LOG.debug("Counter mandatory fields: {}", counterMandatoryFields);

    if (counterMandatoryFields < numberMandatoryFields) {
      throw new IllegalArgumentException("Some mandatory fields are missing, line: " + line);
    }

    if (pos < totalFields) {
      setDefaultValuesForFields(model);
    }

  }

  public String unbind(Map<String, Object> model) throws Exception {

    StringBuilder buffer = new StringBuilder();
    Map<Integer, List<String>> results = new HashMap<Integer, List<String>>();

    // Check if separator exists
    ObjectHelper.notNull(this.separator, "The separator has not been instantiated or property not defined in the @CsvRecord annotation");

    char separator = ConverterUtils.getCharDelimiter(this.getSeparator());

    if (LOG.isDebugEnabled()) {
      LOG.debug("Separator converted: '0x{}', from: {}", Integer.toHexString(separator), this.getSeparator());
    }

    for (Class<?> clazz : models) {
      if (model.containsKey(clazz.getName())) {

        Object obj = model.get(clazz.getName());
        if (LOG.isDebugEnabled()) {
          LOG.debug("Model object: {}, class: {}", obj, obj.getClass().getName());
        }
        if (obj != null) {

          // Generate Csv table
          generateCsvPositionMap(clazz, obj, results);

        }
      }
    }

    // Transpose result
    List<List<String>> l = new ArrayList<List<String>>();
    if (isOneToMany) {
      l = product(results);
    } else {
      // Convert Map<Integer, List> into List<List>
      TreeMap<Integer, List<String>> sortValues = new TreeMap<Integer, List<String>>(results);
      List<String> temp = new ArrayList<String>();

      for (Entry<Integer, List<String>> entry : sortValues.entrySet()) {
        // Get list of values
        List<String> val = entry.getValue();

        // For one to one relation
        // There is only one item in the list
        String value = val.get(0);

        // Add the value to the temp array
        if (value != null) {
          temp.add(value);
        } else {
          temp.add("");
        }
      }

      l.add(temp);
    }

    if (l != null) {
      Iterator<List<String>> it = l.iterator();
      while (it.hasNext()) {
        List<String> tokens = it.next();
        Iterator<String> itx = tokens.iterator();

        while (itx.hasNext()) {
          String res = itx.next();
          if (res != null) {
            // the field may be enclosed in quotes if a quote was configured
            if (quoting && quote != null) {
              buffer.append(quote);
            }
            buffer.append(res);
            if (quoting && quote != null) {
              buffer.append(quote);
            }
          }

          if (itx.hasNext()) {
            buffer.append(separator);
          }
        }

        if (it.hasNext()) {
          buffer.append(ConverterUtils.getStringCarriageReturn(getCarriageReturn()));
        }
      }
    }

    return buffer.toString();
  }

  private List<List<String>> product(Map<Integer, List<String>> values) {
    TreeMap<Integer, List<String>> sortValues = new TreeMap<Integer, List<String>>(values);

    List<List<String>> product = new ArrayList<List<String>>();
    Map<Integer, Integer> index = new HashMap<Integer, Integer>();

    int idx = 0;
    int idxSize = 0;
    do {
      idxSize = 0;
      List<String> v = new ArrayList<String>();

      for (int ii = 1; ii <= sortValues.lastKey(); ii++) {
        List<String> l = values.get(ii);
        if (l == null) {
          v.add("");
          ++idxSize;
          continue;
        }

        if (l.size() >= idx + 1) {
          v.add(l.get(idx));
          index.put(ii, idx);
          if (LOG.isDebugEnabled()) {
            LOG.debug("Value: {}, pos: {}, at: {}", new Object[]{l.get(idx), ii, idx});
          }
        } else {
          v.add(l.get(0));
          index.put(ii, 0);
          ++idxSize;
          if (LOG.isDebugEnabled()) {
            LOG.debug("Value: {}, pos: {}, at index: {}", new Object[]{l.get(0), ii, 0});
          }
        }
      }

      if (idxSize != sortValues.lastKey()) {
        product.add(v);
      }
      ++idx;

    } while (idxSize != sortValues.lastKey());

    return product;
  }

  /**
   *
   * Generate a table containing the data formatted and sorted with their position/offset
   * If the model is Ordered than a key is created combining the annotation @Section and Position of the field
   * If a relation @OneToMany is defined, than we iterate recursively through this function
   * The result is placed in the Map<Integer, List> results
   */
  private void generateCsvPositionMap(Class<?> clazz, Object obj, Map<Integer, List<String>> results) throws Exception {

    String result = "";
    Map<Field, MultiDataField> multiValueFields = new HashMap<Field, MultiDataField>();
    for (Field field : clazz.getDeclaredFields()) {

      field.setAccessible(true);

      DataField datafield = field.getAnnotation(DataField.class);

      if (datafield != null) {

        if (obj != null) {

          // Retrieve the format, pattern and precision associated to the type
          Class<?> type = field.getType();

          // Create format
          Format<?> format = FormatFactory.getFormat(type, getLocale(), datafield);

          // Get field value
          Object value = field.get(obj);

          result = formatString(format, value);

          if (datafield.trim()) {
            result = result.trim();
          }

          if (datafield.clip() && result.length() > datafield.length()) {
            result = result.substring(0, datafield.length());
          }

          if (LOG.isDebugEnabled()) {
            LOG.debug("Value to be formatted: {}, position: {}, and its formatted value: {}", new Object[]{value, datafield.pos(), result});
          }

        } else {
          result = "";
        }

        Integer key;

        if (isMessageOrdered() && obj != null) {

          // Generate a key using the number of the section
          // and the position of the field
          Integer key1 = sections.get(obj.getClass().getName());
          Integer key2 = datafield.position();
          Integer keyGenerated = generateKey(key1, key2);

          if (LOG.isDebugEnabled()) {
            LOG.debug("Key generated: {}, for section: {}", String.valueOf(keyGenerated), key1);
          }

          key = keyGenerated;

        } else {
          key = datafield.pos();
        }

        if (!results.containsKey(key)) {
          List<String> list = new LinkedList<String>();
          list.add(result);
          results.put(key, list);
        } else {
          List<String> list = results.get(key);
          list.add(result);
        }

      }

      OneToMany oneToMany = field.getAnnotation(OneToMany.class);
      if (oneToMany != null) {

        // Set global variable
        // Will be used during generation of CSV
        isOneToMany = true;

        List<?> list = (List<?>)field.get(obj);
        if (list != null) {

          Iterator<?> it = list.iterator();
          while (it.hasNext()) {
            Object target = it.next();
            generateCsvPositionMap(target.getClass(), target, results);
          }

        } else {

          // Call this function to add empty value
          // in the table
          generateCsvPositionMap(field.getClass(), null, results);
        }

      }

      MultiDataField multiDataField = field.getAnnotation(MultiDataField.class);

      if(multiDataField != null){
        multiValueFields.put(field, multiDataField);
      }
    }

    if(multiValueFields.size() > 0){

      int maxpos = 0;
      int highestItems = 0;
      for (Integer pos : results.keySet()) {
        int currentItemSize = results.get(pos).size();
        if(currentItemSize >= highestItems){
          highestItems = currentItemSize;
          maxpos = Math.max(maxpos, pos);
        }

      }

      for (Entry<Field, MultiDataField> multiValueField : multiValueFields.entrySet()) {
        //Next maxpos;


        Field field = multiValueField.getKey();
        // Get field value
        //Add check to make sure it is the right instance type
        Map<?, ?> map = (Map<?, ?>) field.get(obj);
        MultiDataField fieldAnnotation = multiValueField.getValue();
        MultiDataFieldMapper multiDataFieldMapper = (MultiDataFieldMapper) ObjectHelper.newInstance(fieldAnnotation.dataFieldGenerator());

        for (Entry<?, ?> entry : map.entrySet()) {
          DataField dataField = multiDataFieldMapper.generateDataField(entry);
          if(dataField == null){
            continue;
          }
		  maxpos++;
          processMultiValueField(maxpos, entry, dataField, results);
        }
      }
    }

  }

  private void processMultiValueField(int pos, Entry<?,?> entry, DataField datafield, Map<Integer, List<String>> results)throws Exception {
    String result = "";

    // Get field value
    Object value = entry.getValue();

    // Retrieve the format, pattern and precision associated to the type
    Class<?> type = value.getClass();

    // Create format
    Format<?> format = FormatFactory.getFormat(type, getLocale(), datafield);

    result = formatString(format, value);

    if (!results.containsKey(pos)) {
      List<String> list = new LinkedList<String>();
      list.add(result);
      results.put(pos, list);
    } else {
      List<String> list = results.get(pos);
      list.add(result);
    }
  }

  /**
   * Generate for the first line the headers of the columns
   *
   * @return the headers columns
   */
  public String generateHeader(Object body) throws IllegalAccessException {

    Map<Integer, DataField> dataFieldsSorted = new TreeMap<Integer, DataField>(dataFields);
    Iterator<Integer> it = dataFieldsSorted.keySet().iterator();

    StringBuilder builderHeader = new StringBuilder();

    while (it.hasNext()) {

      DataField dataField = dataFieldsSorted.get(it.next());

      // Retrieve the field
      Field field = annotatedFields.get(dataField.pos());
      // Change accessibility to allow to read protected/private fields
      field.setAccessible(true);

      // Get dataField
      if (!dataField.columnName().equals("")) {
        builderHeader.append(dataField.columnName());
      } else {
        builderHeader.append(field.getName());
      }

      if (it.hasNext()) {
        builderHeader.append(separator);
      }

    }

    Iterator<Entry<Field, MultiDataField>> iterator = multiDataFields.entrySet().iterator();

    while(iterator.hasNext()){

      Entry<Field, MultiDataField> next = iterator.next();
      Field field = next.getKey();
      field.setAccessible(true);
      Map<?, ?> map = null;
      try {
        map = (Map<?,?>)field.get(body);
      } catch (IllegalArgumentException e) {
        //We cant find the field, lets attempt to find it in a one-to-many child
        if(isOneToMany){
          Field oneToManyField = oneToManyFields.get(body.getClass());
          oneToManyField.setAccessible(true);
          List<?> list = (List<?>)oneToManyField.get(body);
          map = (Map<?,?>)field.get(list.get(0));
        }else{
          throw e;
        }
      }
      builderHeader.append(separator);
      MultiDataField multiDataField = next.getValue();//
      MultiDataFieldMapper multiDataFieldMapper = (MultiDataFieldMapper) ObjectHelper.newInstance(multiDataField.dataFieldGenerator());

      Iterator<? extends Entry<?, ?>> mapIter = map.entrySet().iterator();
      while(mapIter.hasNext()){
        DataField dataField = multiDataFieldMapper.generateDataField(mapIter.next());
        if(dataField == null){
          continue;
        }
        builderHeader.append(dataField.columnName());
        if (mapIter.hasNext()) {
          builderHeader.append(separator);
        }
      }

      if (iterator.hasNext()) {
        builderHeader.append(separator);
      }
    }

    return builderHeader.toString();
  }

  /**
   * Get parameters defined in @CsvRecord annotation
   */
  private void initCsvRecordParameters() {
    if (separator == null) {
      for (Class<?> cl : models) {

        // Get annotation @CsvRecord from the class
        CsvRecord record = cl.getAnnotation(CsvRecord.class);

        // Get annotation @Section from the class
        Section section = cl.getAnnotation(Section.class);

        if (record != null) {
          LOG.debug("Csv record: {}", record);

          // Get skipFirstLine parameter
          skipFirstLine = record.skipFirstLine();
          LOG.debug("Skip First Line parameter of the CSV: {}" + skipFirstLine);

          // Get generateHeaderColumnNames parameter
          generateHeaderColumnNames = record.generateHeaderColumns();
          LOG.debug("Generate header column names parameter of the CSV: {}", generateHeaderColumnNames);

          // Get Separator parameter
          ObjectHelper.notNull(record.separator(), "No separator has been defined in the @Record annotation");
          separator = record.separator();
          LOG.debug("Separator defined for the CSV: {}", separator);

          // Get carriage return parameter
          crlf = record.crlf();
          LOG.debug("Carriage return defined for the CSV: {}", crlf);

          // Get isOrdered parameter
          messageOrdered = record.isOrdered();
          LOG.debug("Must CSV record be ordered: {}", messageOrdered);

          if (ObjectHelper.isNotEmpty(record.quote())) {
            quote = record.quote();
            LOG.debug("Quoting columns with: {}", quote);
          }

          quoting = record.quoting();
          LOG.debug("CSV will be quoted: {}", messageOrdered);
        }

        if (section != null) {
          // Test if section number is not null
          ObjectHelper.notNull(section.number(), "No number has been defined for the section");

          // Get section number and add it to the sections
          sections.put(cl.getName(), section.number());
        }
      }
    }
  }
  /**
   * Set the default values for the non defined fields.
   * @param model the model which has its default fields set.
   * @throws IllegalAccessException if the underlying fields are inaccessible
   * @throws Exception In case the field cannot be parsed
   */
  private void setDefaultValuesForFields(final Map<String, Object> model) throws IllegalAccessException,
          Exception {
    // Set the default values, if defined
    for (int i = 1; i <= dataFields.size(); i++) {
      Field field = annotatedFields.get(i);
      field.setAccessible(true);
      DataField dataField = dataFields.get(i);
      Object modelField = model.get(field.getDeclaringClass().getName());
      if (field.get(modelField) == null && !dataField.defaultValue().isEmpty()) {
        Format<?> format = FormatFactory.getFormat(field.getType(), getLocale(), dataField);
        Object value = format.parse(dataField.defaultValue());
        field.set(modelField, value);
      }
    }
  }

  /**
   * Find the separator used to delimit the CSV fields
   */
  public String getSeparator() {
    return separator;
  }

  /**
   * Flag indicating if the first line of the CSV must be skipped
   */
  public boolean getGenerateHeaderColumnNames() {
    return generateHeaderColumnNames;
  }

  /**
   * Find the separator used to delimit the CSV fields
   */
  public boolean getSkipFirstLine() {
    return skipFirstLine;
  }

  /**
   * Flag indicating if the message must be ordered
   *
   * @return boolean
   */
  public boolean isMessageOrdered() {
    return messageOrdered;
  }

  public String getQuote() {
    return quote;
  }
}
