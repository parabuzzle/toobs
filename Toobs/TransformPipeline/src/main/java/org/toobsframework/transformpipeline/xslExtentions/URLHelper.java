package org.toobsframework.transformpipeline.xslExtentions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * String Util for XSL Transforms
 */
public class URLHelper {
  private static Log log = LogFactory.getLog(URLHelper.class);

  private static Node findNodeByName(NodeList nodeList, String name) {
    Node ret = null;
    for(int i = 0; i < nodeList.getLength(); i++) {
      Node thisNode = (Node) nodeList.item(i);
      String nodeName = thisNode.getNodeName();
      String localName = thisNode.getLocalName();
      if(thisNode.getLocalName().equals(name)){
        ret = thisNode;
        break;
      }
    }
    return ret;
  }
  
  private  static String findNodeValueByName(NodeList nodeList, String name) {
    Node thisNode = findNodeByName(nodeList, name);
    String content = thisNode.getTextContent();
    if(content == null) {
      content = thisNode.getFirstChild().getNodeValue();      
    }
    return content;
  }

}
