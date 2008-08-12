package org.toobsframework.pres.layout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.URIResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.Transform;
import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.pres.layout.config.DoItRef;
import org.toobsframework.pres.layout.config.Section;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;
import org.toobsframework.util.BetwixtUtil;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.IRequest;


@SuppressWarnings("unchecked")
public class RuntimeLayout {
  private static Log log = LogFactory.getLog(RuntimeLayout.class);

  private static final String XML_HEADER = "<RuntimeLayout>";
  private static final String XML_FOOTER = "</RuntimeLayout>";
  private static final String XML_CP_HEADER = "<TransformParams>";
  private static final String XML_CP_FOOTER = "</TransformParams>";
  private String id;
  private Map transforms = new HashMap();
  private RuntimeLayoutConfig config;
  private String layoutXml;
  private DoItRef doItRef;
  
  public RuntimeLayoutConfig getConfig() {
    return config;
  }
  public void setConfig(RuntimeLayoutConfig config) throws IOException {
    this.config = config;
    StringBuffer sb = new StringBuffer();
    sb.append(XML_HEADER);
    Parameter[] contentParams = config.getAllTransformParams();
    if (contentParams.length > 0) {
      sb.append(XML_CP_HEADER);
      for (int c = 0; c < contentParams.length; c++) {
        sb.append(BetwixtUtil.toXml(contentParams[c], true, false, false, null, null));
      }
      sb.append(XML_CP_FOOTER);
    }
    //sb.append(BetwixtUtil.toXml(config.getAllParams()));
    Section[] sections = config.getAllSections();
    for (int s = 0; s < sections.length; s++) {
      sb.append(BetwixtUtil.toXml(sections[s], true, false, false, null, null));
    }
    sb.append(XML_FOOTER);
    this.setLayoutXml(sb.toString());
  }
  /*
  public ArrayList getTransforms() {
    return transforms;
  }
  public Transform[] getAllTransforms() {
    Transform[] allTransforms = new Transform[transforms.size()];
    return (Transform[])transforms.toArray(allTransforms);
  }
  public void setTransforms(ArrayList transforms) {
    this.transforms = transforms;
  }
  public void addTransform(Transform transform) {
    addTransform(new Transform[] {transform});
  }
  public void addTransform(Transform[] transform) {
    for (int i = 0; i < transform.length; i++) {
      transforms.add(transform[i]);
    }
  }
  */
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  
  public String getLayoutXml() {
    return layoutXml;
  }
  public void setLayoutXml(String layoutXml) {
    this.layoutXml = layoutXml;
  }
  
  public String render(IRequest request) throws ComponentException, ParameterException {
    return this.render(request, null, "xhtml");  
  }
  
  public String render(IRequest request, URIResolver uriResolver) throws ComponentException, ParameterException {
    return this.render(request, uriResolver, "xhtml");
  }
  
  public String render(IRequest request, URIResolver uriResolver, String contentType) throws ComponentException, ParameterException {
    IXMLTransformer xmlTransformer = null;
    StringBuffer outputString = new StringBuffer();
    HashMap layoutParams = new HashMap();
    
    Vector outputXML = new Vector();
    try {
      Vector inputXSLs = new Vector();
      Vector inputXMLs = new Vector();
      Vector contentTransforms = (Vector) this.getTransforms().get(contentType);
      if (contentTransforms != null && contentTransforms.size() > 0) {
        Iterator it = contentTransforms.iterator();
        while (it.hasNext()) {
          Transform transform = (Transform) it.next();
          inputXSLs.add(transform.getTransformName());
          //Fix the params using the param mapping for 
          //this configuration.
          if(transform.getTransformParams() != null){
            ParameterUtil.mapParameters("Transform:" + transform.getTransformName(), transform.getTransformParams().getParameter(), request.getParams(), layoutParams, this.id);
          }
        }
      } else {
        throw new ComponentException("Component Layout with id: " + this.id + " does not have a transform for content type: " + contentType);
      }

      log.debug("uriResolver: " + uriResolver);
      if (inputXSLs.size() > 1) {
        if (!"xhtml".equals(contentType)) {
          xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_XML, uriResolver);
          if (request.getParams().get("outputFormat") != null) {
            layoutParams.put("outputFormat", request.getParams().get("outputFormat"));
          }
        } else {
          xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_HTML, uriResolver);
        }
      } else {
        xmlTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer(uriResolver);
      }

      ParameterUtil.mapParameters("Layout:" + this.id, config.getAllTransformParams(), request.getParams(), layoutParams, this.id);

      inputXMLs.add(this.layoutXml);
      layoutParams.put("context", Configuration.getInstance().getMainContext() + "/");
      if (request.getParams().get("appContext") != null) {
        layoutParams.put("appContext", request.getParams().get("appContext"));
      }
      outputXML = xmlTransformer.transform(inputXSLs, inputXMLs, layoutParams);
    } catch (XMLTransformerException e) {
      throw new ComponentException(e);
    }

    for (int ox = 0; ox < outputXML.size(); ox++) {
      outputString.append((String) outputXML.get(ox));
    }
    // Return
    return outputString.toString();
  }

  public DoItRef getDoItRef() {
    return doItRef;
  }
  public void setDoItRef(DoItRef doItRef) {
    this.doItRef = doItRef;
  }
  public Map getTransforms() {
    return transforms;
  }
  public void setTransforms(Map transforms) {
    this.transforms = transforms;
  }
}
