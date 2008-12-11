package org.toobs.framework.transformpipeline.domain;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.util.Properties;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Map;

public class TransletTransformer implements IXMLTransformer {

  private static Log log = LogFactory.getLog(TransletTransformer.class);

  public TransletTransformer() {
  }

  @SuppressWarnings("unchecked")
  public Vector transform(
      Vector inputXSLTranslets,
      Vector inputXMLs,
      HashMap inputParams) throws XMLTransformerException {

    if (log.isDebugEnabled()) {
      log.debug("TRANSFORM XML STARTED");
      log.debug("Get input XMLs");
    }
    String xslClass = null;
    Vector resultingXMLs = null;
    ByteArrayInputStream  xmlInputStream = null;
    ByteArrayOutputStream xmlOutputStream = null;
    StreamSource xmlSource = null;
    StreamResult xmlResult = null;
    for (int xt = 0; xt<inputXSLTranslets.size(); xt++) {

      xslClass = (String) inputXSLTranslets.get(xt);

      resultingXMLs = new Vector();

      xmlInputStream = null;
      xmlOutputStream = null;
      xmlSource = null;
      xmlResult = null;
      for (int x = 0; x<inputXMLs.size(); x++) {
        if (log.isDebugEnabled()) {
          log.debug("Input XML for " + xslClass + " : " + (String)inputXMLs.get(x));
        }
        try {
          xmlInputStream = new ByteArrayInputStream(((String)inputXMLs.get(x)).getBytes("UTF-8"));
          xmlOutputStream = new ByteArrayOutputStream();

          xmlSource = new StreamSource(xmlInputStream);
          xmlResult = new StreamResult(xmlOutputStream);

          doTransform(
              xslClass,
              xmlSource,
              inputParams,
              xmlResult);

          resultingXMLs.add(((ByteArrayOutputStream) xmlResult.getOutputStream()).toString("UTF-8"));
        } catch (UnsupportedEncodingException uee) {
          log.error("Error creating output string", uee);
          throw new XMLTransformerException(uee);
        } finally {
          try {
            if (xmlInputStream != null) {
              xmlInputStream.close();
              xmlInputStream = null;
            }
            if (xmlOutputStream != null) {
              xmlOutputStream.close();
              xmlOutputStream = null;
            }
          } catch (IOException ex) {
          }
        }
      }
      inputXMLs = resultingXMLs;
    }

    return inputXMLs;
  }

  protected void doTransform(
      String xslTranslet,
      StreamSource xmlSource,
      HashMap params,
      StreamResult xmlResult) throws XMLTransformerException {
    try {
      // Dont rely on the system property to get the right transformer
      TransformerFactory tFactory = new org.apache.xalan.xsltc.trax.TransformerFactoryImpl();
      // set the URI Resolver for the transformer factory      
      tFactory.setURIResolver(new XSLUriResolverImpl());      
      
      String tPkg = "xsl." + xslTranslet.substring(0, xslTranslet.lastIndexOf("/")).replaceAll("/", ".").replaceAll("-", "_"); 
      try {
        //tFactory.setAttribute("use-classpath", Boolean.TRUE);
        tFactory.setAttribute("auto-translet", Boolean.TRUE);
        tFactory.setAttribute("package-name", tPkg);
      } catch (IllegalArgumentException iae) {
        log.error("Error setting XSLTC specific attribute", iae);
        throw new XMLTransformerException(iae);
      }
      String xslFile = Thread.currentThread().getContextClassLoader().getResource("xsl/" + xslTranslet + ".xsl").getFile();
      Transformer transformer = tFactory.newTransformer(new StreamSource(xslFile));

      // 2.2 Set character encoding for all transforms to UTF-8.
      transformer.setOutputProperty("encoding", "UTF-8");

      // 2.5 Set Parameters necessary for transformation.
      if(params != null) {
        Iterator paramIt = params.entrySet().iterator();
        while (paramIt.hasNext()) {
          Map.Entry thisParam = (Map.Entry) paramIt.next();
          transformer.setParameter( (String) thisParam.getKey(),
                                   (String) thisParam.getValue());
        }
      }

      // 3. Use the Transformer to transform an XML Source and send the
      //    output to a Result object.
      transformer.transform(xmlSource, xmlResult);
    } catch(TransformerConfigurationException tce) {
      log.error(tce.toString(), tce);
      throw new XMLTransformerException(tce);
    } catch(TransformerException te) {
      log.error(te.toString(), te);
      throw new XMLTransformerException(te);
    }

  }

  public void setOutputProperties(Properties outputProperties) {
  }
}
