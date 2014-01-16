package com.resonateinsights.binder.util;

import com.resonateinsights.binder.annotation.*;
import com.resonateinsights.binder.spi.PackageScanClassResolver;
import com.resonateinsights.binder.spi.PackageScanFilter;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Annotation based loader for model classes with Binder annotations.
 */
public class AnnotationModelLoader {

  private PackageScanClassResolver resolver;
  private PackageScanFilter filter;
  private Set<Class<? extends Annotation>> annotations;

  public AnnotationModelLoader(PackageScanClassResolver resolver) {
    this.resolver = resolver;

    annotations = new LinkedHashSet<Class<? extends Annotation>>();
    annotations.add(CsvRecord.class);
    annotations.add(Link.class);
    annotations.add(Message.class);
    annotations.add(Section.class);
    annotations.add(FixedLengthRecord.class);
  }

  public AnnotationModelLoader(PackageScanClassResolver resolver, PackageScanFilter filter) {
    this(resolver);
    this.filter = filter;
  }

  public Set<Class<?>> loadModels(String... packageNames) throws Exception {
    Set<Class<?>> results = resolver.findAnnotated(annotations, packageNames);

    //TODO;  this logic could be moved into the PackageScanClassResolver by creating:
    //          findAnnotated(annotations, packageNames, filter)
    Set<Class<?>> resultsToRemove = new HashSet<Class<?>>();
    if (filter != null) {
      for (Class<?> clazz : results) {
        if (!filter.matches(clazz)) {
          resultsToRemove.add(clazz);
        }
      }
    }
    results.removeAll(resultsToRemove);
    return results;
  }

}