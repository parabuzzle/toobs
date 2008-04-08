package org.toobs.framework.util.string;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

public class StringResource {
  /** logger component */
  private static Log log = LogFactory.getLog(StringResource.class);

  /**
   * Function extention for decoding an xml escaped string.  Replaces
   * &lt; &gt; &quot etc. with actual characters.
   *
   * @exception XMLTransformerException if a Transform Exception Occured.
   * @return String
   */
  public static String stripTags(String str, Boolean includeLinks ) {
    String decodedString = convertHTMLtoString(str);
    
    String strippedString = "";
    Parser parser = Parser.createParser(decodedString, null);
    StringBean sb = new StringBean ();
    sb.setLinks (includeLinks);
    try {
      parser.visitAllNodesWith (sb);
      strippedString = sb.getStrings ();
    } catch (ParserException e) {
      log.error("Could not parse html:" + decodedString);
    }
    return strippedString;
  }


  /**
   * Converts an HTML string to a readable string
   *
   * @param str is the string to convert
   *
   * @return is the converted string
   */
  public static String convertHTMLtoString(String str)
  {
      StringBuffer strMutable = new StringBuffer();

      int len = (str != null) ? str.length() : 0;
      int i=0;
      while (i<len)
      {
          char ch = str.charAt(i);

          if (ch == '\'')
          {
            strMutable.append('\'');
            i++;
          }
          else if (ch == '&')
          {
            if ((i+1)<len && str.charAt(i+1) == '#') {
              int poundIndex = str.indexOf("#", i);
              int endIndex = str.indexOf(";", poundIndex);
              if (endIndex != -1) {
              //get only numeric portion of html escaped values and get its char equivalent
              String strNumericPortion = str.substring(poundIndex + 1, endIndex);
              Integer intNumericPortion = new Integer(strNumericPortion);
              //char convertedChar = Character.forDigit(intNumericPortion.intValue(), 10);
              char convertedChar = (char) intNumericPortion.intValue();
              if (convertedChar == '\'') {
                strMutable.append('\'');
              }
              else
                strMutable.append(convertedChar);
              i = ++endIndex;
              } else {
                strMutable.append('&');
                strMutable.append('#');
                i++;i++;
              }
            }
            else {
              int endIndex = str.indexOf(";", i);
              if (endIndex != -1) {
                String code = str.substring(i+1,endIndex);
                if (code.equalsIgnoreCase("amp")) {
                  strMutable.append('&');
                }
                else if (code.equalsIgnoreCase("lt")) {
                  strMutable.append('<');
                }
                else if (code.equalsIgnoreCase("gt")) {
                  strMutable.append('>');
                }
                else if (code.equalsIgnoreCase("quot")) {
                  strMutable.append('"');
                }
                else if (code.equalsIgnoreCase("apos")) {
                  strMutable.append('\'');
                }
                else if (code.equalsIgnoreCase("nbsp")) {
                  strMutable.append(' ');
                }
                i = ++endIndex;
              } else {
                strMutable.append('&');
                i++;
              }
            }
          }
          else
          {
            strMutable.append(ch);
            i++;
          }
      }
      return strMutable.toString();
  }

  /**
   * Extends a string to HTML, but also takes care that the string can be placed as javascript
   * @param s is the string to be converted
   * @param strUseJS to be converted for javascript
   * @return the converted string
   */
  public static String extendedAsHTML(String s, String strUseJS) {
    StringBuffer str = new StringBuffer();
    int len = (s != null) ? s.length() : 0;

    // In the case where the useJS flag is one, we cannot turn special chars into #format
    // And we have to escape and double single quotes
    if (strUseJS.compareToIgnoreCase("TRUE") == 0) {
      for (int i = 0; i < len; i++) {
        char ch = s.charAt(i);

        if (ch == '\'') {
          str.append('\\');
          str.append('\'');
        } else if (ch == '"') {
          str.append('\\');
        }
        str.append(ch);
      }
    } else {
      for (int i = 0; i < len; i++) {
        char ch = s.charAt(i);

        if (ch == '\'') {
          str.append('\'');
        }

        //if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.BASIC_LATIN)) {
          str.append(ch);
        //} else {
        //  str.append("&#");
        //  str.append(Integer.toString((int) ch));
        //  str.append(';');
        //}
      }
    }

    return str.toString();
  }

