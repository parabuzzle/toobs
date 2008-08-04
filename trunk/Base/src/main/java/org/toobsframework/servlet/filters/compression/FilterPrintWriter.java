package org.toobsframework.servlet.filters.compression;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.OutputStream;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class FilterPrintWriter extends PrintWriter {

  //private static Log log = LogFactory.getLog(FilterPrintWriter.class);

  private boolean closed = false;

  public FilterPrintWriter (Writer out) {
      this(out, false);
  }

  public FilterPrintWriter(Writer out,
                     boolean autoFlush) {
      super(out, autoFlush);
  }

  public FilterPrintWriter(OutputStream out) {
      super(out, false);
  }

  public void close() {
    //super.close();
    //closed = true;
  }

  public void reallyClose() {
    super.close();
    closed = true;
  }

  public boolean closed() {
    return closed;
  }

  public void write(String s, int off, int len) {
    super.write(s, off, len);
  }

  public void write(char buf[], int off, int len) {
    super.write(buf, off, len);
  }

  public void print(String s) {
    super.print(s);
  }

}
