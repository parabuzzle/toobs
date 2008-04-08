package org.toobs.framework.servlet.filters.compression;

import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Enumeration;
import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class CompressionFilter extends HttpServlet implements Filter {

  private static final long serialVersionUID = 1L;

  /** The logger instance */
  private static Log log = LogFactory.getLog(CompressionFilter.class);

  /** The Filter config */
  private FilterConfig config = null;

  /** Filter enabled */
  private boolean filterEnabled = false;
  private boolean filterWhitespace = false;
  private boolean filterCompress = false;
  private boolean smartCompress = false;

  /** Filter mode */
  private String compressionType = null;
  private int bufferSize;


  private ArrayList excludedHosts = null;

  /**
   * Initializes the filter's configuratuon
   *
   * @param config the FilterConfig object
   *
   * @throws ServletException
   */
  public void init(FilterConfig config) throws ServletException {
    this.config = config;
    this.config.getServletContext().log("CompressionFilter - init()");
    URL resourcesURL = null;

    try {
      resourcesURL = config.getServletContext().getResource("/WEB-INF/classes/compression-filter-config.xml");

      if (resourcesURL != null) {
        if (log.isDebugEnabled()) {
          log.debug("Loading compression-filter-config");
        }
        CompressionFilterDAO dao = new CompressionFilterDAO(resourcesURL);

        filterCompress = dao.isCompressionEnabled();
        filterWhitespace = dao.isWhitespaceEnabled();

        filterEnabled = (filterCompress || filterWhitespace);
        if (log.isDebugEnabled()) {
          log.debug("filter enabled: " + filterEnabled);
        }
        compressionType = dao.getCompressionType();
        if (log.isDebugEnabled()) {
          log.debug("filter compression type: " + compressionType);
        }
        bufferSize = dao.getBufferSize();
        if (filterCompress) {
          smartCompress = dao.isSmartCompress();
          if (smartCompress) {
            excludedHosts = dao.getHostAddress();
            if (excludedHosts.size() < 1) {
              smartCompress = false;
            }
          }
        }
        if (log.isDebugEnabled()) {
          log.debug("smart compress: " + smartCompress);
          log.debug("host address  : " + excludedHosts);
        }
      }
    } catch (java.net.MalformedURLException ex) {
      this.config.getServletContext().log("CompressionFilter - Exception during init: " + ex.getMessage(), ex);
      throw new ServletException(ex);
    }
  }

  /**
   * Destroy this filter instance
   */
  public void destroy() {
    this.config = null;
  }

  /**
   * The filter intercepts the querystring for this request
   * and will reject the request if any of the filtered characters are found
   *
   * @param request The servlet request object
   * @param response The servlet response object
   * @param chain The servlet chain
   *
   * @throws ServletException
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
    if (log.isDebugEnabled()) {
      log.debug("Begin [doFilter]");
    }

    request.setCharacterEncoding("UTF-8");

    String query = ((HttpServletRequest)request).getQueryString();
    boolean noFilter = (query != null && query.indexOf("DisableWhiteSpace") != -1);
    boolean compressible = false;
    //long startTime = 0L;
    if (!filterEnabled || noFilter) { // || ((HttpServletRequest)request).getRequestURI().endsWith("application.do")
      chain.doFilter(request, response);
    } else {
      if (filterCompress) {
        Enumeration headers = ((HttpServletRequest) request).getHeaders("Accept-Encoding");
        while (headers.hasMoreElements())
        {
          String value = (String) headers.nextElement();
          if (value.indexOf(compressionType) != -1)
          {
            compressible = true;
            break;
          }
        }
      }
      if (smartCompress) {
        for (int i = 0; i < excludedHosts.size(); i++) {
          if ((""+((HttpServletRequest) request).getRemoteAddr()).startsWith((String)excludedHosts.get(i))) {
            compressible = false;
          }
        }
      }
      FilterResponseWrapper wrapper = new FilterResponseWrapper((HttpServletResponse)response);
      chain.doFilter(request, wrapper);
      byte[] input = wrapper.getData();
      byte[] output = null;

      OutputStream servletoutputstream = null;
      String content = wrapper.getContentType();
      if (content != null && content.toUpperCase().indexOf("TEXT/") >=0) {
        response.setContentType(content);
        if (filterCompress && compressible) {
          HttpServletResponse httpResponse = (HttpServletResponse) response;
          httpResponse.addHeader("Content-Encoding", compressionType);
          servletoutputstream = new GZIPOutputStream( response.getOutputStream(), bufferSize);
        } else {
          servletoutputstream = response.getOutputStream();
        }
        if (filterWhitespace) {
          output = new byte[input.length];
          int i = 0;
          int o = 0;
          boolean htComment = false;
          boolean inScript = false;
          while (i < input.length) {
            if (input[i] == 13 || input[i] == 9) {  // 13=CR, 9=TAB
              // nothing
            } else if (input[i] == 10) { // 10=LF
              if (inScript) {
                output[o] = input[i];
                o++;
              }
            } else if (input[i] == 32) { // 32=Space
              if (!htComment && !inScript
                  && i + 1 < input.length && input[i + 1] == 32) {
                i++;
                while (i + 1 < input.length && input[i + 1] == 32) {
                  i++;
                }
              } else if (!htComment) {
                output[o] = input[i];
                o++;
              }
            } else if (input[i] == 60) { // 60=<
              if (!inScript && (i + 2 < input.length) && (input[i + 1] == 33) && (input[i + 2] == 45)) {
                i += 3;
                htComment = true;
              } else if (!htComment && i + 2 < input.length
                         && (input[i + 1] == 83 || input[i + 1] == 115)
                         && (input[i + 2] == 67 || input[i + 2] == 99)) {
                inScript = true;
                output[o] = input[i];
                output[o+1] = input[i+1];
                output[o+2] = input[i+2];
                o += 3;
                i += 2;
              } else if (!htComment && i + 3 < input.length
                         && (input[i + 1] == 47)
                         && (input[i + 2] == 83 || input[i + 2] == 115)
                         && (input[i + 3] == 67 || input[i + 3] == 99)) {
                inScript = false;
                output[o] = input[i];
                output[o+1] = input[i+1];
                output[o+2] = input[i+2];
                output[o+3] = input[i+3];
                o += 4;
                i += 3;
              } else if (!htComment) {
                output[o] = input[i];
                o++;
              }
            } else if (input[i] == 62) { // 62 = >
              if (htComment && input[i - 1] == 45 && input[i - 2] == 45) {
                htComment = false;
              } else if (!htComment) {
                output[o] = input[i];
                o++;
              }
            } else if (!htComment) {
                output[o] = input[i];
                o++;
            }
            i++;
          }
          servletoutputstream.write(output, 0, o);
        } else {
          servletoutputstream.write(input);
        }
      } else {
        servletoutputstream = response.getOutputStream();
        servletoutputstream.write(input);
      }

      if(servletoutputstream != null) {
        servletoutputstream.flush();
        servletoutputstream.close();
      }
      input = null;
      output = null;
    }
    if (log.isDebugEnabled()) {
      log.debug("End [doFilter]");
    }
  }
}

