package org.toobsframework.pres.betwixt.strategy;

import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.strategy.BaseMixedContentEncodingStrategy;

public class MixedContentEncodingStrategy extends BaseMixedContentEncodingStrategy {

  /**
   * Encodes the given body content by either escaping the character data
   * or by encoding within a <code>CDATA</code> section.
   * The algorithm used to decide whether a particular element's mixed 
   * should be escaped is delegated to the concrete subclass through
   * {@link #encodeAsCDATA}
   * @see org.apache.commons.betwixt.strategy.MixedContentEncodingStrategy#encode(java.lang.String, org.apache.commons.betwixt.ElementDescriptor)
   */
  public String encode(String bodyContent, ElementDescriptor element) {
      if (element.getLocalName().equals("importData")) {
        return (bodyContent != null && bodyContent.length() > 0) ? bodyContent.substring(bodyContent.indexOf("?>")+2) : bodyContent;
      }
      if (encodeAsCDATA(element)) {
          return encodeInCDATA(bodyContent);
      }
      
      return escapeCharacters(bodyContent);
  }

  /**
   * Encode by escaping character data unless
   * the <code>ElementDescriptor</code> contains
   * an option with name 
   * <code>org.apache.commons.betwixt.mixed-content-encoding</code>
   * and value <code>CDATA</code>.
   */
  protected boolean encodeAsCDATA(ElementDescriptor element) {
      boolean result = false;
      if (element != null ) {
          String optionValue = element.getOptions().getValue(ENCODING_OPTION_NAME);
          result = CDATA_ENCODING.equals(optionValue);        
      }
      return result;       
  }

}
