package com.resonateinsights.binder.spi;

import com.resonateinsights.binder.annotation.CsvRecord;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class DefaultPackageScanClassResolver implements PackageScanClassResolver {
  @Override
  public void setClassLoaders(Set<ClassLoader> classLoaders) {

  }

  @Override
  public Set<ClassLoader> getClassLoaders() {
    return null;
  }

  @Override
  public void addClassLoader(ClassLoader classLoader) {

  }

  @Override
  public Set<Class<?>> findAnnotated(Class<? extends Annotation> annotation, String... packageNames) {
    HashSet<Class<? extends Annotation>> annotations = new HashSet<>();
    annotations.add(annotation);

    return findAnnotated(annotations);
  }

  @Override
  public Set<Class<?>> findAnnotated(Set<Class<? extends Annotation>> annotations, String... packageNames) {
    Set<Class<?>> classes = new HashSet<>();

    //TODO: User Reflecion configuration builder
    for (String packageName : packageNames) {
      for (Class<? extends Annotation> annotation : annotations) {
        Set<Class<?>> typesAnnotatedWith = new Reflections(packageName).getTypesAnnotatedWith(annotation);
        classes.addAll(typesAnnotatedWith);
      }
    }

    return classes;
  }

  @Override
  public Set<Class<?>> findImplementations(Class<?> parent, String... packageNames) {
    return null;
  }

  @Override
  public Set<Class<?>> findByFilter(PackageScanFilter filter, String... packageNames) {
    return null;
  }

  @Override
  public void addFilter(PackageScanFilter filter) {

  }

  @Override
  public void removeFilter(PackageScanFilter filter) {

  }
}
