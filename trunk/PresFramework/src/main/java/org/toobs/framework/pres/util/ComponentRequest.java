package org.toobs.framework.pres.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.toobs.framework.util.BaseRequest;


public class ComponentRequest extends BaseRequest {

  public ComponentRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map params) {
    super(httpRequest, httpResponse, params);
  }
  
}
