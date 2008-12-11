package org.toobsframework.transformpipeline.domain;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.xml.utils.DefaultErrorHandler;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Vector;
import java.util.Iterator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class StaticXSLTransformer extends BaseXMLTransformer {

  /**
   * To get the logger instance
   */
  private static Log log = LogFactory.getLog(StaticXSLTransformer.class);
  
  /**
   * Implementation of the transform() method. This method first checks some
   * input parameters. Then it creates a Source object and invoces the
   * {@link #makeTransformation makeTransformation()}method.
   *
   */
  @SuppressWarnings("unchecked")
  public Vector transform(
      Vector inputXSLs,
      Vector inputXMLs,
      HashMap inputParams) throws XMLTransformerException {

    if (log.isDebugEnabled()) {
      log.debug("TRANSFORM XML STARTED");
      log.debug("Get input XMLs");
      Iterator iter = inputParams.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry)iter.next();
        log.debug("  Transform Param - name: " + entry.getKey() + " value: " + entry.getValue());
      }        
    }

    Iterator XSLIterator = inputXSLs.iterator();
    
    InputStreamReader reader = null;
    while (XSLIterator.hasNext()) {

      Iterator XMLIterator = inputXMLs.iterator();

      String xslFile = (String) XSLIterator.next();
      Source xslSource = null;
      
      try {
        xslSource = uriResolver.resolve(xslFile + ".xsl", "");
        if (log.isDebugEnabled()) {
          log.debug("XSL Source: " + xslSource.getSystemId());
        }
      } catch (TransformerException e) {
        throw new XMLTransformerException("xsl " + xslFile + " cannot be loaded");
      }

      if (xslSource == null) {
        throw new XMLTransformerException("StreamSource is null");
      }

      Vector resultingXMLs = new Vector();

      //String xmlString = "";
      ByteArrayInputStream  xmlInputStream = null;
      ByteArrayOutputStream xmlOutputStream = null;
      ByteArrayOutputStream outXML = null;
      while (XMLIterator.hasNext()) {
        try {
          Object xmlObject = XMLIterator.next();
          if (xmlObject instanceof org.w3c.dom.Node) {
            TransformerFactory tf=TransformerFactory.newInstance();
            //identity
            Transformer t=tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            t.transform(new DOMSource( (org.w3c.dom.Node)xmlObject ), new StreamResult( os ));
            xmlInputStream = new ByteArrayInputStream(os.toByteArray());
            //xmlString = os.toString("UTF-8");
            if (log.isDebugEnabled()) {
              log.debug("Input XML for " + xslSource.toString() + "( " + xslFile + ") : " + os.toString("UTF-8"));
            }
          } else {
            //xmlString = (String) xmlObject;
            xmlInputStream = new ByteArrayInputStream(((String) xmlObject).getBytes("UTF-8"));
            if (log.isDebugEnabled()) {
              log.debug("Input XML for " + xslSource.toString() + "( " + xslFile + ") : " + xmlObject);
            }
          }
          
          //StringReader xmlReader = new StringReader(xmlString);
          xmlOutputStream = new ByteArrayOutputStream();
          //StringWriter xmlWriter = new StringWriter();
  
          StreamSource xmlSource = new StreamSource(xmlInputStream);
          StreamResult xmlResult = new StreamResult(xmlOutputStream);
  
          doTransform(
              xslSource,
              xmlSource,
              inputParams,
              xmlResult,
              xslFile);
  
          outXML = (ByteArrayOutputStream) xmlResult.getOutputStream();
          //log.debug("Output XML: " + outXML.toString("UTF-8"));
          resultingXMLs.add(outXML.toString("UTF-8"));
        } catch (UnsupportedEncodingException uee) {
          log.error("Error creating output string", uee);
          throw new XMLTransformerException(uee);
        } catch (TransformerException te) {
          log.error("Error creating input xml: " + te.getMessage(), te);
          throw new XMLTransformerException(te);
        } finally {
          try {
            if (reader != null) {
              try {
                reader.close();
                reader = null;
              } catch (IOException ignore) { }
            }
            if (xmlInputStream != null) {
              xmlInputStream.close();
              xmlInputStream = null;
            }
            if (xmlOutputStream != null) {
              xmlOutputStream.close();
              xmlOutputStream = null;
            }
            if (outXML != null) {
              outXML.close();
              outXML = null;
            }
          } catch (IOException ex) {
          }
        }

      }

      inputXMLs = resultingXMLs;

    }

    return inputXMLs;
  }

  /**
   * This method actually does all the XML Document transformation.
   * <p>
   * @param xslSource
   *          holds the xslFile
   * @param xmlSource
   *          holds the xmlFile
   * @param params
   *          holds the params needed to do this transform
   * @param xmlResult
   *          holds the streamResult of the transform.
   */
  @SuppressWarnings("unchecked")
  protected void doTransform(
      Source xslSource,
      Source xmlSource,
      HashMap params,
      StreamResult xmlResult,
      String xslFile) throws XMLTransformerException {

    try {
      // 1. Instantiate a TransformerFactory.
      TransformerFactory tFactory = TransformerFactory.newInstance();
      // set the URI Resolver for the transformer factory
      setFactoryResolver(tFactory);
            
      // 2. Use the TransformerFactory to process the stylesheet Source and
      //    generate a Transformer.
      Transformer transformer = tFactory.newTransformer(xslSource);
      
      transformer.setErrorListener(new DefaultErrorHandler(true));
      
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
      Date timer = new Date();
      transformer.transform(xmlSource, xmlResult);
      Date timer2 = new Date();
      if (log.isDebugEnabled()) {
        long diff = timer2.getTime() - timer.getTime();
        log.debug("Time to transform: " + diff + " mS XSL: " + xslFile);
      }
    } catch(TransformerConfigurationException tce) {
      log.error(tce.toString(), tce);
      throw new XMLTransformerException(tce.toString());
    } catch(TransformerException te) {
      throw new XMLTransformerException(te);
    }

  }

  public void setOutputProperties(Properties outputProperties) {
  }

}
