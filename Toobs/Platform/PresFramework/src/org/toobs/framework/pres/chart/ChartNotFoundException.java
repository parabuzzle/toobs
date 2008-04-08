package org.toobs.framework.pres.chart;

import org.toobs.framework.exception.BaseException;

public class ChartNotFoundException extends BaseException {
   
  private static final long serialVersionUID = 1L;
  
  public ChartNotFoundException(String chartId) {
    super("Component with Id " + chartId + " not found in registry");
  }
}
