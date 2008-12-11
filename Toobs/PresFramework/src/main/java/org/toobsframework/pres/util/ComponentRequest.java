package org.toobsframework.pres.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.toobsframework.util.BaseRequest;


public class ComponentRequest extends BaseRequest {

  public ComponentRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map params) {
    super(httpRequest, httpResponse, params);
  }
  
}
