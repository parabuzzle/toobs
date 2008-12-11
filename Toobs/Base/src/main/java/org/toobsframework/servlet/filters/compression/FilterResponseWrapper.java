package org.toobsframework.servlet.filters.compression;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FilterResponseWrapper extends HttpServletResponseWrapper {
  private ByteArrayOutputStream output;
  private FilterPrintWriter writer;
  private HttpServletResponse origResponse;
  private int contentLength;
  private boolean encodingSpecified = false;

  public FilterResponseWrapper(HttpServletResponse httpservletresponse) {
    super(httpservletresponse);
    output = null;
    writer = null;
    output = new ByteArrayOutputStream();
    origResponse = httpservletresponse;
  }

  public byte[] getData() {
    try {
      finishResponse();
    }
    catch(Exception exception) { }
    return output.toByteArray();
  }

  public ServletOutputStream getOutputStream() {
    return new FilterOutputStream(output);
  }

  public ServletOutputStream createOutputStream() throws IOException {
    return new FilterOutputStream(output);
  }

  public PrintWriter getWriter() throws IOException {
    if(writer != null) {
      return writer;
    } else {
      ServletOutputStream servletoutputstream = getOutputStream();
      writer = new FilterPrintWriter(new OutputStreamWriter(servletoutputstream, origResponse.getCharacterEncoding()), true);
      return writer;
    }
  }

  public void finishResponse() {
    try {
      if (writer != null) {
        writer.reallyClose();
      } else if (output != null) {
        output.flush();
        output.close();
      }
    }
    catch (IOException ioexception) {}
  }

  public void flushBuffer() throws IOException {
    output.flush();
  }

  public void setContentLength(int i) {
    contentLength = i;
    super.setContentLength(i);
  }

  public int getContentLength() {
    return contentLength;
  }

  public void setContentType( String type )
  {
     String explicitType = type;

     // If a specific encoding has not already been set by the app,
     // let's see if this is a call to specify it.  If the content
     // type doesn't explicitly set an encoding, make it UTF-8.
     if (!encodingSpecified)
     {
        String lowerType = type.toLowerCase();

        // See if this is a call to explicitly set the character encoding.
        if (lowerType.indexOf( "charset" ) < 0)
        {
           // If no character encoding is specified, we still need to
           // ensure the app is specifying text content.
           if (lowerType.startsWith( "text/" ))
           {
              // App is sending a text response, but no encoding
              // is specified, so we'll force it to UTF-8.
              explicitType = type + "; charset=UTF-8";
           }
        }
        else
        {
           // App picked a specific encoding, so let's make
           // sure we don't override it.
           encodingSpecified = true;
        }
     }

     // Delegate to supertype to record encoding.
     super.setContentType( explicitType );
  }

}
