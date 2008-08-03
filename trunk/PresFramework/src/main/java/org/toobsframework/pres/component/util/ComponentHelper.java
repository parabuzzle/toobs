package org.toobsframework.pres.component.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.pres.component.manager.IComponentManager;

import org.toobsframework.data.beanutil.BeanMonkey;
import org.toobsframework.pres.chart.ChartBuilder;
import org.toobsframework.pres.chart.ChartDefinition;
import org.toobsframework.pres.chart.manager.IChartManager;
import org.toobsframework.pres.component.Component;
import org.toobsframework.pres.component.Transform;
import org.toobsframework.pres.layout.manager.IComponentLayoutManager;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.servlet.ContextHelper;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.IRequest;
import org.toobsframework.util.constants.PlatformConstants;


@SuppressWarnings("unchecked")
public class ComponentHelper {

  /** To get the logger instance */
  private static Log log = LogFactory.getLog(ComponentHelper.class);
  
  private static BeanFactory beanFactory;

  private static ComponentRequestManager reqManager;
  private static IComponentManager compManager;
  private static IComponentLayoutManager layoutManager;
  private static IChartManager chartManager;
  private static ChartBuilder chartBuilder;
  private static boolean debugComponents;
  private static String layoutExtension;
  private static String componentExtension;
  private static String chartExtension;
  
  static {
    beanFactory = ContextHelper.getInstance().getWebApplicationContext().getParentBeanFactory();
    reqManager = (ComponentRequestManager)beanFactory.getBean("componentRequestManager");
    compManager = (IComponentManager)beanFactory.getBean("IComponentManager");
    layoutManager = (IComponentLayoutManager)beanFactory.getBean("IComponentLayoutManager");
    chartManager = (IChartManager)beanFactory.getBean("IChartManager");
    chartBuilder = (ChartBuilder)beanFactory.getBean("chartBuilder");
    debugComponents = Configuration.getInstance().getDebugComponents();
    layoutExtension = Configuration.getInstance().getLayoutExtension();
    componentExtension = Configuration.getInstance().getComponentExtension();
    chartExtension = Configuration.getInstance().getChartExtension();
  }
  
  /**
   * Function extention for decoding an xml escaped string.  Replaces
   * &lt; &gt; &quot etc. with actual characters.
   *
   * @exception XMLTransformerException if a Transform Exception Occured.
   * @return String
   */
  public static String getSummary(String str, String length) throws
      XMLTransformerException {
    String strippedString = BeanMonkey.getSummary(str, Integer.parseInt(length));
    return strippedString;
  }

  private static long getDeployTime(IRequest request) {
    long deployTime = 0L;
    if (request.getParams().containsKey(PresConstants.DEPLOY_TIME)) {
      deployTime = Long.parseLong((String)request.getParams().get(PresConstants.DEPLOY_TIME));
    } else {
      deployTime = Configuration.getInstance().getDeployTime();
    }
    return deployTime;
  }

  public static String componentRef(String componentId, String contentType, Object parameters) throws
  XMLTransformerException {
    return componentRef(componentId, contentType, "direct", parameters);
  }

  public static String componentRef(String componentId, String contentType, String loader, Object parameters) throws
      XMLTransformerException {

    Random randomGenerator = new Random();
    try {
      StringBuffer sb = new StringBuffer(); 
      IRequest request = reqManager.get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }
      if (contentType == null || contentType.length() == 0) {
    	contentType = "xhtml";
      }
      if(loader.equalsIgnoreCase("direct")) {
        Map inParams = getRequestParameters("Component:", componentId, request.getParams(), parameters);
        Component component = compManager.getComponent(ParameterUtil.resolveParam(componentId, inParams)[0], getDeployTime(request));
        appendStyle(sb, component);
        String randId = componentId + "_"+ randomGenerator.nextInt();
        if(debugComponents && !component.getId().equalsIgnoreCase("componentFrame")) {
          prependDebug(sb, component, randId, contentType);
        }
        sb.append(compManager.renderComponent(component, contentType, inParams, request.getParams(), false));
        if(debugComponents && !component.getId().equalsIgnoreCase("componentFrame")) {
          appendDebug(sb, component, randId, contentType);
        }
        appendControllers(sb, component);
        
      } else if (loader.equalsIgnoreCase("lazy")) {
        Map inParams = getRequestParameters("Component:", componentId, new HashMap(), parameters);
        appendLazyAJAXCall(sb, componentId, inParams);
      }
      return sb.toString();
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
  }
  
