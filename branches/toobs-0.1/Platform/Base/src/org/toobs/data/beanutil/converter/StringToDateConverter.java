package org.toobs.data.beanutil.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringToDateConverter {

  private static Log log = LogFactory.getLog(StringToDateConverter.class);
  
  public static Object convert(Object value) {
    if (value == null) return (Date)null;
    Date parsed = null;
    String strDate;
    if (value.getClass().isArray()) {
      strDate = ((String[])value)[0];
    } else {
      strDate = (String)value;
    }
    DateFormat df;
    if (strDate.length() > 10) {
      df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    } else {
      df = new SimpleDateFormat("MM/dd/yyyy");
    }
    try {
      parsed = df.parse(strDate);
    } catch (ParseException e) {
      log.error("ParseError for " + strDate);
    }
    return parsed;
  }

}