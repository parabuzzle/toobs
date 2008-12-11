package org.toobsframework.doitref.beans;

import java.io.Serializable;
import java.util.Map;

public class DoItRefBean implements Serializable {

  private static final long serialVersionUID = 4L;
  
  private String doItName;
  private Map paramMap;
  protected int attempts = 0;
  protected String failureCause;

  public int getAttempts() {
    return attempts;
  }
  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }
  public String getFailureCause() {
    return failureCause;
  }
  public void setFailureCause(String failureCause) {
    this.failureCause = failureCause;
  }
  public String getDoItName() {
    return doItName;
  }
  public void setDoItName(String doItName) {
    this.doItName = doItName;
  }
  public Map getParamMap() {
    return paramMap;
  }
  public void setParamMap(Map paramMap) {
    this.paramMap = paramMap;
  }
  
  public DoItRefBean() {}
  
  public DoItRefBean(String doItName, Map paramMap) {
    super();
    this.doItName = doItName;
    this.paramMap = paramMap;
  }
  
}
