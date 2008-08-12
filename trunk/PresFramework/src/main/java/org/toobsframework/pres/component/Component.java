package org.toobsframework.pres.component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.xml.transform.URIResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.component.config.GetObject;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.component.datasource.api.DataSourceInitializationException;
import org.toobsframework.pres.component.datasource.api.DataSourceNotInitializedException;
import org.toobsframework.pres.component.datasource.api.IDataSource;
import org.toobsframework.pres.component.datasource.api.IDataSourceObject;
import org.toobsframework.pres.component.datasource.api.InvalidSearchContextException;
import org.toobsframework.pres.component.datasource.api.InvalidSearchFilterException;
import org.toobsframework.pres.component.datasource.api.ObjectCreationException;
import org.toobsframework.pres.component.datasource.api.ObjectNotFoundException;
import org.toobsframework.pres.component.datasource.manager.DataSourceManager;
import org.toobsframework.pres.component.datasource.manager.DataSourceNotFoundException;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.CookieVO;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.servlet.ContextHelper;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;
import org.toobsframework.util.BetwixtUtil;
import org.toobsframework.util.Configuration;


/**
 * @author pudney
 */
@SuppressWarnings("unchecked")
public class Component {
  private static final String XML_HEADER = "<?xml version=\"1.0\"?>";
  private static final String XML_START_COMPONENTS = "<component";
  private static final String XML_END_COMPONENTS = "</component>";
  private static final String XML_START_OBJECTS = "<objects>";
  private static final String XML_END_OBJECTS = "</objects>";
  private static final String XML_START_ERRORS = "<errors>";
  private static final String XML_END_ERRORS = "</errors>";
  private static final String XML_START_ERROR_OBJS = "<errorobjects>";
  private static final String XML_END_ERROR_OBJS = "</errorobjects>";

  private static Log log = LogFactory.getLog(Component.class);
  private ComponentRequestManager componentRequestManager;
  
  private String Id;

  private IDataSource ds;

  private boolean renderErrorObject;

  private boolean scanUrls;

  private boolean initDone;

  private String dsClassName;

  private Map dsParams;

  private Map transforms;

  private GetObject[] objectsConfig;
  
  private String[] controllerNames;

  private String[] styles;

  private String fileName;

  public Component() {
    this.Id = null;
    this.initDone = false;
    this.dsClassName = null;
    this.dsParams = null;
    componentRequestManager = (ComponentRequestManager)ContextHelper.getInstance().getWebApplicationContext().getParentBeanFactory().getBean("componentRequestManager");
  }

  public Component(String dsClassName, Map dsParams) {
    this.Id = null;
    this.initDone = false;
    this.dsClassName = dsClassName;
    this.dsParams = dsParams;
    componentRequestManager = (ComponentRequestManager)ContextHelper.getInstance().getWebApplicationContext().getParentBeanFactory().getBean("componentRequestManager");
  }

