package org.toobsframework.pres.chart;

import org.toobsframework.exception.BaseException;

public class ChartNotFoundException extends BaseException {
   
  private static final long serialVersionUID = 1L;
  
  public ChartNotFoundException(String chartId) {
    super("Component with Id " + chartId + " not found in registry");
  }
}
