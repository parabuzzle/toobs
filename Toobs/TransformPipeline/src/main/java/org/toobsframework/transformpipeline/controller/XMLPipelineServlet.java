package org.toobsframework.transformpipeline.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;

import java.util.Vector;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.HashMap;

/**
 * This is the main XMLPipelineServlet.  It takes requests and
 */
public class XMLPipelineServlet
    extends HttpServlet {
  private static final long serialVersionUID = -8597024562115016690L;

  private boolean useTranslets = false;

  private boolean useChain = false;

  /**
   * To get the logger instance
   */
  private static Log nslog = LogFactory.getLog(XMLPipelineServlet.class);
  /**
   * The Servlet init() method. This will eventually read in the xml pipeline def
   * file on init.  That file will be used to figure the transformations necessary.
   *
   * @param servlet
   *
   * @throws ServletException
   */
  public void init(ServletConfig servlet) throws ServletException {

    if (nslog.isDebugEnabled()) {
      nslog.debug("In xmlpipeline init()");
    }
    super.init();
    //useTranslets = ConfUtil.getBooleanProperty(IXMLTransformer.USE_TRANSLETS);
    //useChain = ConfUtil.getBooleanProperty(IXMLTransformer.USE_CHAIN);
    if (nslog.isDebugEnabled()) {
      nslog.debug("Out xmlpipeline init()");
    }
  }

  /**
   *
   * Do Get
   *
   * @param HttpServletRequest request
   * @param HttpServletResponse response
   *
   * @throws ServletException
   */
  public void doPost(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException {

    doGet(request, response);
  }

  /**
   * The Servlet doGet() method. This method does the work.
   *
   *
   * @param request
   *          the HttpServletRequest
   * @param response
   *          the HttpServletResponse
   *
   * @throws ServletException
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws
      ServletException {

    if (nslog.isDebugEnabled()) {
      nslog.debug("In xmlpipeline doGet()");
    }
    callXMLTransformer(request, response);
    if (nslog.isDebugEnabled()) {
      nslog.debug("Out xmlpipeline doGet()");
    }
  }

  /**
   * The empty Servlet standard destroy method. For later use...
   */
  public void destroy() {
    // nothing to destroy
  }

  /**
   * Loads the transformer based on the type of transform requested.
   *
   * @param type
   *
   * @return XMLTransformer
   * @throws StrutsCXException
   */
  private IXMLTransformer getXMLTransformer(String type) throws
      XMLTransformerException {
    return XMLTransformerFactory.getInstance().getXMLTransformer(type);
  }

  private void callXMLTransformer(
      HttpServletRequest request,
      HttpServletResponse response) throws ServletException {

    try {
      //Later this will all be read from an xml file, but hardcoded in the
      //request for now.
      IXMLTransformer xmlTransformer = null;
      Vector inputXSLs = (Vector) request.getAttribute(IXMLTransformer.
          INPUT_XSL_REQUEST_PARAM);
      Vector xslSources = new Vector();
      if (useTranslets && useChain) {
        xmlTransformer = getXMLTransformer(XMLTransformerFactory.TRANSLET_CHAIN_XSL);
        xslSources.addAll(inputXSLs);
      } else if (useTranslets) {
        xmlTransformer = getXMLTransformer(XMLTransformerFactory.TRANSLET_XSL);
        xslSources.addAll(inputXSLs);
      } else if (useChain) {
        xmlTransformer = getXMLTransformer(XMLTransformerFactory.CHAIN_XSL);
        xslSources.addAll(inputXSLs);
      } else {
        // load the static transformer for the dataype xsl transform.
        xmlTransformer = getXMLTransformer(XMLTransformerFactory.STATIC_XSL);
        ClassLoader cld = XMLPipelineServlet.class.getClassLoader();
        for (int ix = 0; ix < inputXSLs.size(); ix++) {
          String xslFileName = (String) inputXSLs.get(ix) + ".xsl";
          xslFileName = cld.getResource(xslFileName).getFile();
          File xslFile = new File(xslFileName);
          StreamSource xslSource = new StreamSource(xslFile);
          xslSources.add(xslSource);
        }
      }

      // transform
      Vector outputXML = xmlTransformer.transform(
        xslSources,
        (Vector) request.getAttribute(IXMLTransformer.INPUT_XML_REQUEST_PARAM),
        (HashMap) request.getAttribute(IXMLTransformer.INPUT_PARAM_REQUEST_PARAM));

      PrintWriter writer = response.getWriter();

      for (int ox = 0; ox < outputXML.size(); ox++) {
        writer.write((String) outputXML.get(ox));
      }
      writer.flush();
    }
    catch (XMLTransformerException xte) {
      nslog.error("XML Transformer Exception", xte);
      throw new ServletException("XML Transformer Exception: " + xte.getMessage());
    }
    catch (IOException ioe) {
      nslog.error("IOException Exception", ioe);
      throw new ServletException("IOException Exception: " + ioe.getMessage());
    }
  }
}
