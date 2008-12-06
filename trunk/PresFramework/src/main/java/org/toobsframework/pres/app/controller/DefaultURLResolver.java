package org.toobsframework.pres.app.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.app.AppReader;

public class DefaultURLResolver implements IURLResolver {

  private static Log log = LogFactory.getLog(DefaultURLResolver.class);

  private String compPrefix = "comp";
  
  public IAppView resolve(AppReader appReader, String url, String method) {

    return null;
  }


  private BaseAppView getBaseAppView(AppReader appReader, String urlPath) {
    String[] splitUrl = urlPath.split("/");
    if (log.isDebugEnabled()) {
      for (int i = 0; i < splitUrl.length; i++) {
        log.debug("Url part " + i + ": " + splitUrl[i]);
      }
    }
    BaseAppView view = null;
    
    if (splitUrl.length <= 1) {
      return new BaseAppView("/", DEFAULT_VIEW);
    }
    if (appReader.containsApp("/" + splitUrl[1])) {
      String appName = splitUrl[1];
      if (splitUrl.length == 2) {
        return new BaseAppView(appName, DEFAULT_VIEW);
      } else {
        
      }
    }
    
    /*
    if (splitUrl[1].equals(compPrefix)) {
      if (splitUrl.length >= 5) {
        return new BaseAppView("/", true, splitUrl[2], splitUrl[3], splitUrl[4]);
      } else if (splitUrl.length == 4) {
        return new BaseAppView("/", true, splitUrl[2], splitUrl[3], splitUrl[2]);
      } else {
        return new BaseAppView("/", true, null, null, splitUrl[2]);
      }
    }
    if (appManager.containsApp("/" + splitUrl[1])) {
      if (splitUrl[1].equals(compPrefix)) {
        if (splitUrl.length >= 6) {
          return new BaseAppView("/" + splitUrl[1], true, splitUrl[3], splitUrl[4], splitUrl[5]);
        } else if (splitUrl.length == 4) {
          return new BaseAppView("/" + splitUrl[1], true, splitUrl[3], splitUrl[4], splitUrl[3]);
        } else {
          return new BaseAppView("/" + splitUrl[1], true, null, null, splitUrl[3]);
        }
      } else {
        if (splitUrl.length >= 5) {
          return new BaseAppView("/" + splitUrl[1], false, splitUrl[2], splitUrl[3], splitUrl[4]);
        } else if (splitUrl.length == 4) {
          return new BaseAppView("/" + splitUrl[1], false, splitUrl[2], splitUrl[3], splitUrl[2]);
        } else if (splitUrl.length == 3) {
          return new BaseAppView("/" + splitUrl[1], false, null, null, splitUrl[2]);
        } else {
          return new BaseAppView("/" + splitUrl[1], false, null, null, DEFAULT_VIEW);
        }
      }
    } else {
      if (splitUrl.length >= 4) {
        return new BaseAppView("/", true, splitUrl[1], splitUrl[2], splitUrl[3]);
      } else if (splitUrl.length == 3) {
        return new BaseAppView("/", true, splitUrl[1], splitUrl[2], splitUrl[1]);
      } else {
        return new BaseAppView("/", true, null, null, splitUrl[1]);
      }
    }
    */ 
    return view;
  }

  public String getCompPrefix() {
    return compPrefix;
  }

  public void setCompPrefix(String compPrefix) {
    this.compPrefix = compPrefix;
  }

}
