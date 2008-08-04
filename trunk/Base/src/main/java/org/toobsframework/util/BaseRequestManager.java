package org.toobsframework.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

@SuppressWarnings("unchecked")
public class BaseRequestManager {

  //private static Log log = LogFactory.getLog(BaseRequestManager.class);

  protected static ThreadLocal requestHolder = new ThreadLocal();
  
  public void set(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map params) {
    BaseRequest request = new BaseRequest(httpRequest, httpResponse, params);
    requestHolder.set(request);
  }
  
  public IRequest get() {
    IRequest request = (BaseRequest)requestHolder.get();
    return request;
  }

  public void unset() {
    requestHolder.set(null);
  }
}
