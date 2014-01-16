package com.resonateinsights.binder.spi;

import com.resonateinsights.binder.exception.NoTypeConversionAvailableException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DefaultTypeConverter implements TypeConverter {
  @Override
  public <T> T convertTo(Class<T> type, Object value) {
    //Try converting to string
    T string = convertToString(type, value);
    if(string!=null){
      return string;
    }

    //Try converting to byte[] if value is string
    T byteArray = convertToByteArrayFromString(type, value);
    if(byteArray != null){
      return byteArray;
    }

    //Try converting to array
    T array = convertToArray(type, value);
    if(array!=null){
      return array;
    }

    //Can't convert return null
    return null;
  }

  @Override
  public <T> T convertTo(Class<T> type, Context context, Object value) throws NoTypeConversionAvailableException {
    return convertTo(type, value);
  }

  public <T> T convertToByteArrayFromString(Class<T> type, Object value){
    if (value != null) {
      //Try to convert to string
      if (type.equals(byte[].class) && (value instanceof String)) {
        return (T)((String) value).getBytes();
      }
    }

    return null;
  }

  public <T> T convertToString(Class<T> type, Object value){
    if (value != null) {
      //Try to convert to string
      if (type.equals(String.class)) {
        return (T)value.toString();
      }
    }

    return null;
  }

  public <T> T convertToArray(Class<T> type, Object value){
    if (type.isArray()) {
      if (value instanceof Collection) {
        Collection collection = (Collection)value;
        Object array = Array.newInstance(type.getComponentType(), collection.size());
        if (array instanceof Object[]) {
          collection.toArray((Object[])array);
        } else {
          int index = 0;
          for (Object element : collection) {
            Array.set(array, index++, element);
          }
        }
        return (T)array;
      } else if (value != null && value.getClass().isArray()) {
        int size = Array.getLength(value);
        Object answer = Array.newInstance(type.getComponentType(), size);
        for (int i = 0; i < size; i++) {
          Array.set(answer, i, Array.get(value, i));
        }
        return (T)answer;
      }
    } else if (Collection.class.isAssignableFrom(type)) {
      if (value != null) {
        if (value instanceof Object[]) {
          return (T) Arrays.asList((Object[]) value);
        } else if (value.getClass().isArray()) {
          int size = Array.getLength(value);
          List answer = new ArrayList(size);
          for (int i = 0; i < size; i++) {
            answer.add(Array.get(value, i));
          }
          return (T)answer;
        }
      }
    }
    return null;
  }
}
