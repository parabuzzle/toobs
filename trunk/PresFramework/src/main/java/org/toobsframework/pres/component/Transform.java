package org.toobsframework.pres.component;

import org.toobsframework.pres.component.config.Parameters;

/**
 * @author spudney
 */
public class Transform {

  private String transformType;
  private String transformName;
  private Parameters transformParams;

  public Transform() {
  }

  public Transform(org.toobsframework.pres.component.config.Transform transform) {
    this.transformName = transform.getName();
    this.transformType = transform.getType();
    this.transformParams = transform.getParameters();
  }

  public Parameters getTransformParams() {
    return transformParams;
  }

  public void setTransformParams(Parameters transformParams) {
    this.transformParams = transformParams;
  }

  public void setTransformType(String transformType) {
    this.transformType = transformType;
  }

  public String getTransformType() {
    return transformType;
  }

  public String getTransformName() {
    return transformName;
  }

  public void setTransformName(String transformName) {
    this.transformName = transformName;
  }

}