  /**
   * search string for double quotes, and insert backslashes before them
   *
   * @param str input string
   *
   * @return processed result
   */
  public static String protectQuotes(String str) {
    StringBuffer result = new StringBuffer(str.length());
    int len = (str != null) ? str.length() : 0;

    for (int i = 0; i < len; ++i) {
      char ch = str.charAt(i);

      if (ch == '"') {
        result.append("\\\"");
      } else {
        result.append(ch);
      }
    }

    return result.toString();
  }

  /**
   * search string for non HTML attribute characters
   * @param s input string
   * @return processed result
   */
  public static String formatHTMLAttribute(String s)
  {
      StringBuffer str = new StringBuffer();

      int len = (s != null) ? s.length() : 0;
      for (int i = 0; i < len; i++ ) {
          char ch = s.charAt(i);
          switch ( ch ) {
              case '<': {
                 str.append("&lt;");
                 break;
              }
              case '>': {
                 str.append("&gt;");
                 break;
              }
              case '&': {
                 str.append("&amp;");
                 break;
              }
              case '"': {
                 str.append("&quot;");
                 break;
              }
              default: {
               // if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.BASIC_LATIN)) {
                  str.append(ch);
               // }
               // else {
               //   str.append("&#");
               //   str.append(Integer.toString((int)ch));
               //   str.append(';');
               // }
              }
          }
      }
      return str.toString();
  }
  /**
   * Converts an HTML string to a readable string
   *
   * @param str is the string to convert
   *
   * @return is the converted string
   */
  public static String formatHTMLAttributetoString(String str)
  {
    StringBuffer strMutable = new StringBuffer();

    int len = (str != null) ? str.length() : 0;
    int i=0;
    while (i<len)
    {
        char ch = str.charAt(i);

        if (ch == '\'')
        {
          strMutable.append('\'');
          i++;
        }
        else if (ch == '&')
        {
          if ((i+1)<len && str.charAt(i+1) == '#') {
            int poundIndex = str.indexOf("#", i);
            int endIndex = str.indexOf(";", poundIndex);

            //get only numeric portion of html escaped values and get its char equivalent
            String strNumericPortion = str.substring(poundIndex + 1, endIndex);
            Integer intNumericPortion = new Integer(strNumericPortion);
            //char convertedChar = Character.forDigit(intNumericPortion.intValue(), 10);
            char convertedChar = (char) intNumericPortion.intValue();
            if (convertedChar == '\'') {
              strMutable.append('\'');
            }
            else
              strMutable.append(convertedChar);
            i = ++endIndex;
          }
          else {
            int endIndex = str.indexOf(";", i);
            if ( endIndex != -1 ) {
              String code = str.substring(i+1,endIndex);
              if (code.equalsIgnoreCase("amp")) {
                strMutable.append('&');
                i = ++endIndex;
              }
              else if (code.equalsIgnoreCase("lt")) {
                strMutable.append('<');
                i = ++endIndex;
              }
              else if (code.equalsIgnoreCase("gt")) {
                strMutable.append('>');
                i = ++endIndex;
              }
              else if (code.equalsIgnoreCase("quot")) {
                strMutable.append('"');
                i = ++endIndex;
              }
              else if (code.equalsIgnoreCase("apos")) {
                strMutable.append('\'');
                i = ++endIndex;
              }
              else {
                strMutable.append(ch);
                i++;
              }
            } else {
              strMutable.append(ch);
              i++;
            }
          }
        }
        else
        {
          strMutable.append(ch);
          i++;
        }
    }
    return strMutable.toString();
  }

  /**
   * Utility method to format a string
   *
   * @param pattern String
   * @param arguments String[]
   * @return String
   */
  public static String formatString(String pattern, String[] arguments) {
    if (pattern == null || arguments == null) {
      return null;
    }
    return MessageFormat.format(pattern, (Object[])arguments);
  }

/*
public static void main(String[] args) {
    String testString = "Quote here -&gt;&quot;&lt;-";
    String output = convertHTMLtoString(testString);
    System.out.println("Output: " + output);
}
*/
}

