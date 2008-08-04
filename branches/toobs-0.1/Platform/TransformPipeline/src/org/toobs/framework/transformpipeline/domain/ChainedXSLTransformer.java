package org.toobs.framework.transformpipeline.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

@SuppressWarnings("unchecked")
public class ChainedXSLTransformer implements IXMLTransformer {

  /**
   * To get the logger instance
   */
  private static Log log = LogFactory.getLog(ChainedXSLTransletTransformer.class);

  private Properties outputProperties = null;

  public Vector transform(
      Vector inputXSLs,
      Vector inputXMLs,
      HashMap inputParams) throws XMLTransformerException {

    Vector resultingXMLs = new Vector();
    for (int i = 0; i < inputXMLs.size(); i++) {
      resultingXMLs.add(transform(inputXSLs, (String)inputXMLs.get(i), inputParams));
    }
    return resultingXMLs;
  }

  private String transform(
      Vector inputXSLs,
      String inputXML,
      HashMap inputParams) throws XMLTransformerException {

    String outputXML = null;
    ByteArrayInputStream  xmlInputStream = null;
    ByteArrayOutputStream xmlOutputStream = null;
    try {
      TransformerFactory tFactory = TransformerFactory.newInstance();

      if (tFactory.getFeature(SAXSource.FEATURE) &&
          tFactory.getFeature(SAXResult.FEATURE)) {
        // Cast the TransformerFactory to SAXTransformerFactory.
        SAXTransformerFactory saxTFactory = ( (SAXTransformerFactory) tFactory);

        // Create a TransformerHandler for each stylesheet.
        Vector tHandlers = new Vector();
        TransformerHandler tHandler = null;

        // Create an XMLReader.
        XMLReader reader = new SAXParser();

        // transformer3 outputs SAX events to the serializer.
        if (outputProperties == null) {
          outputProperties = OutputPropertiesFactory.getDefaultMethodProperties("html");
        }
        Serializer serializer = SerializerFactory.getSerializer(outputProperties);
        for (int it = 0; it < inputXSLs.size(); it++) {
          Object source = inputXSLs.get(it);
          if (source instanceof StreamSource) {
            tHandler = saxTFactory.newTransformerHandler((StreamSource)source);
          } else {
            tHandler = saxTFactory.newTransformerHandler(new StreamSource(getXSLFile((String) source)));
          }
          Transformer transformer = tHandler.getTransformer();
          transformer.setOutputProperty("encoding", "UTF-8");
          if(inputParams != null) {
            Iterator paramIt = inputParams.entrySet().iterator();
            while (paramIt.hasNext()) {
              Map.Entry thisParam = (Map.Entry) paramIt.next();
              transformer.setParameter( (String) thisParam.getKey(),
                                       (String) thisParam.getValue());
            }
          }
          tHandlers.add(tHandler);
        }
        tHandler = null;
        for (int th = 0; th < tHandlers.size(); th++) {
          tHandler = (TransformerHandler)tHandlers.get(th);
          if (th==0) {
            reader.setContentHandler(tHandler);
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", tHandler);
          } else {
            ((TransformerHandler)tHandlers.get(th-1)).setResult(new SAXResult(tHandler));
          }
        }
        // Parse the XML input document. The input ContentHandler and output ContentHandler
        // work in separate threads to optimize performance.
        InputSource xmlSource = null;
        xmlInputStream = new ByteArrayInputStream((inputXML).getBytes("UTF-8"));
        if (log.isDebugEnabled()) {
          log.debug("Input XML:\n" + inputXML);
        }
        xmlSource = new InputSource(xmlInputStream);
        xmlOutputStream = new ByteArrayOutputStream();
        serializer.setOutputStream(xmlOutputStream);
        ((TransformerHandler)tHandlers.get(tHandlers.size()-1)).setResult(new SAXResult(serializer.asContentHandler()));
        reader.parse(xmlSource);
        outputXML = xmlOutputStream.toString("UTF-8");
        if (log.isDebugEnabled()) {
          log.debug("Output XML:\n" + outputXML);
        }
      }
    }
    catch (IOException ex) {
      log.error("", ex);
      throw new XMLTransformerException(ex);
    }
    catch (IllegalArgumentException ex) {
      log.error("", ex);
      throw new XMLTransformerException(ex);
    }
    catch (SAXException ex) {
      log.error("", ex);
      throw new XMLTransformerException(ex);
    }
    catch (TransformerConfigurationException ex) {
      log.error("", ex);
      throw new XMLTransformerException(ex);
    }
    catch (TransformerFactoryConfigurationError ex) {
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
    return outputXML;
  }

  public void setOutputProperties(Properties outputProperties) {
    this.outputProperties = outputProperties;
  }

  private static InputStream getXSLFile(String xslFileName)
      throws FileNotFoundException, IOException {
    ClassLoader cld = ChainedXSLTransformer.class.getClassLoader();
    ByteArrayInputStream baos = null;
    URL configFileURL = cld.getResource("xsl/" + xslFileName + ".xsl");
    // If the file exists, read it.
    if (null != configFileURL) {
      File file = new File(configFileURL.getFile());
      byte[] ba = new byte[(int)file.length()];
      DataInputStream in = new DataInputStream(new FileInputStream(file));
      in.readFully(ba);
      in.close();
      baos = new ByteArrayInputStream(ba);
    } else {
      log.error("XSL File " + "xsl/" + xslFileName + ".xsl does not exist for component ");
      throw new FileNotFoundException("xsl " + xslFileName + " does not exist");
    }
    return baos;
  }
}
