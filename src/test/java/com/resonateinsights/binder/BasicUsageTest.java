package com.resonateinsights.binder;

import com.resonateinsights.binder.csv.BinderCsvDataFormat;
import com.resonateinsights.binder.model.PurchaseOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileOutputStream;

@RunWith(JUnit4.class)
public class BasicUsageTest {

  private static final String ModelPackage = "com.resonateinsights.binder.model";

  @Test
  public void PojoToCsv(){

    PurchaseOrder order = new PurchaseOrder();
    order.setId(123);
    order.setName("Camel in Action");
    order.setAmount(2);
    order.setOrderText("Please hurry");
    order.setSalesRef("Jane Doe");
    order.setCustomerRef("John Doe");

    File file = new File("C:\\Users\\pedro.alvarado\\zzzz.txt");

    try (FileOutputStream fop = new FileOutputStream(file)){
      BinderCsvDataFormat bindy = new BinderCsvDataFormat(ModelPackage);
      bindy.marshal(order, fop);
    }
    catch(Exception e){
      System.err.println("Error " + e.getMessage() + "\n" + e.getStackTrace());
    }

  }
}
