package org.toobs.framework.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRequest {

  public abstract Map getParams();

  public abstract void setParams(Map params);

  public abstract HttpServletRequest getHttpRequest();

  public abstract void setHttpRequest(HttpServletRequest httpRequest);

  public abstract HttpServletResponse getHttpResponse();
  
  public abstract void setHttpResponse(HttpServletResponse httpResponse);

  public abstract String getClientChangeParam();

  public abstract String[] getClientListParam();

  public abstract String getClientParam();

  public abstract Object getClient();

  public abstract String getPersonId();
  
  public abstract Object getPerson();
  
  public abstract String getPersonBusinessId();

  public abstract String getBusinessTemplateId();
  
  public abstract String getSecurityMode();

  public abstract Boolean getSingleBooleanParam(String paramName);

}