package org.toobs.framework.pres.spring.multipart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;


public interface IMultipartResolver {

  public abstract void resolveMultipart(
      HttpServletRequest request, MultipartHttpServletRequest multiPartReq) throws MultipartException;

}