  public IDataSourceObject[] getObjects(Map paramsIn, Map paramsOut) throws ComponentException,
      ComponentNotInitializedException, ParameterException {
    ArrayList allObjects = new ArrayList();
    if (!this.initDone) {
      ComponentNotInitializedException ex = new ComponentNotInitializedException();
      ex.setComponentId(this.Id);
      throw ex;
    }
    int len = objectsConfig.length;
    for (int i = 0; i < len; i++) {
      Map params = new HashMap(paramsIn);
      GetObject thisObjDef = objectsConfig[i];
      //Fix the params using the param mapping for 
      //this configuration.
      if(thisObjDef.getParameters() != null){
        ParameterUtil.mapParameters("Component:" + this.Id + ":GetObject:" + thisObjDef.getDaoObject(), thisObjDef.getParameters().getParameter(), params, params, this.Id, allObjects);
      }

      ArrayList theseObjects = new ArrayList();
      
      //Call the appropriate action.
      Map outParams = new HashMap();
      if (thisObjDef.getObjectAction().equals("get")) {
        Object guidObj = null;
        String thisGuidParam = ParameterUtil.resolveParam(thisObjDef.getGuidParam(), params)[0];
        if (thisGuidParam != null && params.containsKey(thisGuidParam)) {
          guidObj = params.get(thisGuidParam);
        }
        if (guidObj != null && guidObj.getClass().isArray()) {
          guidObj = ((String[]) params.get(thisGuidParam))[0];
        } else {
          guidObj = (String) params.get(thisGuidParam);
        }
        Object obj = null;
        obj = checkForValidation(paramsIn, thisObjDef, (String)guidObj);
        if (obj == null) {
          obj = this.getObject(
              thisObjDef.getReturnedValueObject(),
              ParameterUtil.resolveParam(thisObjDef.getDaoObject(), params)[0], 
              thisObjDef.getNoCache(), 
              (String)guidObj, 
              params, 
              outParams);
        }
        theseObjects.add(obj);
      } else if (thisObjDef.getObjectAction().equals("getCookie")) {
        String searchCriteria = ParameterUtil.resolveParam(thisObjDef.getSearchCriteria(), params)[0];
        String thisGuidParam = ParameterUtil.resolveParam(thisObjDef.getGuidParam(), params)[0];
        String cookieName = (searchCriteria != null ? searchCriteria : "");
        Object guidValue = params.get(thisGuidParam);
        if (guidValue != null && guidValue.getClass().isArray()) {
          cookieName += ((String[])guidValue)[0];
        } else {
          cookieName += guidValue;
        }
        String cookieValue = null;
        
        Cookie[] cookies = componentRequestManager.get().getHttpRequest().getCookies();
        if (cookies != null) {
          for (int c = 0; c < cookies.length; c++) {
            Cookie cookie = cookies[c];
            if (cookie.getName().equals(cookieName)) {
              cookieValue = cookie.getValue();
              break;
            }
          }
        }
        if (cookieName != null && cookieValue != null) {
          theseObjects.add(this.createObject(new CookieVO(cookieName, cookieValue)));
        }
      } else if (thisObjDef.getObjectAction().equals("search")) {
        try {
          theseObjects.addAll(this.ds.search(
              ParameterUtil.resolveParam(thisObjDef.getReturnedValueObject(), params)[0], 
              ParameterUtil.resolveParam(thisObjDef.getDaoObject(), params)[0], 
              thisObjDef.getSearchCriteria(),
              thisObjDef.getSearchMethod(), 
              thisObjDef.getPermissionAction(), 
              params, outParams));
        } catch (ObjectCreationException e) {
          throw new ComponentException("Problem fetching object:" + this.Id + ":" + thisObjDef.getDaoObject() + ":" + thisObjDef.getReturnedValueObject(), e);
        } catch (InvalidSearchContextException e) {
          throw new ComponentException("Invalid Search Context", e);
        } catch (InvalidSearchFilterException e) {
          throw new ComponentException("Ivalid Search Filter", e);
        } catch (DataSourceNotInitializedException e) {
          throw new ComponentException("Datasource Not Initialized", e);
        }
      } else if (thisObjDef.getObjectAction().equals("searchIndex")) {
        try {
          theseObjects.addAll(this.ds.searchIndex(
              thisObjDef.getReturnedValueObject(),
              ParameterUtil.resolveParam(thisObjDef.getDaoObject(), params)[0], 
              thisObjDef.getSearchCriteria(),
              thisObjDef.getSearchMethod(), 
              thisObjDef.getPermissionAction(), 
              params, outParams));
        } catch (ObjectCreationException e) {
          throw new ComponentException("Problem fetching object:" + thisObjDef.getDaoObject(), e);
        } catch (InvalidSearchContextException e) {
          throw new ComponentException("Invalid Search Context", e);
        } catch (InvalidSearchFilterException e) {
          throw new ComponentException("Ivalid Search Filter", e);
        } catch (DataSourceNotInitializedException e) {
          throw new ComponentException("Datasource Not Initialized", e);
        }
      }
      ParameterUtil.mapScriptParams(outParams, paramsIn);
      if(thisObjDef.getOutputParameters() != null){
        ParameterUtil.mapOutputParameters(thisObjDef.getOutputParameters().getParameter(), paramsIn, this.Id, theseObjects);
        if (paramsOut != null) {
          ParameterUtil.mapOutputParameters(thisObjDef.getOutputParameters().getParameter(), paramsOut, this.Id, theseObjects);
        }
      }
      allObjects.addAll(theseObjects);
    }
    allObjects.trimToSize();
    IDataSourceObject[] objArray = new IDataSourceObject[allObjects.size()];
    objArray = (IDataSourceObject[]) allObjects.toArray(objArray);
    return objArray;
  }

