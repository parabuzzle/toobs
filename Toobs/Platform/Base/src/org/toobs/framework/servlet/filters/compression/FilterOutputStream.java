package org.toobs.framework.servlet.filters.compression;

import java.io.*;
import javax.servlet.ServletOutputStream;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class FilterOutputStream extends ServletOutputStream
{

  //private static Log log = LogFactory.getLog(FilterOutputStream.class);

  private DataOutputStream stream;

  public FilterOutputStream(OutputStream outputstream) {
    stream = new DataOutputStream(outputstream);
  }

  public void write(int i) throws IOException {
    stream.write(i);
  }

  public void write(byte buf[]) throws IOException {
    stream.write(buf);
  }

  public void write(byte buf[], int i, int j) throws IOException {
    stream.write(buf, i, j);
  }

}
