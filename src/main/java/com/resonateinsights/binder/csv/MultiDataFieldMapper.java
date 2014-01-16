package com.resonateinsights.binder.csv;

import com.resonateinsights.binder.annotation.DataField;

import java.util.Map;

public interface MultiDataFieldMapper<K,V> {

  DataField generateDataField(Map.Entry<K,V> map);

}
