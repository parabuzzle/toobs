package org.toobs.framework.transformpipeline.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class XSLUriResolverImpl implements URIResolver {

  /**
   * To get the logger instance
   */
  private static Log log = LogFactory.getLog(XSLUriResolverImpl.class);

  public Source resolve(String href, String base) throws TransformerException {    
    log.debug("ENTER XSLUriResolverImpl.resolve('" + href + "', '" + base + "');");

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStreamReader reader = null;
    StreamSource xslSource = null;
    String xslFile = href;
    
    URL configFileURL = classLoader.getResource("xsl/" + xslFile);
    // If the file exists, read it.
    if (null != configFileURL) {
      try {
        reader = new InputStreamReader(configFileURL.openStream());
        xslSource = new StreamSource(reader);
      } catch (IOException e) {
        log.error("XSL File " + xslFile + " had IOException " + e.getMessage());
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException ignore) { }
        }
        throw new TransformerException("xsl " + xslFile + " cannot be loaded");
      }
    } else {
      log.error("XSL File " + xslFile + " does not exist for component ");
      throw new TransformerException("xsl " + xslFile + " does not exist");
    }
        
    log.debug("EXIT XSLUriResolverImpl.resolve('" + href + "', '" + base + "');");
    
    return xslSource;
  }

}
