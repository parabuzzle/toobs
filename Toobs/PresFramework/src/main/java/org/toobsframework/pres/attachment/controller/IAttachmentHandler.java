package org.toobsframework.pres.attachment.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.servlet.ModelAndView;
import org.toobsframework.pres.util.ComponentRequestManager;


public interface IAttachmentHandler {

  public abstract void setBeanFactory(BeanFactory beanFactory)
      throws BeansException;

  /**
   * 
   * Retrieves the URL path to use for lookup and delegates to
   * <code>getViewNameForUrlPath</code>.
   * 
   * @throws Exception Exception fetching or rendering component.
   * @see #getViewNameForUrlPath
   * 
   */
  public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
      throws Exception;

  public abstract ComponentRequestManager getComponentRequestManager();

  public abstract void setComponentRequestManager(ComponentRequestManager componentRequestManager);

}