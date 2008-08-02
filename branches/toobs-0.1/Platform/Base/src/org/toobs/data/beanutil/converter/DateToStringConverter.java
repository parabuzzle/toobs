package org.toobs.data.beanutil.converter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils.Converter;

public class DateToStringConverter implements Converter {

  public Object convert(Class arg0, Object value) {
    if (value == null) {
      return ((String) null);
    } else if(value instanceof java.util.Date) {
      Date date = (Date) value;
      return String.valueOf(date.getTime());
    } else if(value instanceof BigDecimal) {
      NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
      BigDecimal bigD = (BigDecimal) value;
      return n.format(bigD.doubleValue());
    }  else {
      return (value.toString());
    }
  }

}