package org.toobsframework.transformpipeline.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class XslURIResolver_Main implements URIResolver {

  public Source resolve(String href, String base) throws TransformerException {
    StreamSource xslSource = null;
    PrintWriter pw = null;
    try {
      File logFile = new File(System.getProperty("user.home") + "/uri-resolver.log");
      pw = new PrintWriter(logFile);
      
      pw.println("[" + new Date().toString() + "] ENTER XSLUriResolverImpl.resolve('" + href + "', '" + base + "');");
  
      InputStreamReader reader = null;
      String xslFile = href;
      
      String libFile = base.substring(0, base.indexOf("src/xsl") + 8) + href;
      libFile = libFile.replace("file:", "");
      pw.println("[" + new Date().toString() + "] File path " + libFile);
      File configFile = new File(libFile);
      URL configFileURL = configFile.toURL();
      // If the file exists, read it.
      if (null != configFileURL) {
        try {
          reader = new InputStreamReader(configFileURL.openStream());
          xslSource = new StreamSource(reader);
        } catch (IOException e) {
          pw.println("[" + new Date().toString() + "] XSL File " + xslFile + " had IOException " + e.getMessage());
          if (reader != null) {
            try {
              reader.close();
            } catch (IOException ignore) { }
          }
          throw new TransformerException("xsl " + xslFile + " cannot be loaded");
        }
      } else {
        pw.println("[" + new Date().toString() + "] XSL File " + xslFile + " does not exist for component ");
        throw new TransformerException("xsl " + xslFile + " does not exist");
      }
          
      System.out.println("EXIT XSLUriResolverImpl.resolve('" + href + "', '" + base + "');");
    } catch (Exception e) {
      throw new TransformerException(e.getMessage());
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
    return xslSource;
  }

}
