package org.toobs.framework.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.toobs.framework.scheduler.AppScheduler;


public class ContextListener extends org.springframework.web.context.ContextLoaderListener {

  private static Log log = LogFactory.getLog(ContextListener.class);
  
  private ContextLoader contextLoader;
  private ServletContext context = null;
  private WebApplicationContext webApplicationContext;
  
  private AppScheduler scheduler = null;
  
  public void contextDestroyed(ServletContextEvent event) {
    if (scheduler != null) {
      scheduler.destroy();
    }
    //super.contextDestroyed(event);
    if (this.contextLoader != null) {
      this.contextLoader.closeWebApplicationContext(event.getServletContext());
    }
    context = null;
  }

  public void contextInitialized(ServletContextEvent event) {
    //super.contextInitialized(event);
    this.contextLoader = createContextLoader();
    webApplicationContext = this.contextLoader.initWebApplicationContext(event.getServletContext());
    ContextHelper.getInstance().setWebApplicationContext(webApplicationContext);
    
    context = event.getServletContext();
    scheduler = (AppScheduler)webApplicationContext.getBean("appScheduler");
    //scheduler = new AppScheduler();
    scheduler.init();
    log.info("Context " + context.getServletContextName() + " initialized");
  }

}
