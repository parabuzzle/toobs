package org.toobsframework.transformpipeline.xslExtentions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.string.StringResource;



/**
 * String Util for XSL Transforms
 */
public class XMLEncodeHelper {
  private static Log log = LogFactory.getLog(XMLEncodeHelper.class);

  public static String encodeURL(String url) throws XMLTransformerException {
    try {
      return URLEncoder.encode(url, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return url;
    }
  }

  /**
   * Function extention for decoding an xml escaped string.  Replaces
   * &lt; &gt; &quot etc. with actual characters.
   *
   * @exception XMLTransformerException if a Transform Exception Occured.
   * @return String
   */
  public static String stripTags(String str, Boolean includeLinks ) throws
      XMLTransformerException {
    String strippedString = StringResource.stripTags(str, includeLinks);
    return strippedString;
  }

  /**
   * Function extention for decoding an xml escaped string.  Replaces
   * &lt; &gt; &quot etc. with actual characters.
   *
   * @exception XMLTransformerException if a Transform Exception Occured.
   * @return String
   */
  public static String decodeString(String str ) throws
      XMLTransformerException {
    String decodedString = "";
    decodedString = StringResource.convertHTMLtoString(str);
    return decodedString;
  }

  /**
   * Escapes the string for use in Javascript Replaces ' with \'
   * and " with \"
   *
   * @exception XMLTransformerException if a Transform Exception Occured.
   * @return String
   */
  public static String escapeJSString(String str) throws
      XMLTransformerException {

    StringBuffer escapedstr = new StringBuffer();
    int len = (str != null) ? str.length() : 0;

      for (int i = 0; i < len; i++) {
        char ch = str.charAt(i);

        if (ch == '\'' || ch == '"')
          escapedstr.append('\\');

        escapedstr.append(ch);
      }

    return escapedstr.toString();
  }

  /**
   * XML encodes the string replaces < > " etc. with &lt; &gt; &quot; etc.
   *
   * @exception XMLTransformerException if a Transform Exception Occured.
   * @return String
   */
  public static String encodeString(String str ) throws
      XMLTransformerException {
    String decodedString = "";
    decodedString = StringResource.formatHTMLAttribute(str);
    return decodedString;
  }

  public static String toHtml(String str ) throws
      XMLTransformerException {
    if (str == null) return null;
    if (str.indexOf("<br") == -1) {
      str = str.replaceAll("\n", "<br/>");
    }
    return str.replaceAll("&nbsp;", "&#xa0;");
  }
  
  public static String truncate(String str, int chars) {
    if (str==null) return str;
    if (str.length() > chars) {
      return str.substring(0, chars-3) + "...";
    }
    return str;
  }
  
  public static String splitAppend(String input, String delim, String append) {
    if (input == null || delim == null) return input;
    String output = "";
    String[] split = input.split(delim);
    for (int i = 0; i < split.length; i++) {
      if (i>0) output = output + ";";
      output = output + split[i] + append;
    }
    return output;
  }
  
  public static String breakUp(String str, int chars, String ua) {
    if (str==null) return str;
    if (ua.indexOf("MSIE") != -1) {
      return breakUpMSIE(str, chars).toString();
    } else if (ua.indexOf("Firefox") != -1) {
      return breakUpFFox(str, chars).toString();
    /*} else if (ua.indexOf("Safari") != -1) {
      return breakUpSafari(str, chars).toString();*/      
    } else {
      return breakUpGeneric(str, chars).toString();      
    }
  }
  
  private static StringBuffer breakUpMSIE(String str, int chars) {
    StringBuffer sb = new StringBuffer();
    int z = 0;
    for (int i=0; i<str.length(); i++) {
      switch (str.charAt(i)) {
      case ' ':
        z = 0;
        sb.append(str.charAt(i));
        break;
      case '&':
        sb.append("&");
        if (str.length() > i+4 && !str.substring(i+1, i+4).equals("amp;")) {
          sb.append("amp;");
        }
        z++;
        break;
      default:
        sb.append(str.charAt(i));
        z++;
      }
      if (z > 0 && z % chars == 0) {
        sb.append(" ");
      }
    }
    return sb;
  }

  private static StringBuffer breakUpFFox(String str, int chars) {
    StringBuffer sb = new StringBuffer();
    int z = 0;
    for (int i=0; i<str.length(); i++) {
      switch (str.charAt(i)) {
      case ' ':
        z = 0;
        sb.append(str.charAt(i));
        break;
      /*case '-':
        sb.append(str.charAt(i));
        int q = str.indexOf(" ", i);
        int p = str.indexOf("-", i);
        if (q == -1 && p == -1) {
          sb.append("&#8203;");
          z = 0;
        } else if (q > (chars - z) && p > (chars - z)) {
          sb.append("&#8203;");
          z = 0;
        } else {
          z++;
        }
        break;*/
      case '&':
        sb.append("&");
        if (str.length() > i+4 && !str.substring(i+1, i+4).equals("amp;")) {
          sb.append("amp;");
        }
        z++;
        break;
      default:
        sb.append(str.charAt(i));
        z++;
      }
      if (z > 0 && z % chars == 0) {
        sb.append(" ");
      }
    }
    return sb;
  }

  private static StringBuffer breakUpGeneric(String str, int chars) {
    StringBuffer sb = new StringBuffer();
    int z = 0;
    for (int i=0; i<str.length(); i++) {
      switch (str.charAt(i)) {
      case ' ':
        z = 0;
        sb.append(str.charAt(i));
        break;
      /*case '-':
        sb.append(str.charAt(i));
        int q = str.indexOf(" ", i);
        int p = str.indexOf("-", i);
        if (q == -1 && p == -1) {
          sb.append("&#8203;");
          z = 0;
        } else if (q > (chars - z) && p > (chars - z)) {
          sb.append("&#8203;");
          z = 0;
        } else {
          z++;
        }
        break;*/
      case '&':
        sb.append("&");
        if (str.length() > i+4 && !str.substring(i+1, i+4).equals("amp;")) {
          sb.append("amp;");
        }
        z++;
        break;
      default:
        sb.append(str.charAt(i));
        z++;
      }
      if (z > 0 && z % chars == 0) {
        sb.append(" ");
      }
    }
    return sb;
  }
}

