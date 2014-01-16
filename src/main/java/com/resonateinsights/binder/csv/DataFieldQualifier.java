package com.resonateinsights.binder.csv;

import com.resonateinsights.binder.annotation.DataField;
import com.resonateinsights.binder.util.AnnotationLiteral;

public class DataFieldQualifier extends AnnotationLiteral<DataField> implements DataField {

  private int pos;
  private String name;
  private String columnName;
  private String pattern;
  private int length;
  private int lengthPos;
  private String align;
  private char paddingChar;
  private int precision;
  private int position;
  private boolean required;
  private boolean trim;
  private boolean clip;
  private boolean impliedDecimalSeparator;
  private String delimiter;
  private String defaultValue;
  private static DataField annotationDefaultValues = temp.class.getAnnotation(DataField.class);

  @DataField(pos = 0) class temp{}

  public DataFieldQualifier() {
    this.pos = annotationDefaultValues.pos();
    this.name = annotationDefaultValues.name();
    this.columnName = annotationDefaultValues.columnName();
    this.pattern = annotationDefaultValues.pattern();
    this.length = annotationDefaultValues.length();
    this.lengthPos = annotationDefaultValues.lengthPos();
    this.align = annotationDefaultValues.align();
    this.paddingChar = annotationDefaultValues.paddingChar();
    this.precision = annotationDefaultValues.precision();
    this.position = annotationDefaultValues.position();
    this.required = annotationDefaultValues.required();
    this.trim = annotationDefaultValues.trim();
    this.clip = annotationDefaultValues.clip();
    this.impliedDecimalSeparator = annotationDefaultValues.impliedDecimalSeparator();
    this.delimiter = annotationDefaultValues.delimiter();
    this.defaultValue = annotationDefaultValues.defaultValue();

  }

  public DataFieldQualifier(int pos, String name, String columnName, String pattern, int length, int lengthPos, String align, char paddingChar, int precision, int position, boolean required, boolean trim, boolean clip, boolean impliedDecimalSeparator, String delimeter, String defaultValue) {
    this.pos = pos;
    this.name = name;
    this.columnName = columnName;
    this.pattern = pattern;
    this.length = length;
    this.lengthPos = lengthPos;
    this.align = align;
    this.paddingChar = paddingChar;
    this.precision = precision;
    this.position = position;
    this.required = required;
    this.trim = trim;
    this.clip = clip;
    this.impliedDecimalSeparator = impliedDecimalSeparator;
    this.delimiter = delimeter;
    this.defaultValue = defaultValue;
  }

  public void setPos(int pos) {
    this.pos = pos;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public void setLengthPos(int lengthPos) {
    this.lengthPos = lengthPos;
  }

  public void setAlign(String align) {
    this.align = align;
  }

  public void setPaddingChar(char paddingChar) {
    this.paddingChar = paddingChar;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public void setTrim(boolean trim) {
    this.trim = trim;
  }

  public void setClip(boolean clip) {
    this.clip = clip;
  }

  public void setImpliedDecimalSeparator(boolean impliedDecimalSeparator) {
    this.impliedDecimalSeparator = impliedDecimalSeparator;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  @Override
  public int pos() {
    return pos;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String columnName() {
    return columnName;
  }

  @Override
  public String pattern() {
    return pattern;
  }

  @Override
  public int length() {
    return length;
  }

  @Override
  public int lengthPos() {
    return lengthPos;
  }

  @Override
  public String align() {
    return align;
  }

  @Override
  public char paddingChar() {
    return paddingChar;
  }

  @Override
  public int precision() {
    return precision;
  }

  @Override
  public int position() {
    return position;
  }

  @Override
  public boolean required() {
    return required;
  }

  @Override
  public boolean trim() {
    return trim;
  }

  @Override
  public boolean clip() {
    return clip;
  }

  @Override
  public String delimiter() {
    return delimiter;
  }

  @Override
  public String defaultValue() {
    return defaultValue;
  }

  @Override
  public boolean impliedDecimalSeparator() {
    return impliedDecimalSeparator;
  }
}