  public IDataSourceObject createObject(Object valueObject) throws ComponentException,
      ComponentNotInitializedException {
    IDataSourceObject object = null;
    if (!this.initDone) {
      ComponentNotInitializedException ex = new ComponentNotInitializedException();
      ex.setComponentId(this.Id);
      throw ex;
    }
    try {
      if (valueObject instanceof ArrayList) {
        object = this.ds.createObject(((ArrayList)valueObject).get(0));
      } else {
        object = this.ds.createObject(valueObject);
      }
    } catch (DataSourceNotInitializedException ex) {
      ComponentException ce = new ComponentException("Datasource not initialized.", ex);
      throw ce;
    }
    return object;
  }

  public IDataSourceObject getObject(String returnedValueObject,
      String daoObject, boolean noCache, String guid, Map params, Map outParams) throws ComponentException,
      ComponentNotInitializedException {
    IDataSourceObject object = null;
    if (!this.initDone) {
      ComponentNotInitializedException ex = new ComponentNotInitializedException();
      ex.setComponentId(this.Id);
      throw ex;
    }
    try {
      if (!noCache) {
        object = componentRequestManager.checkRequestCache("get", returnedValueObject, guid);
      }
      if (object == null) {
        object = this.ds.getObject(returnedValueObject, daoObject, guid, params, outParams);
        if (!noCache) {
          componentRequestManager.cacheObject("get", returnedValueObject, guid, object);
        }
      }
    } catch (DataSourceNotInitializedException ex) {
      ComponentException ce = new ComponentException("Datasource not initialized.", ex);
      throw ce;
    } catch (ObjectNotFoundException ex) {
      ComponentException ce = new ComponentException("Error getting object " +
          returnedValueObject + " for component " + this.Id, ex);
      throw ce;
    }
    return object;
  }
  
  public void init() throws DataSourceInitializationException,
      DataSourceNotFoundException {
    if (this.ds == null) {
      DataSourceManager dsm = DataSourceManager.getInstance();
      this.ds = dsm.getDataSource(this.dsClassName, this.dsParams);
    }
    this.initDone = true;
  }

  public String render(String contentType, Map params, Map paramsOut)
    throws ComponentNotInitializedException, ComponentException, ParameterException {
    return this.render(contentType, params, paramsOut, null);
  }

  /**
   * Runs the objects through the xml pipeline to get the proper rendering of
   * the component as defined in the config file.
   * 
   * @return rendered component
   * @throws ComponentNotInitializedException
   * @throws ComponentException
   */
  public String render(String contentType, Map params, Map paramsOut, URIResolver uriResolver)
      throws ComponentNotInitializedException, ComponentException, ParameterException {
    StringBuffer renderedOutput = new StringBuffer();
    Date start = new Date();
    String componentXML = this.getObjectsAsXML(params, paramsOut);
    Date endGet = new Date();

    if (!contentType.equals("bizXML")) {
      renderedOutput.append(this.callXMLPipeline(contentType, componentXML, params, uriResolver));
      /*
      if (this.getControllerNames().length > 0) {
        renderedOutput.append("<script type=\"text/javascript\">\n");
        for (int i=0; i < this.getControllerNames().length; i++) {
          if (this.getControllerNames()[i].length() > 0) {
            renderedOutput.append("setTimeout( function() { Toobs.Controller.useComp('" + this.getControllerNames()[i] + "'); }, 100);\n");
          }
        }
        renderedOutput.append("</script>\n");
      }
      */
    } else {
      renderedOutput.append(componentXML);
    }
    Date end = new Date();
    if (log.isDebugEnabled()) {
      log.debug("Comp [" + Id + "] gTime: " + (endGet.getTime()-start.getTime()) + " rTime: " + (end.getTime()-endGet.getTime()) + " fTime: " + (end.getTime()-start.getTime()));
    }
    return renderedOutput.toString();
  }

