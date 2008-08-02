package org.toobs.framework.pres.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.WebUtils;
import org.toobs.framework.exception.ParameterException;
import org.toobs.framework.pres.component.config.Parameter;
import org.toobs.framework.pres.doit.config.Forward;
import org.toobs.framework.util.Configuration;


@SuppressWarnings("unchecked")
public class ParameterUtil {

  private static Log log = LogFactory.getLog(ParameterUtil.class);
  private static List excludedParameters;
  private static Map envParameters;
  static {
    excludedParameters = new ArrayList();
    
    excludedParameters.add("org.springframework.web.servlet.DispatcherServlet.THEME"); 
    excludedParameters.add("org.springframework.web.servlet.DispatcherServlet.THEME_RESOLVER");
    excludedParameters.add("org.springframework.web.servlet.DispatcherServlet.THEME_SOURCE"); 
    excludedParameters.add("org.springframework.web.servlet.DispatcherServlet.CONTEXT"); 
    excludedParameters.add("org.springframework.web.servlet.DispatcherServlet.LOCALE");
    excludedParameters.add("org.springframework.web.servlet.DispatcherServlet.LOCALE_RESOLVER");
    excludedParameters.add("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping");
    
    excludedParameters.add("hibernateFilter.FILTERED");
    
    envParameters = new HashMap();
    envParameters.put("host", Configuration.getInstance().getMainHost() );
    envParameters.put("toobs.debug", Configuration.getInstance().getProperty("toobs.debug", "false") );
  }
  
  /**
   * Extract the URL filename from the given request URI.
   * Delegates to <code>WebUtils.extractViewNameFromUrlPath(String)</code>.
   * @param uri the request URI (e.g. "/index.html")
   * @return the extracted URI filename (e.g. "index")
   * @see org.springframework.web.util.WebUtils#extractFilenameFromUrlPath
   */
  public static String extractViewNameFromUrlPath(String uri) {
    return WebUtils.extractFilenameFromUrlPath(uri);
  }

  public static String extractExtensionFromUrlPath(String uri) {
    int lastDot = uri.indexOf(".");
    if (lastDot != -1) {
      return uri.substring(lastDot+1);
    }
    return "";
  }

  public static String extractContextPathFromUrlPath(String uri) {
    int midSlash = uri.indexOf("/",1);
    if (midSlash != -1) {
      return uri.substring(1,midSlash);
    }
    return "";
  }

  public static String resoveForwardPath(Forward forwardDef, Map parameters, String urlPath) {
    String forwardPath = null;
    forwardPath = ((String[])ParameterUtil.resolveParam(forwardDef.getUri(), parameters))[0];
    if (forwardPath != null && forwardDef.getUseContext()) {
      String contextPath = ParameterUtil.extractContextPathFromUrlPath(urlPath);
      forwardPath = (contextPath.length()>0 ? "/" + contextPath + "/" : "") + forwardPath; 
    }
    return forwardPath;
  }

  public static Map buildParameterMap(HttpServletRequest request) {
    return buildParameterMap(request, false);
  }
  
