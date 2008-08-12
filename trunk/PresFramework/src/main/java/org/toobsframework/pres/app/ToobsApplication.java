package org.toobsframework.pres.app;

import java.util.Map;

import org.toobsframework.pres.component.Component;
import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.layout.RuntimeLayout;

public class ToobsApplication {

  private String root;
  private String name;
  private String[] xslLocations;
  private Map<String,Component> components;
  private Map<String,RuntimeLayout> layouts;
  private Map<String,DoIt> doits;

  public String getRoot() {
    return root;
  }
  public void setRoot(String root) {
    this.root = root;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String[] getXslLocations() {
    return xslLocations;
  }
  public void setXslLocations(String[] xslLocations) {
    this.xslLocations = xslLocations;
  }
  public Map<String, Component> getComponents() {
    return components;
  }
  public void setComponents(Map<String, Component> components) {
    this.components = components;
  }
  public Map<String, RuntimeLayout> getLayouts() {
    return layouts;
  }
  public void setLayouts(Map<String, RuntimeLayout> layouts) {
    this.layouts = layouts;
  }
  public Map<String, DoIt> getDoits() {
    return doits;
  }
  public void setDoits(Map<String, DoIt> doits) {
    this.doits = doits;
  }

  
}
