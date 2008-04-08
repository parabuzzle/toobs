package org.toobs.framework.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.toobs.framework.util.constants.PlatformConstants;


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
  
  public String getClientChangeParam() {
    Object param = this.params.get(PlatformConstants.SESSION_CLIENT_CHANGE);
    if (param != null && param.getClass().isArray()) {
      param = ((Object[])param)[0];
    }
    return (String)param;
  }

  public String[] getClientListParam() {
    return (String[])this.params.get(PlatformConstants.CLIENT_ACCESS_LIST);
  }

  public String getClientParam() {
    return (String)this.params.get(PlatformConstants.SESSION_CLIENT_PARAM);
  }
  
  public Object getClient() {
    return this.params.get(PlatformConstants.SESSION_CLIENT);
  }
  
  public String getPersonId() {
    return (String)this.params.get(PlatformConstants.SESSION_PERSON_PARAM);
  }

  public Object getPerson() {
    return this.params.get("user");
  }

  public String getPersonBusinessId() {
    return (String)this.params.get("userBusinessId");
  }

  public String getBusinessTemplateId() {
    return (String)this.params.get("businessTemplateId");
  }

  public String getSecurityMode() {
    Object secMode = this.params.get(PlatformConstants.DATA_SECURITY_MODE);
    if (secMode != null && secMode.getClass().isArray()) {
      return ((String[])secMode)[0];
    } else {
      return (String)secMode;
    }
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

  public HttpServletResponse getHttpResponse() {
    return httpResponse;
  }
  
  public void setHttpResponse(HttpServletResponse httpResponse) {
    this.httpResponse = httpResponse;
  }
}