  public static String componentUrl(String componentUrl, String contentType) throws
    XMLTransformerException {

    try {
      StringBuffer sb = new StringBuffer(); 
      IRequest request = reqManager.get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }
      if (contentType == null || contentType.length() == 0) {
        contentType = "xhtml";
      }
      Map inParams = new HashMap(request.getParams());
      String componentId = parseUrl("Component:", componentUrl, request, inParams);
      if (componentId.indexOf(layoutExtension) != -1) {
        return layoutManager.getLayout(ParameterUtil.resolveParam(componentId.replace(layoutExtension, ""), request.getParams())[0], getDeployTime(request)).render(request, true);
      } else {
        Component component = compManager.getComponent(ParameterUtil.resolveParam(componentId, inParams)[0], getDeployTime(request));
        sb.append(compManager.renderComponent(component, contentType, inParams, request.getParams(), false));
        appendControllers(sb, component);
      }
      return sb.toString();
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
  }

  public static String chartUrl(String componentUrl, int width, int height) throws
    XMLTransformerException {
  
    try {
      IRequest request = reqManager.get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }

      Map inParams = new HashMap(request.getParams());
      String componentId = parseUrl("Component:", componentUrl, request, inParams);

      ChartDefinition chartDef = chartManager.getChartDefinition(componentId.replace(chartExtension, ""));
      request.getParams().putAll(inParams);
      return chartBuilder.buildAsImage(chartDef, request, width, height);

    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
  }

  private static void appendLazyAJAXCall(StringBuffer sb, String componentId, Map parameters) {
    Random randomGenerator = new Random();
    //Create container id
    String container = componentId + "_"+ randomGenerator.nextInt();
    //Create url
    StringBuffer url = new StringBuffer();
    url.append(componentId + componentExtension + "?");
    //Create url params
    Iterator paramIt = parameters.keySet().iterator();
    while(paramIt.hasNext()) {
      String thisKey = (String) paramIt.next();
      
      url.append(thisKey + "=" + parameters.get(thisKey) + "&amp;");
    }
    //Create HTML
    sb.append("<div id=\"" + container + "\" class=\"loading\" >\n");
    sb.append("Loading...\n");
    sb.append("</div>\n");
    sb.append("<script type=\"text/javascript\">\n");
    sb.append("Toobs.Controller.lazyLoadComp('" + container + "','" + url + "');\n");
    sb.append("</script>\n");
  }