  public static Map buildParameterMap(HttpServletRequest request, boolean compCall) {
    Map params = new HashMap();
    HttpSession session = request.getSession();
    Enumeration attributes = session.getAttributeNames();
    // Session has lowest priority
    while(attributes.hasMoreElements()){
      String thisAttribute = (String) attributes.nextElement();
      //if (session.getAttribute(thisAttribute) instanceof String) {
        params.put(thisAttribute, session.getAttribute(thisAttribute));
      //}
    }
    // Parameters next highest
    params.putAll(request.getParameterMap());
    
    // Attributes rule all
    attributes = request.getAttributeNames();
    while(attributes.hasMoreElements()){
      String thisAttribute = (String) attributes.nextElement();
      if (!excludedParameters.contains(thisAttribute)) {
        if (log.isDebugEnabled()) {
          log.debug("Putting " + thisAttribute + " As " + request.getAttribute(thisAttribute));
        }
        params.put(thisAttribute, request.getAttribute(thisAttribute));
      }
    }
    params.put("httpQueryString", request.getQueryString());
    if (compCall && request.getMethod().equals("POST")) {
      StringBuffer qs = new StringBuffer();
      Iterator iter = request.getParameterMap().entrySet().iterator();
      int i = 0;
      while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry)iter.next();
        String key = (String)entry.getKey();
        String[] value = (String[])entry.getValue();
        for (int j = 0; j < value.length; j++) {
          if (i > 0) qs.append("&");
          qs.append(key).append("=").append(value[j]);
          i++;
        }
      }
      params.put("httpQueryString", qs.toString());
    }
    return params;
  }
  
  public static void mapParameters(String callingContext, 
      Parameter[] paramMap, 
      Map inParams, 
      Map outParams, 
      String scopeId) throws ParameterException {
    mapParameters(callingContext, paramMap, inParams, outParams, scopeId, null);
  }
  
  public static void mapParameters(String callingContext, 
      Parameter[] paramMap, 
      Map inParams, 
      Map outParams, 
      String scopeId, 
      ArrayList objectList) throws ParameterException {
    JXPathContext context = JXPathContext.newContext(inParams);
    for(int j = 0; j < paramMap.length; j++){
      Parameter thisParam = paramMap[j];
      Object value = null;
      String thisPath = null;
      String thisName = null;
      try {
        if (thisParam.getScope() != null && 
            !thisParam.getScope().equalsIgnoreCase("all") && 
            !thisParam.getScope().equalsIgnoreCase(scopeId) ) {
          continue;
        }
        if(!thisParam.getOverwriteExisting() && inParams.get(thisParam.getName()) != null) {
          continue;
        }
        thisName = resolveParam(thisParam.getName(), inParams)[0];
        thisPath = resolveParam(thisParam.getPath(), inParams)[0];
        boolean condition = true;
        if (thisParam.getCondition() != null) {
          Object condObj = context.getValue(thisParam.getCondition());
          if (log.isDebugEnabled()) {
            log.debug("Condition Object: " + condObj);
          }
          if (condObj != null && condObj instanceof Boolean) {
            condition = (Boolean)condObj;
          }
        }
        if (condition) {
          if (thisParam.getIsStatic()) {
            value = thisPath;
          } else if (thisParam.getIsObject()) {
            if((objectList == null) || (objectList != null && thisParam.getObjectIndex() >= objectList.size())){
              continue;
            }
            JXPathContext objContext = JXPathContext.newContext(objectList.get(thisParam.getObjectIndex()));
            if (thisParam.getIsList()) {
              Iterator iter = objContext.iterate(thisPath);
              value = new ArrayList();
              while (iter.hasNext()) {
                ((ArrayList)value).add(iter.next());
              }
              if (((ArrayList)value).size() == 0 && thisParam.getDefault() != null) {
                ((ArrayList)value).add(thisParam.getDefault());
              }
            } else {
              value = objContext.getValue(thisPath);
            }
          } else if (thisParam.getIsList()) {
            Object newList = inParams.get(thisName);
            if (newList == null)
              newList = outParams.get(thisName);
            if (newList != null && !(newList instanceof ArrayList)) {
              newList = new ArrayList();
              ((ArrayList)newList).add(value);
            }
            if (newList == null)
              newList = new ArrayList();
  
            value = context.getValue(thisPath);
            if(value != null && value.getClass().isArray()){
              Object[] valueArray = (Object[])value;
              if (valueArray.length > 1) {
                for (int i = 0; i < valueArray.length; i++) {
                  if (valueArray[i] != null && ((String)valueArray[i]).length() > 0)
                    ((ArrayList)newList).add(valueArray[i]);
                }
                value = null;
              } else {
                value = valueArray[0];
              }
            }
            if (value != null && !"".equals(value))
              ((ArrayList)newList).add(value);
            
            value = newList;
          } else {
            value = context.getValue(thisPath);
            if(value != null && value.getClass().isArray()){
              Object[] valueArray = (Object[])value;
              if (valueArray.length > 1) {
                value = valueArray;
              } else {
                value = valueArray[0];
              }
            } else if (value == null && thisParam.getSessionPath() != null) {
              value = context.getValue(thisParam.getSessionPath());
            }
          }
          if (value != null && value.getClass().isArray() && thisParam.getIsList()) {
            outParams.put(thisName, value);
          } else if (value != null && value.getClass().isArray()) {
            outParams.put(thisName, ((String[])value)[0]);
          } else if (value != null && value instanceof ArrayList && ((ArrayList)value).size()>0) {
            outParams.put(thisName, value);
          } else if (value != null && !(value instanceof ArrayList) && String.valueOf(value).length() > 0) {
            outParams.put(thisName, String.valueOf(value));
          } else if (thisParam.getDefault() != null) {
            String [] defVal = resolveParam(thisParam.getDefault(), inParams);
            if (defVal != null) {
              outParams.put(thisName, defVal[0]);
            }
          } else if (!thisParam.getIgnoreNull()) {
            throw new ParameterException(callingContext, thisName, thisPath);
          } else if (log.isDebugEnabled()){
            log.debug("Param " + thisName + " evaluated to null");
          }
        }
      } catch (Exception e) {
        log.error("mapParameters - exception [name:" + thisName + " path:" + thisPath + " value:" + value + "]");
        throw new ParameterException(callingContext, thisName, thisPath);
      }
    }
  }

  public static void mapOutputParameters(Parameter[] paramMap, Map paramsIn, String scopeId, ArrayList objects) {
    for(int j = 0; j < paramMap.length; j++){
      Parameter thisParam = paramMap[j];
      if (thisParam.getScope() != null && 
          !thisParam.getScope().equalsIgnoreCase("all") && 
          !thisParam.getScope().equalsIgnoreCase(scopeId) ) {
        continue;
      }
      if(!thisParam.getOverwriteExisting() && paramsIn.get(thisParam.getName()) != null) {
        continue;
      }
      if(thisParam.getObjectIndex() >= objects.size()){
        continue;
      }
      JXPathContext context = null;
      Object value = null;
      String paramName = resolveParam(thisParam.getName(), paramsIn)[0];
      try {
        String thisPath = resolveParam(thisParam.getPath(), paramsIn)[0];
        if(thisParam.getIsStatic()){
          value = thisPath;
        } else {
          if (thisParam.getIsList()) {
            value = new ArrayList();
            if (thisParam.getObjectIndex() == -1) {
              for (int i = 0; i < objects.size(); i++) {
                context = JXPathContext.newContext(objects.get(i));
                ((ArrayList)value).add(context.getValue(thisPath));
              }
            } else {
              context = JXPathContext.newContext(objects.get(thisParam.getObjectIndex()));
              Iterator iter = context.iterate(thisPath);
              while (iter.hasNext()) {
                ((ArrayList)value).add(iter.next());
              }
            }
            if (((ArrayList)value).size() == 0) {
              if (thisParam.getDefault() != null) {
                try {
                  ((ArrayList)value).add(Integer.parseInt(thisParam.getDefault()));
                } catch (NumberFormatException nfe) {
                  ((ArrayList)value).add(thisParam.getDefault());
                }
              } else {
                value = null;
              }
            }
          } else {
            context = JXPathContext.newContext(objects.get(thisParam.getObjectIndex()));
            value = context.getValue(thisPath);
          }
        }
        if(value != null 
            && List.class.isAssignableFrom(value.getClass()) 
            && ((List)value).size() == 0
            && thisParam.getDefault() != null){
          ((List)value).add(thisPath);
        }
        paramsIn.put(paramName, value);
      } catch (JXPathException e) {
        if (thisParam.getDefault() != null) {
          String[] def = resolveParam(thisParam.getDefault(), paramsIn);
          if (def != null && def.length > 0) {
            paramsIn.put(paramName, def[0]);
          }
        } else if (!thisParam.getIgnoreNull()) {
          log.error("JXPathException for parameter " + paramName + " in scope " + scopeId);
          // TODO This should be a BaseException
          throw e;
        }
      }
    }
  }
  
  public static void mapDoItInputParameters(Parameter[] paramMap, Map paramsIn, Map paramsOut, boolean useJXPathContext) 
  {
    JXPathContext context = null;
    if(useJXPathContext)
      context = JXPathContext.newContext(paramsIn);
    
    for(int j = 0; j < paramMap.length; j++){
      Parameter thisParam = paramMap[j];
      Object value = null;
      if(thisParam.getIsStatic()) {
        String [] valueAry = new String[1];
        valueAry[0] = resolveParam(thisParam.getPath(), paramsIn)[0];
        value = valueAry;
      } else {
        value = context.getValue(resolveParam(thisParam.getPath(), paramsIn)[0]);
        if (value != null && value.getClass().isArray() && ((Object[])value).length == 1) {
          value = ((Object[])value)[0];
        } else if (value == null && thisParam.getDefault() != null) {
          value = thisParam.getDefault();
        }
      }
      paramsOut.put(resolveParam(thisParam.getName(), paramsIn)[0], value);
    }
  }
  
  public static String[] resolveParam(Object input, Map params) {
    return resolveParam(input, params, null);
  }
  
  public static String[] resolveParam(Object input, Map params, Object defaultValue) {
    String[] output;
    if (input != null && input.getClass().isArray()) {
      output = (String[])input;
    } else {
      output = new String[] {(String)input};
    }
    if (input != null && input instanceof String && !"".equals(input)) {
      char ind = ((String)input).charAt(0);
      Object value;
      switch (ind) {
      case '$':
        value = params.get(((String)input).substring(1));
        if (value == null) {
          if (defaultValue != null) {
            value = defaultValue;
          } else {
            log.warn("Input variable with name " + input + " resolved to null");
            return null;
          }
        }
        if (value.getClass().isArray()) {
          output = ((String[])value);
        } else {
          output = new String[1];
          output[0] = (String)value;
        }
        if (log.isDebugEnabled()) {
          log.debug("Input variable with name " + input + " resolved to " + output[0]);
        }
        break;
      case '#':
        value = envParameters.get(((String)input).substring(1));
        if (value != null) {
          if (value.getClass().isArray()) {
            output = ((String[])value);
          } else {
            output = new String[1];
            output[0] = (String)value;
          }
        }
        break;
      case '%':
        if (((String)input).equalsIgnoreCase("%now")) {
          output = new String[1];
          output[0] = String.valueOf(new Date().getTime());
        }
      }
    } else if (defaultValue != null) {
      output = new String[1];
      output[0] = (String)defaultValue;
    }
    return output;
  }

  public static void mapScriptParams(Map params, Map paramsIn) {
    Iterator iter = params.keySet().iterator();
    while (iter.hasNext()) {
      Object key = iter.next();
      paramsIn.put(key, params.get(key));
    }
  }
}
