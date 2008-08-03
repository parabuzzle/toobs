package org.toobsframework.transformpipeline.xslExtentions;

//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;


import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.Locale;

import org.toobsframework.transformpipeline.domain.XMLTransformerException;


/**
 * Price Formatting class used in xsl Extentions.
 *
 * @author spudney
 */
public class PriceFormatHelper {
  /** DOCUMENT ME! */
  //private static Log log = LogFactory.getLog(PriceFormatHelper.class);

  /**
   * Gets a string that represents the input Price formatted into the proper fromate, and converted
   * into the proper timezone.
   *
   * @return Price-only string formatted with given time zone.
   *
   * @exception XMLTransfromerException if parsing problem occurs
   */
  public static String getFormattedPrice(String inputPrice, String priceFormat, String language)
    throws XMLTransformerException {
    if ((inputPrice == null) || (inputPrice.trim().length() == 0)) {
      inputPrice = "0";
    }

    Locale locale = new Locale(language.substring(2, 4).toLowerCase(), language.substring(0, 2));
    DecimalFormat priceFormatter = (DecimalFormat) NumberFormat.getNumberInstance(locale);
    priceFormatter.setGroupingUsed(true);
    priceFormatter.setMaximumFractionDigits(2);
    priceFormatter.setMinimumFractionDigits(2);

    priceFormatter.applyPattern(priceFormat);

    return priceFormatter.format(new Double(inputPrice));
  }
}
