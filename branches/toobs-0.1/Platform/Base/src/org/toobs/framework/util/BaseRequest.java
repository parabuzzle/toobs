package org.toobs.framework.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseRequest implements IRequest {
  private HttpServletRequest httpRequest;
  HttpServletResponse httpResponse;
  private Map params;
  
  public Map getParams() {
    return params;
  }

  public void setParams(Map params) {
    this.params = params;
  }
  
  public BaseRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map params) {
    this.httpRequest = httpRequest;
    this.httpResponse = httpResponse;
    this.params = params;
  }
  
  public HttpServletRequest getHttpRequest() {
    return httpRequest;
  }

  public void setHttpRequest(HttpServletRequest httpRequest) {
    this.httpRequest = httpRequest;
  }
  
  public Boolean getSingleBooleanParam(String paramName) {
    Object param = this.params.get(paramName);
    if (param != null && param.getClass().isArray()) {
      param = ((Object[])param)[0];
    }
    if (param == null || param.equals("")) {
      return null;
    }
    boolean paramVal = false;
    if (param.equals("true")) {
      paramVal = true;
    }
    return new Boolean(paramVal);
  }

  public Object getParam(String paramName) {
    return this.params.get(paramName);
  }

  public String getString(String paramName) {
    Object param = this.params.get(paramName);
    if (param != null && param.getClass().isArray()) {
      param = ((Object[])param)[0];
    }
    return (String)param;
  }

  public String[] getStringArray(String paramName) {
    Object param = this.params.get(paramName);
    if (param != null && param.getClass().isArray()) {
      return (String[])param;
    } else {
      return new String[] {(String)param};
    }
  }

  public HttpServletResponse getHttpResponse() {
    return httpResponse;
  }
  
  public void setHttpResponse(HttpServletResponse httpResponse) {
    this.httpResponse = httpResponse;
  }
}