  /**
   * Gets all of the objects in this component as a single xml file with a
   * proper wrapper.
   * 
   * @return component as xml
   * @throws ComponentNotInitializedException
   * @throws ComponentException
   */
  private String getObjectsAsXML(Map params, Map paramsOut)
      throws ComponentNotInitializedException, ComponentException, ParameterException {
    StringBuffer xml = new StringBuffer();
    IDataSourceObject[] objects = this.getObjects(params, paramsOut);
    try {
      xml.append(XML_HEADER);
      xml.append(XML_START_COMPONENTS).append(" id=\"").append(this.Id).append("\">");
      xml.append(XML_START_OBJECTS);
      if ((objects != null) && (objects.length > 0)) {
        for (int i = 0; i < objects.length; i++) {
          xml.append(objects[i].toXml());
        }
      }
      xml.append(XML_END_OBJECTS);
      if (renderErrorObject) {
        if (params.containsKey(PresConstants.VALIDATION_ERROR_MESSAGES)) {
          componentRequestManager.get().getHttpResponse().setHeader("toobs.error.validation", "true");
          List globalErrorList = (List)params.get(PresConstants.VALIDATION_ERROR_MESSAGES);
          for (int g = 0; g < globalErrorList.size(); g++) {
            xml.append(XML_START_ERRORS);
            List errorList = (List)globalErrorList.get(g);
            for (int i = 0; i < errorList.size(); i++) {
              xml.append(BetwixtUtil.toXml(errorList.get(i)));
            }
            xml.append(XML_END_ERRORS);
          }
        }
        if (params.containsKey(PresConstants.VALIDATION_ERROR_OBJECTS)) {
          List globalMessageList = (List)params.get(PresConstants.VALIDATION_ERROR_OBJECTS);
          for (int g = 0; g < globalMessageList.size(); g++) {
            xml.append(XML_START_ERROR_OBJS);
            List errorList = (List)globalMessageList.get(g);        
            for (int i = 0; i < errorList.size(); i++) {
              xml.append(BetwixtUtil.toXml(errorList.get(i)));
            }
            xml.append(XML_END_ERROR_OBJS);
          }
        }
      }
      xml.append(XML_END_COMPONENTS);
    } catch (IOException ex) {
      throw new ComponentException("Error getting xml for object", ex);
    }

    return xml.toString();
  }

  /**
   * Runs the objects through the xml pipeline to get the proper rendering of
   * the component as defined in the config file.
   * 
   * @return rendered component
   * @throws ComponentNotInitializedException
   * @throws ComponentException
   */
  private String callXMLPipeline(String contentType, String inputXMLString, Map inParams, URIResolver uriResolver)
      throws ComponentException, ParameterException {
    StringBuffer outputString = new StringBuffer();
    Vector outputXML = new Vector();

    try {
      // Initialize variables needed to run transformer.
      IXMLTransformer xmlTransformer = null;
      Vector inputXSLs = new Vector();
      HashMap params = new HashMap();
      Vector inputXML = new Vector();

      // Prepare XML
      inputXML.add(inputXMLString);

      // Prepare XSLs and Params.
      Vector contentTransforms = (Vector) this.getTransforms().get(contentType);
      if (contentTransforms != null && contentTransforms.size() > 0) {
        Iterator it = contentTransforms.iterator();
        while (it.hasNext()) {
          Transform transform = (Transform) it.next();
          inputXSLs.add(transform.getTransformName());
          //Fix the params using the param mapping for 
          //this configuration.
          if(transform.getTransformParams() != null){
            ParameterUtil.mapParameters("Transform:" + transform.getTransformName(), transform.getTransformParams().getParameter(), inParams, params, this.Id);
          }
        }
      } else {
        throw new ComponentException("Component with id: " + this.Id + " does not have a transform for content type: " + contentType);
      }
      //ParameterUtil.mapFrameworkParams(inParams, params);

      // Figure out which Transformer to run and prepare as
      // necessary for that Transformer.
      Vector xslSources = new Vector();
      xslSources.addAll(inputXSLs);
      if (xslSources.size() > 1) {
        if (!"xhtml".equals(contentType)) {
          xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_XML, uriResolver);
        } else {
          xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_HTML, uriResolver);
        }
      } else {
        xmlTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer(uriResolver);
      }

