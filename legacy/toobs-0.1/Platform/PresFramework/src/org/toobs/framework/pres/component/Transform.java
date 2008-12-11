package org.toobs.framework.pres.component;

import org.toobs.framework.pres.component.config.ParameterMapping;

/**
 * @author spudney
 */
public class Transform {

  private String transformType;
  private String transformName;
  private ParameterMapping transformParams;

  public Transform() {
  }

  public Transform(org.toobs.framework.pres.component.config.Transform transform) {
    this.transformName = transform.getTransformName();
    this.transformType = transform.getTransformType();
    this.transformParams = transform.getParameterMapping();
  }

  public ParameterMapping getTransformParams() {
    return transformParams;
  }

  public void setTransformParams(ParameterMapping transformParams) {
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