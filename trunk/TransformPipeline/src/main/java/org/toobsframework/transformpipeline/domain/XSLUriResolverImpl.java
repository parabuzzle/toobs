package org.toobsframework.transformpipeline.domain;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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

  private String[] base = new String[]{"xsl/"};
  
  public XSLUriResolverImpl() {
  }

  public XSLUriResolverImpl(String[] base) {
    this.base = base;
  }

  public Source resolve(String xslFile, String base) throws TransformerException {
    if (log.isDebugEnabled())
      log.debug("ENTER XSLUriResolverImpl.resolve('" + xslFile + "', '" + base + "');");

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStreamReader reader = null;
    StreamSource xslSource = null;
    
    URL configFileURL = null;
    String systemId = null;
    for (int i = 0; i < this.base.length; i++) {
      systemId = this.base[i] + xslFile;
      if (log.isDebugEnabled())
        log.debug("Checking for: " + systemId);
      configFileURL = classLoader.getResource(systemId);
      
      if (null != configFileURL) {
        break;
      }
    }
    // If the file exists, read it.
    if (null != configFileURL) {
      try {
        reader = new InputStreamReader(configFileURL.openStream());
        xslSource = new StreamSource(reader);
        xslSource.setSystemId(systemId);
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
        
    if (log.isDebugEnabled())
      log.debug("EXIT XSLUriResolverImpl.resolve('" + xslFile + "', '" + base + "');");
    
    return xslSource;
  }

}
