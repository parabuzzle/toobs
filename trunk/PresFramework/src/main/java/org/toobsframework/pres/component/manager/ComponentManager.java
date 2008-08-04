package org.toobsframework.pres.component.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Unmarshaller;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.ComponentNotFoundException;
import org.toobsframework.pres.component.ComponentNotInitializedException;
import org.toobsframework.pres.component.config.Component;
import org.toobsframework.pres.component.config.Components;
import org.toobsframework.pres.component.config.ContentType;
import org.toobsframework.pres.component.config.DataSourceProperty;
import org.toobsframework.data.beanutil.converter.DateToStringConverter;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.component.datasource.api.IDataSource;

import java.util.Vector;
import java.util.Enumeration;


/**
 * @author pudney
 */
@SuppressWarnings("unchecked")
public final class ComponentManager implements IComponentManager {

  private static Log log = LogFactory.getLog(ComponentManager.class);

  private static Map registry;
  private static boolean doReload = true;
  private static boolean initDone = false;
  private static long[] lastModified;
  private static long localDeployTime = 0L;

  private IDataSource defaultDatasource;
  
  private List configFiles = null;

  private ComponentManager() throws ComponentInitializationException {
    log.info("Constructing new ComponentManager");
    registry = new HashMap();
    ConvertUtils.register(new DateToStringConverter(), String.class);
    //ConvertUtils.register(new StringToDateConverter(), Date.class);
  }

  public org.toobsframework.pres.component.Component getComponent(String Id, long deployTime)
      throws ComponentNotFoundException, ComponentInitializationException {
    //if (doReload || !initDone) {
    if (doReload || deployTime > localDeployTime) {
      //Date initStart = new Date();
      this.init();
      //Date initEnd = new Date();
      //log.info("Init Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    synchronized (registry) {
      if (!registry.containsKey(Id)) {
        ComponentNotFoundException ex = new ComponentNotFoundException();
        ex.setComponentId(Id);
        throw ex;
      }
      localDeployTime = deployTime;
      return (org.toobsframework.pres.component.Component) registry.get(Id);
    }
  }
  
  public String renderComponent(
      org.toobsframework.pres.component.Component component,
      String contentType, Map params, Map paramsOut, boolean appendUrlScanner)
      throws ComponentNotInitializedException, ComponentException, ParameterException {
    return component.render(contentType, params, paramsOut, appendUrlScanner);
  }

  // Read from config file
  public void init() throws ComponentInitializationException {
    synchronized (registry) {
      InputStreamReader reader = null;
      if(configFiles == null) {
        return;
      }
      int l = configFiles.size();
      if (lastModified == null) {
        log.info("LastModified is null " + this.toString() + " Registry " + registry);
        lastModified = new long[l];
      }
      for(int fileCounter = 0; fileCounter < l; fileCounter++) {
        String fileName = (String)configFiles.get(fileCounter);
        try {
          ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
          URL configFileURL = classLoader.getResource(fileName);
          if (configFileURL == null) {
            log.warn("Skipping missing Components file [" + fileName + "]");
            continue;
          }
          File configFile = new File(configFileURL.getFile());
          if (configFile.lastModified() <= lastModified[fileCounter]) {
            continue;
          }
          log.info("Reloading Components file [" + fileName + "]");
          //registry.clear();
          reader = new InputStreamReader(configFileURL.openStream());
          Unmarshaller unmarshaller = new Unmarshaller(
              Class.forName(Components.class.getName()));
          unmarshaller.setValidation(false);
          Components componentConfig = (Components) unmarshaller.unmarshal(reader);
          Component[] components = componentConfig.getComponent();
          if ((components != null) && (components.length > 0)) {
            Component comp = null;
            org.toobsframework.pres.component.Component uic = null;
            for (int i = 0; i < components.length; i++) {
              comp = components[i];
              Map dsParams = new HashMap();
              if(comp.getDataSource() != null){
                String dsClassName = comp.getDataSource().getClassName();
                DataSourceProperty[] dsProperties = comp.getDataSource().getDataSourceProperty();
                if ((dsProperties != null) && (dsProperties.length > 0)) {
                  String propertyName = null;
                  String[] propertyValue = null;
                  for (int p = 0; p < dsProperties.length; p++) {
                    propertyName = dsProperties[p].getDataSourcePropertyName();
                    propertyValue = dsProperties[p].getDataSourcePropertyValue();
                    dsParams.put(propertyName, propertyValue);
                  }
                }
                uic = new org.toobsframework.pres.component.Component(dsClassName, dsParams);
              } else {
                uic = new org.toobsframework.pres.component.Component();
                uic.setDs(defaultDatasource);
              }
               
              uic.setId(comp.getId());
              uic.setFileName(fileName);
              uic.setRenderErrorObject(comp.getRenderErrorObject());
              //Set object config property.
              uic.setObjectsConfig(comp.getGetObject());
              //Set component pipeline properties.
              HashMap transforms = new HashMap();
              Enumeration contentTypeEnum = comp.getPipeline().enumerateContentType();
              while (contentTypeEnum.hasMoreElements()) {
                Vector theseTransforms = new Vector();
                ContentType thisContentType = (ContentType) contentTypeEnum.nextElement();
                Enumeration transEnum = thisContentType.enumerateTransform();
                while (transEnum.hasMoreElements()) {
                  org.toobsframework.pres.component.config.Transform thisTransformConfig = (org.toobsframework.pres.component.config.Transform) transEnum.nextElement();                  
                  org.toobsframework.pres.component.Transform thisTransform = new org.toobsframework.pres.component.Transform();
    
                  thisTransform.setTransformName(thisTransformConfig.getName());
                  thisTransform.setTransformParams(thisTransformConfig.getParameters());
    
                  theseTransforms.add(thisTransform);
                }
                String[] ctSplit = thisContentType.getContentType().split(";");
                for (int ct = 0; ct < ctSplit.length; ct++) {
                  transforms.put(ctSplit[ct], theseTransforms);
                }
              }
              uic.setTransforms(transforms);
              uic.setControllerNames(new String[comp.getControllerCount()]);
              for (int c=0; c < comp.getControllerCount(); c++) {
                uic.getControllerNames()[c] = comp.getController(c).getName();
              }
              uic.setStyles(new String[comp.getStyleCount()]);
              for (int c=0; c < comp.getStyleCount(); c++) {
                uic.getStyles()[c] = comp.getStyle(c).getName();
              }
              uic.init();
              if (registry.containsKey(uic.getId()) && !initDone) {
                log.warn("Overriding component with Id: " + uic.getId());
              }
              registry.put(uic.getId(), uic);
            }
          }
          //doReload = Configuration.getInstance().getReloadComponents();
          lastModified[fileCounter] = configFile.lastModified();
          doReload = false;
        } catch (Exception ex) {
          log.error("ComponentLayout initialization failed " + ex.getMessage(), ex);
          doReload = true;
        } finally {
          if (reader != null) {
            try {
              reader.close();
            } catch (IOException e) {
            }
          }
        }
      }
      initDone = true;
    }
  }

  public IDataSource getDefaultDatasource() {
    return defaultDatasource;
  }

  public void setDefaultDatasource(IDataSource defaultDatasource) {
    this.defaultDatasource = defaultDatasource;
  }

  public List getConfigFiles() {
    return configFiles;
  }

  public void setConfigFiles(List configFiles) {
    this.configFiles = configFiles;
  }

  public void addConfigFiles(List configFiles) {
    this.configFiles.addAll(configFiles);
  }
  
  
}
