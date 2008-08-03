package org.toobsframework.pres.spring.multipart;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.ParameterUtil;

@SuppressWarnings("unchecked")
public class XslMultipartResolver extends CommonsMultipartResolver
implements MultipartResolver {

  private static final String DEFAULT_ENCODING = "utf8"; 

  private static Log log = LogFactory.getLog(XslMultipartResolver.class);

  private ComponentRequestManager requestManager;
  private IMultipartResolver multipartResolver;

  public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request)
      throws MultipartException {

    //MultipartHttpServletRequest multiPartReq = super.resolveMultipart(request);

    String encoding = determineEncoding(request);
    FileUpload fileUpload = prepareFileUpload(encoding);
    try {
      List fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
      MultipartParsingResult parsingResult = parseFileItems(fileItems, encoding, 0);

      Map params = ParameterUtil.buildParameterMap(request);
      requestManager.set(request, null, params);

      MultipartHttpServletRequest multiPartReq = new DefaultMultipartHttpServletRequest(
          request, parsingResult.getMultipartFiles(), parsingResult.getMultipartParameters()); 
      multipartResolver.resolveMultipart(request, multiPartReq);

      return multiPartReq;
    }
    catch (FileUploadBase.SizeLimitExceededException ex) {
      throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
    }
    catch (FileUploadException ex) {
      throw new MultipartException("Could not parse multipart servlet request", ex);
    } finally {
      requestManager.unset();
    }
    
  }

  protected MultipartParsingResult parseFileItems(List fileItems, String encoding, int IHateYouJuergenHoeller) {
    Map multipartFiles = new LinkedHashMap();
    Map multipartParameters = new LinkedHashMap();

    // Extract multipart files and multipart parameters.
    for (Iterator it = fileItems.iterator(); it.hasNext();) {
      FileItem fileItem = (FileItem) it.next();
      if (fileItem.isFormField()) {
        String value = null;
        if (encoding != null) {
          try {
            value = fileItem.getString(encoding);
          }
          catch (UnsupportedEncodingException ex) {
            if (logger.isWarnEnabled()) {
              logger.warn("Could not decode multipart item '" + fileItem.getFieldName() +
                  "' with encoding '" + encoding + "': using platform default");
            }
            value = fileItem.getString();
          }
        }
        else {
          value = fileItem.getString();
        }
        String[] curParam = (String[]) multipartParameters.get(fileItem.getFieldName());
        if (curParam == null) {
          // simple form field
          multipartParameters.put(fileItem.getFieldName(), new String[] { value });
        }
        else {
          // array of simple form fields
          String[] newParam = StringUtils.addStringToArray(curParam, value);
          multipartParameters.put(fileItem.getFieldName(), newParam);
        }
      }
      else {
        // multipart file field
        CommonsMultipartFile file = new CommonsMultipartFile(fileItem);
        multipartFiles.put(file.getName(), file);
        if (logger.isDebugEnabled()) {
          logger.debug("Found multipart file [" + file.getName() + "] of size " + file.getSize() +
              " bytes with original filename [" + file.getOriginalFilename() + "], stored " +
              file.getStorageDescription());
        }
      }
    }
    return new MultipartParsingResult(multipartFiles, multipartParameters);
  }

  protected String determineEncoding(HttpServletRequest request) {
    String enc = request.getCharacterEncoding();
    if (enc == null) {
      enc = DEFAULT_ENCODING;
    }
    return enc;
  }

  protected static class MultipartParsingResult {

    private final Map multipartFiles;

    private final Map multipartParameters;

    /**
     * Create a new MultipartParsingResult.
     * @param multipartFiles Map of field name to MultipartFile instance
     * @param multipartParameters Map of field name to form field String value
     */
    public MultipartParsingResult(Map multipartFiles, Map multipartParameters) {
      this.multipartFiles = multipartFiles;
      this.multipartParameters = multipartParameters;
    }

    /**
     * Return the multipart files as Map of field name to MultipartFile instance.
     */
    public Map getMultipartFiles() {
      return multipartFiles;
    }

    /**
     * Return the multipart parameters as Map of field name to form field String value.
     */
    public Map getMultipartParameters() {
      return multipartParameters;
    }
  }

  public IMultipartResolver getMultipartResolver() {
    return multipartResolver;
  }

  public void setMultipartResolver(IMultipartResolver multipartResolver) {
    this.multipartResolver = multipartResolver;
  }

  public ComponentRequestManager getRequestManager() {
    return requestManager;
  }

  public void setRequestManager(ComponentRequestManager requestManager) {
    this.requestManager = requestManager;
  }

}
