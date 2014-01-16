package com.resonateinsights.binder.annotation;


import com.resonateinsights.binder.csv.DefaultMultiDataFieldMapper;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiDataField {

  Class<?> dataFieldGenerator() default DefaultMultiDataFieldMapper.class;

}
