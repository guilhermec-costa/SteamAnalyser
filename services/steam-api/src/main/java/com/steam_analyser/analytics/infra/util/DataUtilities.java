package com.steam_analyser.analytics.infra.util;

import java.util.List;

import in.dragonbra.javasteam.types.KeyValue;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;

public class DataUtilities {

  public static <T> List<T> sortDatasetBy(List<T> dataset, String orderBy) {
    Collections.sort(dataset, new Comparator<T>() {
      @Override
      public int compare(T o1, T o2) {
        try {
          Field comparingField = o1.getClass().getDeclaredField(orderBy);
          comparingField.setAccessible(true);

          Comparable o1value = (Comparable) comparingField.get(o1);
          Comparable o2value = (Comparable) comparingField.get(o2);

          return o1value.compareTo(o2value);

        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });

    return dataset;
  }

  public static void printKeyValue(KeyValue keyValue, int depth) {
    String spacePadding = String.join("", Collections.nCopies(depth, "    "));

    if (keyValue.getChildren().isEmpty()) {
      System.out.println(spacePadding + keyValue.getName() + ": " + keyValue.getValue());
      return;
    }
    System.out.println(spacePadding + keyValue.getName() + ":");
    for (KeyValue child : keyValue.getChildren()) {
      printKeyValue(child, depth + 1);
    }
  }
}
