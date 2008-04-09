package org.toobs.framework.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Configuration {
  
  public static final String MAIN_PROP_FILE = "toobs.properties";
  public static final String DEPLOY_PROP_FILE = "deploy.properties";
  public static final String DEFAULT_GLOBAL_CONTEXT = "/toobs"; 

  public static final String USE_TRANSLETS_PROPERTY = "xml.usetranslets";
  public static final String USE_CHAIN_PROPERTY = "xml.usechain";
  public static final String RELOAD_COMPONENTS_PROPERTY = "reload.components";
  public static final String RELOAD_DOITS_PROPERTY = "reload.doits";
  public static final String DEBUG_COMPONENTS_PROPERTY = "debug.components";

  public static final String UPLOAD_DIR_PROPERTY = "toobs.upload.dir";

  public static final String LAYOUT_EXT_PROPERTY = "toobs.layout.ext";
  public static final String COMPONENT_EXT_PROPERTY = "toobs.component.ext";
  public static final String CHART_EXT_PROPERTY = "toobs.chart.ext";

  public static final String LAYOUT_EXT_DEFAULT = ".html";
  public static final String COMPONENT_EXT_DEFAULT = ".comp";
  public static final String CHART_EXT_DEFAULT = ".chart";

  public static final String MAIN_HOST_PROPERTY = "main.host";
  public static final String MAIN_CONTEXT_PROPERTY = "main.context";

  public static final String HOST_DEFAULT = "localhost:8080";

  public static final String IMAGES_HOSTS[] = {"images1.host", "images2.host", "images3.host", "images4.host"};
  
  public static final String STATIC1_HOST_PROPERTY = "static1.host";
  public static final String STATIC2_HOST_PROPERTY = "static2.host";
  public static final String STATIC3_HOST_PROPERTY = "static3.host";
  public static final String STATIC4_HOST_PROPERTY = "static4.host";

  public static final String INDEX_PATH_PROPERTY = "fulltext.index.basepath";

  private Log log = LogFactory.getLog(Configuration.class);

  private static Configuration instance = null;
  
  private Properties properties = null;
  private Properties deployProperties = null;
  private Configuration() {
    properties = new Properties();
    deployProperties = new Properties();

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    
    String userProperties = System.getProperty("user.home") + "/" + MAIN_PROP_FILE;
    
    URL propUrl;
    File propFile = new File(userProperties);
    try {
      if (propFile.exists()) {
        propUrl = propFile.toURL();
      } else {
        propUrl = classLoader.getResource(MAIN_PROP_FILE);
      }
      if (propUrl != null) {
        properties.load(propUrl.openStream());
      }
    } catch (MalformedURLException e) {
      log.warn("Property file " + userProperties + " could not be loaded due to " + e.getMessage(), e);
    } catch (IOException e) {
      log.warn("Property file " + userProperties + " could not be loaded due to " + e.getMessage(), e);
    }

    propUrl = classLoader.getResource(DEPLOY_PROP_FILE);
    propFile = new File(propUrl.getFile());
    if (propFile.exists()) {      
      try {
        deployProperties.load(propFile.toURL().openStream());
      } catch (MalformedURLException e) {
        log.warn("Property file " + DEPLOY_PROP_FILE + " could not be loaded due to " + e.getMessage(), e);
      } catch (IOException e) {
        log.warn("Property file " + DEPLOY_PROP_FILE + " could not be loaded due to " + e.getMessage(), e);
      }
    }

  }
  
  public static Configuration getInstance() {
    if (instance == null) {
      instance = new Configuration();
    }
    return instance;
  }

  public String getProperty(String key) {
    return properties.getProperty(key, null);
  }
  
  public String getProperty(String key, String defaultValue) {
    if (properties.containsKey(key)) {
      return properties.getProperty(key);
    } else {
      return defaultValue;
    }
  }
  
  public String getMainContext() {
    return deployProperties.getProperty(MAIN_CONTEXT_PROPERTY, DEFAULT_GLOBAL_CONTEXT);
  }

  public String getMainHost() {
    String prop = getProperty(MAIN_HOST_PROPERTY);
    if (prop != null && prop.length() > 0) {
      return prop;
    } else {
      return HOST_DEFAULT;
    }
  }
  
  public String getHostProperty(String hostProperty) {
    String prop = getProperty(hostProperty);
    if (prop != null && prop.length() > 0) {
      return prop;
    } else {
      return getMainHost();
    }
  }

  public String getHttpHostProperty(String hostProperty) {
    String prop = getProperty(hostProperty);
    if (prop != null && prop.length() > 0) {
      return "http://" + prop;
    } else {
      return "";
    }
  }

  public boolean getUseTranslets() {
    String prop = getProperty(USE_TRANSLETS_PROPERTY);
    if (prop != null && prop.equalsIgnoreCase("true")) {
      return true;
    } else {
      return false;
    }
  }

  public boolean getUseChain() {
    String prop = getProperty(USE_CHAIN_PROPERTY);
    if (prop != null && prop.equalsIgnoreCase("true")) {
      return true;
    } else {
      return false;
    }
  }

  public boolean getReloadComponents() {
    String prop = getProperty(RELOAD_COMPONENTS_PROPERTY);
    if ((prop != null && prop.equalsIgnoreCase("true")) || prop == null) {
      return true;
    } else {
      return false;
    }
  }

  public boolean getDebugComponents() {
    String prop = getProperty(DEBUG_COMPONENTS_PROPERTY);
    if ((prop != null && prop.equalsIgnoreCase("false")) || prop == null) {
      return false;
    } else {
      return true;
    }
  }

  public boolean getReloadDoits() {
    String prop = getProperty(RELOAD_DOITS_PROPERTY);
    if ((prop != null && prop.equalsIgnoreCase("true")) || prop == null) {
      return true;
    } else {
      return false;
    }
  }
  
  public long getDeployTime() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL propUrl =  classLoader.getResource(DEPLOY_PROP_FILE);
    File propFile = new File(propUrl.getFile());
    return propFile.lastModified();
  }

  public String getLayoutExtension() {
    String prop = getProperty(LAYOUT_EXT_PROPERTY);
    if (prop != null && prop.length() > 0) {
      return prop;
    } else {
      return LAYOUT_EXT_DEFAULT;
    }
  }

  public String getComponentExtension() {
    String prop = getProperty(COMPONENT_EXT_PROPERTY);
    if (prop != null && prop.length() > 0) {
      return prop;
    } else {
      return COMPONENT_EXT_DEFAULT;
    }
  }

  public String getChartExtension() {
    String prop = getProperty(CHART_EXT_PROPERTY);
    if (prop != null && prop.length() > 0) {
      return prop;
    } else {
      return CHART_EXT_DEFAULT;
    }
  }

  public String getUploadDir() {
    String prop = getProperty(UPLOAD_DIR_PROPERTY);
    if (prop == null || prop.length() == 0) {
      prop = System.getProperty("catalina.home") + "/webapps" + Configuration.getInstance().getMainContext() + "/upload/";
    }
    File upDir = new File(prop);
    if (!upDir.exists()) {
      upDir.mkdirs();
    }
    return prop;
  }
}