      // Do Transformation
      xslSources.trimToSize();
      if (xslSources.size() > 0) {
        params.put("context", Configuration.getInstance().getMainContext() + "/");
        if (inParams.get("appContext") != null) {
          params.put("appContext", inParams.get("appContext"));
        }
        outputXML = xmlTransformer.transform(xslSources, inputXML, params);
      } else {
        outputXML = inputXML;
      }

    } catch (XMLTransformerException xte) {
      log.error(xte);
      throw new ComponentException("Error running transform", xte);
    }

    // Prepare output
    for (int ox = 0; ox < outputXML.size(); ox++) {
      outputString.append((String) outputXML.get(ox));
    }
    // Return
    return outputString.toString();

  }

  private Object checkForValidation(Map paramsIn, GetObject getObjDef, String guid) throws ComponentException, ComponentNotInitializedException {
    List<Object> errorObjects = (List<Object>)paramsIn.get(PresConstants.VALIDATION_ERROR_OBJECTS);
    if(errorObjects != null)
    {
      for(int i = 0; i < errorObjects.size(); i++)
      {
        Object errorObject = errorObjects.get(i);
        Class errorObjClass = errorObject.getClass();
        
        //if the error obj class and the returned value object match...
        String errorObjClassName = errorObjClass.getName();
        errorObjClassName = errorObjClassName.substring(errorObjClassName.lastIndexOf(".") + 1);
        if(errorObjClassName.equals(getObjDef.getReturnedValueObject()))
        {
          //then check the guid value
          try {
            Method getGuidMethod = errorObjClass.getMethod("getGuid");
            String errorObjGuid = (String)getGuidMethod.invoke(errorObject);
            // if guids match, return the error obj instance
            if(errorObjGuid != null && errorObjGuid.equals(guid)) 
              return this.createObject(errorObject);

          // if exception, then just continue
          } catch(NoSuchMethodException nsme) {
          } catch(IllegalAccessException iae) {
          } catch(InvocationTargetException iae) {
          }
        }
      }
    }
    
    return null;
  }
  
  /**
   * Loads the transformer based on the type of transform requested.
   * 
   * @param type
   * 
   * @return XMLTransformer
   * @throws StrutsCXException
  private IXMLTransformer getXMLTransformer(String type)
      throws XMLTransformerException {
    return XMLTransformerFactory.getInstance().getXMLTransformer(type);
  }
  */

  public IDataSource getDs() {
    return ds;
  }

  public void setDs(IDataSource ds) {
    this.ds = ds;
  } 

  public GetObject[] getObjectsConfig() {
    return objectsConfig;
  }

  public void setObjectsConfig(GetObject[] objectsConfig) {
    this.objectsConfig = objectsConfig;
  }

  public void setId(String Id) {
    this.Id = Id;
  }

  public String getId() {
    return this.Id;
  }

  public void setTransforms(Map transforms) {
    this.transforms = transforms;
  }

  public Map getTransforms() {
    return this.transforms;
  }

  public boolean isRenderErrorObject() {
    return renderErrorObject;
  }

  public void setRenderErrorObject(boolean renderErrorObject) {
    this.renderErrorObject = renderErrorObject;
  }

  public boolean isScanUrls() {
    return scanUrls;
  }

  public void setScanUrls(boolean scanUrls) {
    this.scanUrls = scanUrls;
  }

  public String[] getControllerNames() {
    return controllerNames;
  }

  public void setControllerNames(String[] controllerNames) {
    this.controllerNames = controllerNames;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String[] getStyles() {
    return styles;
  }

  public void setStyles(String[] styles) {
    this.styles = styles;
  }

  public String getDsClassName() {
    return dsClassName;
  }

  public void setDsClassName(String dsClassName) {
    this.dsClassName = dsClassName;
  }

  public Map getDsParams() {
    return dsParams;
  }

  public void setDsParams(Map dsParams) {
    this.dsParams = dsParams;
  }

}
