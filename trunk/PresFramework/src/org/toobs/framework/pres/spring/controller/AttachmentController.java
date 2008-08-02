package org.toobs.framework.pres.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@SuppressWarnings("unchecked")
public class AttachmentController extends AbstractController {

  private static Log log = LogFactory.getLog(AttachmentController.class);
  
  private IAttachmentHandler attachmentHandler;
  
  /**
   * 
   * Retrieves the URL path to use for lookup and delegates to
   * <code>getViewNameForUrlPath</code>.
   * 
   * @throws Exception Exception fetching or rendering component.
   * @see #getViewNameForUrlPath
   * 
   */
  public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    return attachmentHandler.handleRequestInternal(request, response);
  }

  public IAttachmentHandler getAttachmentHandler() {
    return attachmentHandler;
  }

  public void setAttachmentHandler(IAttachmentHandler attachmentHandler) {
    this.attachmentHandler = attachmentHandler;
  }

}