  private static void appendStyle(StringBuffer sb, Component component) {
    if (component.getStyles().length > 0) {
      for (int i=0; i < component.getStyles().length; i++) {
        if (component.getStyles()[i].length() > 0) {
          sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/common/css/component/" + component.getStyles()[i] + ".css\"></link>");
        }
      }
    }
  }

  private static void prependDebug(StringBuffer sb, Component component, String randId, String contentType) {
    sb.append("<div id=\"" + randId + "_stats\" style=\"display:none;\" class=\"component_stats bluebox\">");
    sb.append("<h2>File: " + component.getFileName() + "</h2>");
    sb.append("<h2>Component: " + component.getId() + "</h2>");
    sb.append("<h2>XSL: ");
    Vector contentTransforms = (Vector) component.getTransforms().get(contentType);
    if (contentTransforms != null) {
      Iterator it = contentTransforms.iterator();
      while (it.hasNext()) {
        Transform transform = (Transform) it.next();
        sb.append(transform.getTransformName());
      }
    }
    sb.append(".xsl</h2>");
    sb.append("</div>");
    sb.append("<div id=\"" + randId + "\" class=\"component_stats_wrapper\" >");
  }

  private static void appendDebug(StringBuffer sb, Component component, String randId, String contentType) {
    sb.append("</div>");
  }

  private static void appendControllers(StringBuffer sb, Component component) {
    if (component.getControllerNames().length > 0) {
      sb.append("<script type=\"text/javascript\">\n");
      for (int i=0; i < component.getControllerNames().length; i++) {
        if (component.getControllerNames()[i].length() > 0) {
          sb.append("Toobs.Controller.useComp('" + component.getControllerNames()[i] + "');\n");
        }
      }
      sb.append("</script>\n");
    }
  }
  
  public static String componentLayoutRef(String layoutId, Object parameters) throws
      XMLTransformerException {
    
    try {
      IRequest request = reqManager.get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }
      request.setParams(getRequestParameters("Layout:", layoutId, request.getParams(), parameters));      
      return layoutManager.getLayout(ParameterUtil.resolveParam(layoutId, request.getParams())[0], getDeployTime(request)).render(request, true);
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
  }

  public static String inlineUrl(String transformUrl, Object inputNode) throws
    XMLTransformerException {
  
    try {
      StringBuffer sb = new StringBuffer(); 
      IRequest request = reqManager.get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }
      IXMLTransformer xmlTransformer = null;
      xmlTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer();
      
      HashMap inParams = new HashMap(); //new HashMap(request.getParams());
      String transformPath = parseUrl("Transform:", transformUrl, request, inParams);
      
      Vector xslSources = new Vector();
      Vector inputXML = new Vector();
      Vector outputXML = new Vector();
      xslSources.add(transformPath);

      // Prepare XML
      if (inputNode != null && inputNode instanceof org.w3c.dom.traversal.NodeIterator) {
        org.w3c.dom.traversal.NodeIterator nodeIter = (org.w3c.dom.traversal.NodeIterator)inputNode;

        inputXML.add(nodeIter.getRoot());
        // This is the Translet case
      } else if (inputNode != null && inputNode instanceof org.apache.xml.dtm.ref.DTMAxisIterNodeList) {
        org.apache.xml.dtm.ref.DTMAxisIterNodeList nodeList = (org.apache.xml.dtm.ref.DTMAxisIterNodeList)inputNode;
        inputXML.add(nodeList.toString());
      } else {
        inputXML.add("<inline/>");
      }

      outputXML = xmlTransformer.transform(xslSources, inputXML, inParams);
      for (int ox = 0; ox < outputXML.size(); ox++) {
        sb.append((String) outputXML.get(ox));
      }
      // Return
      return sb.toString();
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
  }
  
  public static String transformEmail(String transformUrl, String emailContent, Object inputNode) throws
    XMLTransformerException {

  try {
    StringBuffer sb = new StringBuffer(); 
    IRequest request = reqManager.get();
    if (request == null) {
      throw new XMLTransformerException("Invalid request");
    }
    IXMLTransformer xmlTransformer = null;
    xmlTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer();
    
    HashMap inParams = new HashMap(); //new HashMap(request.getParams());
    String transformPath = parseUrl("Transform:", transformUrl, request, inParams);
    
    Vector xslSources = new Vector();
    Vector inputXML = new Vector();
    Vector outputXML = new Vector();
    xslSources.add(transformPath);

    StringBuffer xmlBuf = new StringBuffer();
    String unescapedHtml = StringEscapeUtils.unescapeHtml(emailContent);
    if (log.isDebugEnabled()) {
      log.debug("Template HTML: " + unescapedHtml);
    }
    xmlBuf.append("<emaildata>").append("<EmailContent>")
      .append(unescapedHtml)
      .append("</EmailContent>");
    
    // Prepare XML
    if (inputNode != null && inputNode instanceof org.w3c.dom.traversal.NodeIterator) {
      org.w3c.dom.traversal.NodeIterator nodeIter = (org.w3c.dom.traversal.NodeIterator)inputNode;

      //inputXML.add(nodeIter.getRoot());
      
      TransformerFactory tf=TransformerFactory.newInstance();
      //identity
      Transformer t=tf.newTransformer();
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      t.transform(new DOMSource( (org.w3c.dom.Node)nodeIter.getRoot() ), new StreamResult( os ));
      xmlBuf.append(os.toString("UTF-8"));

      // This is the Translet case
    } else if (inputNode != null && inputNode instanceof org.apache.xml.dtm.ref.DTMAxisIterNodeList) {
      org.apache.xml.dtm.ref.DTMAxisIterNodeList nodeList = (org.apache.xml.dtm.ref.DTMAxisIterNodeList)inputNode;
      throw new XMLTransformerException("transformEmail does not support translets");
      //inputXML.add(nodeList.toString());
    } else {
      inputXML.add("<inline/>");
    }
    xmlBuf.append("</emaildata>");
    
    inputXML.add(xmlBuf.toString());
    outputXML = xmlTransformer.transform(xslSources, inputXML, inParams);
    for (int ox = 0; ox < outputXML.size(); ox++) {
      sb.append((String) outputXML.get(ox));
    }
    // Return
    return sb.toString();
  } catch (Exception ex) {
    throw new XMLTransformerException(ex);
  }
}

  public static String getParam(String name, String defaultValue) throws XMLTransformerException {
    if (name == null) {
      throw new XMLTransformerException("getParam: name must not be null");
    }
    Object value = defaultValue;
    try {
      IRequest request = reqManager.get();
      Map params = request.getParams();
      // Not overriding existing
      if (params.containsKey(name)) {
        value = params.get(name);
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return String.valueOf(value);
  }

  public static boolean setParam(String name, String value) throws XMLTransformerException {
    return setParam(name, value, false);
  }

  public static boolean setParam(String name, String value, boolean overwrite) throws XMLTransformerException {
    if (name == null || value == null) {
      throw new XMLTransformerException("setParam: name and value must not be null");
    }
    try {
      IRequest request = reqManager.get();
      Map params = request.getParams();
      // Not overriding existing
      if (overwrite || !params.containsKey(name)) {
        if (value.length() == 0) {
          params.remove(name);
          if (log.isDebugEnabled()) {
            log.debug("setParam - removed - name: " + name);
          }
        } else {
          params.put(name, value);
          if (log.isDebugEnabled()) {
            log.debug("setParam - name: " + name + " value: " + value);
          }
        }
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return true;
  }

  public static boolean setSessionParam(String name, String value) throws XMLTransformerException {
    if (name == null || value == null) {
      throw new XMLTransformerException("setSessionParam: name and value must not be null");
    }
    try {
      IRequest request = reqManager.get();
      HttpSession session = request.getHttpRequest().getSession();
      session.setAttribute(name, value);
      Map params = request.getParams();
      params.put(name, value);
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return true;
  }

  public static Object getNumberParam(String name, boolean asAverage) throws XMLTransformerException {
    if (name == null) {
      throw new XMLTransformerException("getNumberParam: name must not be null");
    }
    Double value = new Double(0);
    try {
      IRequest request = reqManager.get();
      Map params = request.getParams();
      // Not overriding existing
      if (params.containsKey(name)) {
        value = (Double)params.get(name);
        if (asAverage) {
          Integer count = (Integer)params.get(name + ".count");
          value = value / (double)count.intValue();
        }
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return value;
  }

  public static boolean setNumberParam(String name, Object value, boolean trackAverage) throws XMLTransformerException {
    if (name == null || value == null) {
      throw new XMLTransformerException("setParam: name and value must not be null");
    }
    try {
      IRequest request = reqManager.get();
      Map params = request.getParams();

      if (params.containsKey(name)) {
        Double curVal = (Double)params.get(name);
        curVal += (Double)value; 
        if (trackAverage) {
          Integer count = (Integer)params.get(name + ".count");
          count++;
          params.put(name + ".count", count);
        }
        params.put(name, curVal);
      } else {
        params.put(name + ".count", new Integer(1));
        params.put(name, value);
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return true;
  }

  public static boolean resetNumberParam(String name) throws XMLTransformerException {
    if (name == null) {
      throw new XMLTransformerException("setParam: name and value must not be null");
    }
    try {
      IRequest request = reqManager.get();
      Map params = request.getParams();

      if (params.containsKey(name)) {
        params.put(name, new Double(0));
        if (params.containsKey(name + ".count")) {
          params.put(name + ".count", new Integer(0));
        }
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return true;
  }

  private static String parseUrl(String context, 
      String componentUrl, 
      IRequest request, 
      Map inParams) throws Exception {
    if (componentUrl.indexOf("?") == -1) {
      return componentUrl.replace(componentExtension, "");
    }
    if (log.isDebugEnabled()) {
      log.debug("parseUrl: " + componentUrl);
    }
    String[] compUri = componentUrl.split("\\?");
    String componentId = compUri[0];
    String[] params = compUri[1].split("&");
    for (int i=0; i<params.length; i++) {
      String[] kvp = params[i].split("=");
      if (log.isDebugEnabled()) {
        log.debug("param: " + params[i]);
      }
      if (inParams.containsKey(kvp[0])) {
        String[] newVal;
        Object val = inParams.get(kvp[0]);
        if (val.getClass().isArray()) {
          String[] valAry = (String[])val;
          boolean put = true;
          for (int j = 0; j < valAry.length; j++) {
            if (kvp.length > 1 && valAry[j].equals(kvp[1])) {
              put = false;
              break;
            }
          }
          if (put) {
            newVal = new String[valAry.length+1];
            System.arraycopy(valAry, 0, newVal, 0, valAry.length);
            newVal[valAry.length] = kvp.length > 1 ? kvp[1] : "";
            inParams.put(kvp[0], newVal);
          }
        } else if (kvp.length > 1 && !val.equals(kvp[1])) {
          newVal = new String[2];
          newVal[0] = (String)val;
          newVal[1] = kvp[1];
          inParams.put(kvp[0], newVal);
        }
      } else {
        inParams.put(kvp[0], kvp.length > 1 ? kvp[1] : "");
      }
    }
    return componentId.replace(componentExtension, "");
  }

  private static Map getRequestParameters(String context, 
      String scopeId, 
      Map requestParams, 
      Object parameters) throws Exception {
    Map outParams = new HashMap(requestParams);
    if (parameters != null) {
      ArrayList paramList = new ArrayList();
      // This is the static XSL case
      if (parameters instanceof org.w3c.dom.traversal.NodeIterator) {
        org.w3c.dom.traversal.NodeIterator nodeIter = (org.w3c.dom.traversal.NodeIterator)parameters;
        org.w3c.dom.Node currentNode = null;
        while ((currentNode = nodeIter.nextNode()) != null) {
          processNode(currentNode, paramList);
        }
      }
      // This is the Translet case
      if (parameters instanceof org.apache.xml.dtm.ref.DTMAxisIterNodeList) {
        org.apache.xml.dtm.ref.DTMAxisIterNodeList nodeList = (org.apache.xml.dtm.ref.DTMAxisIterNodeList)parameters;
        int nodes = nodeList.getLength();
        for (int i = 0; i < nodes; i++) {
          processNode(nodeList.item(i), paramList);
        }
      }
      Parameter[] paramMap = new Parameter[paramList.size()];
      paramMap = (Parameter[])paramList.toArray(paramMap);
      ParameterUtil.mapParameters(context + scopeId, paramMap, requestParams, outParams, scopeId);
    }
    return outParams;
  }
  
  private static void processNode(org.w3c.dom.Node currentNode, ArrayList paramList) throws Exception {
    if (currentNode != null && currentNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
      org.w3c.dom.NamedNodeMap nodeMap = currentNode.getAttributes();
      Map nodeAttributes = new HashMap();
      for (int a = 0; a < nodeMap.getLength(); a++) {
        org.w3c.dom.Node attrNode = nodeMap.item(a);
        nodeAttributes.put(attrNode.getNodeName(), attrNode.getNodeValue());
      }
      Parameter param = new Parameter();
      BeanMonkey.populate(param, nodeAttributes, true);
      if (param.getPath() == null && param.getIsStatic()) param.setPath("");
      //log.info("Ref Param - Name: " + param.getName() + " Value: " + param.getPath());
      paramList.add(param);
    }
  }
  
  /*
  public static String getToken() throws XMLTransformerException {
    IRequest request = reqManager.get();
    if (request == null) {
      throw new XMLTransformerException("Invalid request");
    }
    return (String)request.getParams().get(PlatformConstants.REQUEST_GUID);
  }
  */
  
  public static String getUserAgent() throws XMLTransformerException {
    try {
      IRequest request = reqManager.get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }
      return request.getHttpRequest().getHeader("user-agent");
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      throw new XMLTransformerException(ex);
    }
  }
  
  public static String pageLinks(int pageSize, int firstResult, int totalRows, int pagesDisplayed) {
    StringBuffer sb = new StringBuffer();
    int currentPage=firstResult;
    if (currentPage >= (pageSize*pagesDisplayed)) {
      sb.append("<a class=\"pageLink\" href=\"#\" page=\"");
      sb.append("0");
      sb.append("\" title=\"Page 1\">1</a>...");
    }
    for (int i=0; i<pagesDisplayed; i++) {
      int pageNum = currentPage/pageSize;
      sb.append("<a class=\"pageLink\" href=\"#\" page=\"");
      sb.append(currentPage);
      sb.append("\" title=\"Page ");
      sb.append(pageNum+1);
      sb.append("\">");
      sb.append(pageNum+1);
      sb.append("</a>");
      currentPage += pageSize;
      if (currentPage > totalRows) break;
    }
    
    return sb.toString();
  }

}
