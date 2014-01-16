package com.resonateinsights.binder.csv;

import com.resonateinsights.binder.annotation.DataField;

import java.util.Map;

public class DefaultMultiDataFieldMapper<K,V> implements MultiDataFieldMapper<K,V> {
  @Override
  public DataField generateDataField(Map.Entry<K,V> map) {
    DataFieldQualifier dataFieldQualifier = new DataFieldQualifier();
    dataFieldQualifier.setColumnName(map.getKey().toString());
    return dataFieldQualifier;
  }
}
