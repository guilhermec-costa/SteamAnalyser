package com.steam_analyser.analytics.util;

import java.text.NumberFormat;
import java.util.Locale;

public class Formmater {
  
  public static String formatNumberUsingLocale(int number) {
    NumberFormat formatter = NumberFormat.getInstance(new Locale("pt", "BR"));
    String formmatedNumber = formatter.format(number).replace(".", ",");
    return formmatedNumber;
  }
}
