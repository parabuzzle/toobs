package org.toobs.framework.servlet;

import org.springframework.web.context.WebApplicationContext;

public class ContextHelper {

  private static ContextHelper instance;
  private WebApplicationContext webApplicationContext;
  
  private ContextHelper() {
  }
  
  public static ContextHelper getInstance() {
    if (instance == null) {
      instance = new ContextHelper();
    }
    return instance;
  }

  public WebApplicationContext getWebApplicationContext() {
    return webApplicationContext;
  }

  public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
    this.webApplicationContext = webApplicationContext;
  }
  
}
