package org.toobsframework.transformpipeline.xslExtentions;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.Configuration;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Date Formatting class used in xsl Extentions.
 * 
 * @author spudney
 */
public class DateHelper {
  /** DOCUMENT ME! */
  private static Log log = LogFactory.getLog(DateHelper.class);
  
  public static String getMonthStringFromNumber(String monthNumber) {
    int monthInt = -1;
    try{
      monthInt = Integer.parseInt(monthNumber);
    } catch (NumberFormatException e) {
      return "";
    }
    if(monthInt < 1 || monthInt > 12) {
      return "";
    }
    DateFormatSymbols dfs = new DateFormatSymbols();
    /*
    String[] month = {"", "January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"};
    */
    return dfs.getMonths()[monthInt - 1]; //month[monthInt];
  }
  public static String getDateNow() {
    return String.valueOf(new Date().getTime());
  }
  
  public static String getDateLastWeek() {
    Calendar cal = new GregorianCalendar();
    cal.roll(Calendar.DAY_OF_YEAR, - 7);
    return String.valueOf(cal.getTimeInMillis());
  }
  
  public static String getDateNextWeek() {
    Calendar cal = new GregorianCalendar();
    cal.roll(Calendar.DAY_OF_YEAR, + 7);
    return String.valueOf(cal.getTimeInMillis());
  }  
  
  public static String getDayNextWeek() {
    Calendar cal = new GregorianCalendar();
    cal.roll(Calendar.DAY_OF_YEAR, + 7);
    return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
  }
  
  public static String getDateNextMonth() {
    Calendar cal = new GregorianCalendar();
    cal.roll(Calendar.MONTH, + 1); 
    return String.valueOf(cal.getTimeInMillis());
  }  

  /**
   * Gets a string that represents the input date formatted into the proper
   * format, and converted into the proper timezone.
   * 
   * @return date-only string formatted with given time zone.
   * 
   * @exception XMLTransfromerException
   *              if parsing problem occurs
   */
  public static String getFormattedDate(String inputDate, String format)
      throws XMLTransformerException {
    String dateString = "";
    Date date = null;

    if ((null != inputDate) && (!inputDate.trim().equals(""))) {
      try {
        long inputDateAsLong = Long.parseLong(inputDate);
        date = new Date(inputDateAsLong);
      } catch (NumberFormatException ex) {
        throw new XMLTransformerException("Problem parseing date.");
      }

      DateFormat dateFormatter = createDateFormatter();

      if (format == null || format.length() == 0) {
        format = "dd MMM yyyy";
      }
      try {
        ((SimpleDateFormat) dateFormatter).applyPattern(format);
      } catch (ClassCastException cce) {
        log.warn("Exception while applying pattern value", cce);
      }

      dateString = dateFormatter.format(date);
    }

    return dateString;
  }

  /**
   * WARNING: untested...not used yet best i can tell. Gets a string that
   * represents the input datetime formatted into the proper fromate, and
   * converted into the proper timezone.
   * 
   * @return date-time string formatted with given time zone.
   * 
   * @exception XMLTransfromerException
   *              if parsing problem occurs
   */
  public static String getFormattedDateTime(String inputDate,
      String dateFormat, String timeFormat, String tzValue, String language)
      throws XMLTransformerException {
    String dateString = "";
    String timeString = "";
    Date date = null;

    if ((null != inputDate) && (!inputDate.trim().equals(""))) {
      try {
        try {
          long inputDateAsLong = Long.parseLong(inputDate);
          date = new Date(inputDateAsLong);
        } catch (NumberFormatException ex) {
          date = (new SimpleDateFormat(
              Configuration.getInstance().getDateFormat() + " "
                  + Configuration.getInstance().getTimeFormat()))
              .parse(inputDate);
        }

        String formattedDateString = DateHelper.getFormattedDate(inputDate,
            dateFormat);

        DateFormat timeFormatter = createTimeFormatter(language);

        if ((timeFormat != null) && !("".equalsIgnoreCase(timeFormat.trim()))) {
          try {
            ((SimpleDateFormat) timeFormatter).applyPattern(timeFormat);
          } catch (ClassCastException cce) {
            log.warn("Exception while applying pattern value", cce);
          }
        }

        timeString = timeFormatter.format(date);

        // Commenting this out for now...The date is stored in some other
        // timezone than GMT.
        // formatter.setTimeZone(getTimeZone(tzValue));
        dateString = formattedDateString + "  " + timeString;

      } catch (ParseException ex) {
        dateString = inputDate;
      }
    }

    return dateString;
  }

  /**
   * Utility to convert string timezone to TimeZone type.
   */
  private static TimeZone getTimeZone(String tzValue)
      throws XMLTransformerException {
    TimeZone tz = null;

    if (tzValue != null) {
      tz = TimeZone.getTimeZone((String) tzValue);
    } else {
      throw new XMLTransformerException("Null time zone");
    }

    return tz;
  }

  /*
   * Creates DateFromat instance based on the type dates and times to the
   * corresponding java.util.DateFormat constant.
   * 
   * @param languageCode - String @return java.util.DateFormat
   * 
   */
  private static DateFormat createDateFormatter() {
    DateFormat formatter = null;
    formatter = DateFormat.getDateInstance(DateFormat.SHORT);
    return formatter;
  }

  /*
   * Creates DateFromat instance based on the type dates and times to the
   * corresponding java.util.DateFormat constant. @param languageCode - String
   * @return java.util.DateFormat
   * 
   */
  private static DateFormat createTimeFormatter(String languageCode) {
    DateFormat formatter = null;

    if ((languageCode != null) && !("".equalsIgnoreCase(languageCode.trim()))) {
      languageCode = languageCode.trim();

      Locale locale = new Locale(languageCode.substring(2, 4).toLowerCase(),
          languageCode.substring(0, 2));
      formatter = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
    } else {
      formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
    }

    return formatter;
  }

}
