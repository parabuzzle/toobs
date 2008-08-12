package org.toobsframework.transformpipeline.domain;

import java.util.Properties;
import java.util.Vector;
import java.util.HashMap;

import javax.xml.transform.URIResolver;

/**
 * This is the Base Interface for all datatable domain objects
 *
 * @author gtian
 * @version 1.0
 */
public interface IXMLTransformer {

  /** Holds the constant reference to the request parameter for storing the input xml vector. */
  public static final String INPUT_XML_REQUEST_PARAM = "inputXMLVec";

  /** Holds the constant reference to the request parameter for storing the input xml vector. */
  public static final String INPUT_XSL_REQUEST_PARAM = "inputXSLVec";

  /** Holds the constant reference to the request parameter for storing the input params used
   * in the xsl transforms. */
  public static final String INPUT_PARAM_REQUEST_PARAM = "inputXSLParamVec";

  public static final String USE_TRANSLETS = "xmlpipeline.UseTranslets";

  public static final String USE_CHAIN = "xmlpipeline.UseChain";

  public static final String CACHE_XSL_FILES = "xmlpipeline.CacheXSLFiles";

  /**
   * Contains the transformation logic.
   *
   * @param request
   * @param response
   * @param session
   * @param servlet
       * @param document the XML Document object create by the StrutsCXDocumentBuilder
   * @param notransform indicates if XML document should not get transformed
   *
   * @throws StrutsCXXSLTException
   */
  @SuppressWarnings("unchecked")
  public Vector transform(
      Vector inputXSLs,
      Vector inputXMLs,
      HashMap inputParams) throws XMLTransformerException;

  public void setOutputProperties(Properties outputProperties);
  
  public void setURIResolver(URIResolver uriResolver); 
}
