package org.jfree.chart.imagemap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Generates URLs using the HTML href attribute for image map area tags.
 */
public class AjaxURLFragment 
    implements URLTagFragmentGenerator {

    /**
     * Generates a URL string to go in an HTML image map.
     *
     * @param urlText  the URL.
     * 
     * @return The formatted text
     */
    public String generateURLFragment(String urlText) {
      String decoded;
      String category = "";
      String baseChart = "";
      String chartMod = "";
      String chartParams = "";
      try {
        decoded = URLDecoder.decode(urlText, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        decoded = urlText;
      }
      int catIdx = decoded.indexOf("category=");
      if (catIdx != -1) {
        category = decoded.substring(catIdx+9);
      }
      String[] split = urlText.split("\\?");
      int spIdx = split[0].indexOf("SinglePlot");
      if (spIdx != -1) {
        chartMod = "SinglePlot";
      } else {
        spIdx = split[0].indexOf(".xchart");
      }
      baseChart = split[0].substring(0,spIdx);
      if (split.length > 1) {
        chartParams = split[1];
      }
      return " href=\"javascript:void(0)\" baseChart=\"" + baseChart + "\" chartMod=\"" + chartMod + "\" params=\"" + chartParams + "\" class=\"ajaxMapArea\" category=\"" + category + "\"";
    }

}
