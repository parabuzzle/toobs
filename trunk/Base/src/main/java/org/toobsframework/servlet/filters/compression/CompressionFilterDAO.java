package org.toobsframework.servlet.filters.compression;

import org.xml.sax.InputSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// jaxp 1.0.1 imports
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.net.URL;
import java.util.ArrayList;

/**
 * This class provides the data bindings for the screendefinitions.xml
 * and the requestmappings.xml file.
 * The data obtained is maintained by the ScreenFlowManager
 */

public class CompressionFilterDAO {

  public static final int DEFAULT_BUFFER_SIZE      = 512;

  // xml tag constants
  public static final String COMPRESSION_ENABLED   = "compression-enabled";
  public static final String COMPRESSION_TYPE      = "compression-type";
  public static final String BUFFER_SIZE           = "buffer-size";
  public static final String SMART_COMPRESS        = "smart-compression";

  public static final String HOST_ADDRESS_EXCLUDES = "host-address-excludes";
  public static final String HOST_ADDRESS          = "host-address";
  public static final String IP_ADDRESS            = "ip-address";
  public static final String IP_MASK               = "ip-mask";


  public static final String WHITESPACE_ENABLED  = "whitespace-filter-enabled";


  /**
   * The logger instance
   */
  private static Log log = LogFactory.getLog ( CompressionFilterDAO.class );

  private boolean compressionEnabled = false;

  private boolean whitespaceEnabled = false;

  private boolean smartCompress = false;

  private int bufferSize;

  private String compressionType = "gzip";

  private ArrayList excludedHosts = null;

  public CompressionFilterDAO (URL configURL) {
    Element root = loadDocument (configURL);

    try {
      compressionEnabled = Boolean.valueOf ( getTagValue(root, COMPRESSION_ENABLED ).trim() ).booleanValue();
    } catch (Exception e) { }

    try {
      whitespaceEnabled = Boolean.valueOf ( getTagValue(root, WHITESPACE_ENABLED ).trim() ).booleanValue();
    } catch (Exception e) { }

    try {
      smartCompress = Boolean.valueOf ( getTagValue(root, SMART_COMPRESS ).trim() ).booleanValue();
    } catch (Exception e) { }

    compressionType = getTagValue(root, COMPRESSION_TYPE ).trim();

    try {
      bufferSize = Integer.valueOf ( getTagValue(root, BUFFER_SIZE ).trim() ).intValue();
    } catch (Exception e) {
      bufferSize = DEFAULT_BUFFER_SIZE;
    }

    excludedHosts = getExcludedHosts(root);

  }

  public boolean isCompressionEnabled() {
    return compressionEnabled;
  }

  public boolean isWhitespaceEnabled() {
    return whitespaceEnabled;
  }

  public boolean isSmartCompress() {
    return smartCompress;
  }

  public String getCompressionType() {
    return compressionType;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public ArrayList getHostAddress() {
    return excludedHosts;
  }

  private  Element loadDocument(URL url) {
    Document doc = null;
    try {
      InputSource xmlInp = new InputSource(url.openStream());

      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = docBuilderFactory.newDocumentBuilder();
      doc = parser.parse(xmlInp);
      Element root = doc.getDocumentElement();
      root.normalize();
      return root;
    } catch (SAXParseException err) {
      log.fatal ("CompressionFilterDAO ** Parsing error" + ", line " +
                          err.getLineNumber () + ", uri " + err.getSystemId ());
      log.fatal("CompressionFilterDAO error: " + err.getMessage ());
    } catch (SAXException e) {
      log.fatal("CompressionFilterDAO error: " + e);
    } catch (java.net.MalformedURLException mfx) {
      log.fatal("CompressionFilterDAO error: " + mfx);
    } catch (java.io.IOException e) {
      log.fatal("CompressionFilterDAO error: " + e);
    } catch (Exception pce) {
      log.fatal("CompressionFilterDAO error: " + pce);
    }
    return null;
  }

  private ArrayList getExcludedHosts(Element root) {
    ArrayList excluded = new ArrayList();

    // get protected pages //
    NodeList outterList = root.getElementsByTagName(HOST_ADDRESS_EXCLUDES);
    for (int outterLoop = 0; outterLoop < outterList.getLength(); outterLoop++) {
      Element element = (Element)outterList.item(outterLoop);
      NodeList list = element.getElementsByTagName(HOST_ADDRESS);
      for (int loop = 0; loop < list.getLength(); loop++) {
        Node node = list.item(loop);
        if (node != null) {
          String hostAddress = "";
          String ipAddress = getSubTagValue(node, IP_ADDRESS);
          String ipMask = getSubTagValue(node, IP_MASK);
          String[] host = ipAddress.split(".");
          String[] mask = ipMask.split(".");
          boolean add = true;
          if (host.length == mask.length) {
            for (int i = 0; i < host.length; i++) {
              int part = 0;
              try {
                part = Integer.parseInt(mask[i]);
              }
              catch (NumberFormatException ex1) {
                add = false;
                break;
              }
              if (part != 0) {
                hostAddress = hostAddress + host[i] + ".";
              } else {
                break;
              }
            }
          }
          if (add && hostAddress.length() > 0) {
            excluded.add(hostAddress);
          }
        }
      }
    }
    return excluded;
  }

  private String getTagValue(Element root, String tagName) {
    String returnString = "";
    NodeList list = root.getElementsByTagName(tagName);
    for (int loop = 0; loop < list.getLength(); loop++) {
      Node node = list.item(loop);
      if (node != null) {
        Node child = node.getFirstChild();
        if ((child != null) && child.getNodeValue() != null) return child.getNodeValue();
      }
    }
    return returnString;
  }

  private String getSubTagValue(Node node, String subTagName) {
    String returnString = "";
    if (node != null) {
      NodeList  children = node.getChildNodes();
      for (int innerLoop =0; innerLoop < children.getLength(); innerLoop++) {
        Node  child = children.item(innerLoop);
        if ((child != null) && (child.getNodeName() != null) && child.getNodeName().equals(subTagName) ) {
          Node grandChild = child.getFirstChild();
          if (grandChild.getNodeValue() != null) return grandChild.getNodeValue();
        }
      } // end inner loop
    }
    return returnString;
  }
}


