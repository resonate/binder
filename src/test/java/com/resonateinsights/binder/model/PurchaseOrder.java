package com.resonateinsights.binder.model;

import com.resonateinsights.binder.annotation.CsvRecord;
import com.resonateinsights.binder.annotation.DataField;

@CsvRecord(separator = ",", generateHeaderColumns = true)
public class PurchaseOrder {

  @DataField(pos = 1)
  private int id;

  @DataField(pos = 2)
  private String name;

  @DataField(pos = 3)
  private int amount;

  @DataField(pos = 4, required = false)
  private String orderText;

  @DataField(pos = 5, required = false)
  private String salesRef;

  @DataField(pos = 6, required = false)
  private String customerRef;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getOrderText() {
    return orderText;
  }

  public void setOrderText(String text) {
    this.orderText = text;
  }

  public String getSalesRef() {
    return salesRef;
  }

  public void setSalesRef(String salesRef) {
    this.salesRef = salesRef;
  }

  public String getCustomerRef() {
    return customerRef;
  }

  public void setCustomerRef(String customerRef) {
    this.customerRef = customerRef;
  }
}
