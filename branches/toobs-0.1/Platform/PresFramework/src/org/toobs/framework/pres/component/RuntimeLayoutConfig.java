package org.toobs.framework.pres.component;

import java.util.LinkedHashMap;

import org.toobs.framework.pres.component.config.Parameter;
import org.toobs.framework.pres.componentlayout.config.Section;


@SuppressWarnings("unchecked")
public class RuntimeLayoutConfig {
  private LinkedHashMap sections = new LinkedHashMap();
  private LinkedHashMap params = new LinkedHashMap();
  private LinkedHashMap contentParams = new LinkedHashMap();
  private String noAccessLayout;
  
  public LinkedHashMap getParams() {
    return params;
  }
  public Parameter[] getAllParams() {
    Parameter[] allParams = new Parameter[params.size()];
    return (Parameter[])params.values().toArray(allParams);
  }
  public void addParam(Parameter param) {
    addParam(new Parameter[] {param});
  }
  public void addParam(Parameter[] param) {
    for (int i = 0; i < param.length; i++) {
      params.put(param[i].getName(), param[i]);
    }
  }

  public LinkedHashMap getContentParams() {
    return contentParams;
  }
  public Parameter[] getAllContentParams() {
    Parameter[] allParams = new Parameter[contentParams.size()];
    return (Parameter[])contentParams.values().toArray(allParams);
  }
  public void addContentParam(Parameter param) {
    addContentParam(new Parameter[] {param});
  }
  public void addContentParam(Parameter[] param) {
    for (int i = 0; i < param.length; i++) {
      contentParams.put(param[i].getName(), param[i]);
    }
  }

  public LinkedHashMap getSections() {
    return sections;
  }
  public Section[] getAllSections() {
    Section[] allSections = new Section[sections.size()];
    return (Section[])sections.values().toArray(allSections);
  }
  public void addSection(Section section) {
    addSection(new Section[] {section});
  }
  public void addSection(Section[] section) {
    for (int i = 0; i < section.length; i++) {
      sections.put(section[i].getId(), section[i]);
    }
  }
  public String getNoAccessLayout() {
    return noAccessLayout;
  }
  public void setNoAccessLayout(String noAccessLayout) {
    this.noAccessLayout = noAccessLayout;
  }

}
