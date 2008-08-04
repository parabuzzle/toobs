package org.toobsframework.pres.layout.manager;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Unmarshaller;
import org.toobsframework.pres.layout.ComponentLayoutInitializationException;
import org.toobsframework.pres.layout.ComponentLayoutNotFoundException;
import org.toobsframework.pres.component.config.ContentType;
import org.toobsframework.pres.layout.RuntimeLayout;
import org.toobsframework.pres.layout.RuntimeLayoutConfig;
import org.toobsframework.pres.layout.config.Layout;
import org.toobsframework.pres.layout.config.Layouts;
import org.toobsframework.exception.PermissionException;


@SuppressWarnings("unchecked")
public final class ComponentLayoutManager implements IComponentLayoutManager {

  private static Log log = LogFactory.getLog(ComponentLayoutManager.class);
  
  private static Map registry;
  private static boolean doReload = false;
  private static boolean initDone = false;
  private static long[] lastModified;
  private static long localDeployTime = 0L;
  
  private List configFiles = null;
  
  private ComponentLayoutManager() throws ComponentLayoutInitializationException {
    log.info("Constructing new ComponentLayoutManager");
    registry = new HashMap();
  }
  
  public RuntimeLayout getLayout(String Id, long deployTime)
      throws ComponentLayoutNotFoundException, ComponentLayoutInitializationException {
    //if (doReload || !initDone) {
    if (doReload || deployTime > localDeployTime) {
      Date initStart = new Date();
      this.init();
      Date initEnd = new Date();
      log.info("Init Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    synchronized (registry) {
      if (!registry.containsKey(Id)) {
        ComponentLayoutNotFoundException ex = new ComponentLayoutNotFoundException();
        ex.setComponentLayoutId(Id);
        throw ex;
      }
      localDeployTime = deployTime;
      return (RuntimeLayout) registry.get(Id);
    }
  }
  
  public RuntimeLayout getLayout(PermissionException permissionException)
    throws ComponentLayoutNotFoundException, ComponentLayoutInitializationException {
    String objectErrorPage = permissionException.getAction() + permissionException.getObjectTypeName();
    synchronized (registry) {
      if (!registry.containsKey(objectErrorPage)) {
        log.info("Permission Error page " + objectErrorPage + " not defined");
        return null;
      }
      return (RuntimeLayout) registry.get(objectErrorPage);
    }
  }

  // Read from config file
  public void init() throws ComponentLayoutInitializationException {
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
          if (log.isDebugEnabled()) {
            log.debug("Checking Configuration file: " + fileName);
          }
          ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
          URL configFileURL = classLoader.getResource(fileName);
          if (configFileURL == null) {
            log.warn("Configuration file " + fileName + " not found");
            continue;
          }
          File configFile = new File(configFileURL.getFile());
          if (log.isDebugEnabled()) {
            log.debug("Layout Config: " + fileName + " File Mod: " + new Date(configFile.lastModified()) + " Last Mod: " + new Date(lastModified[fileCounter]));
          }
          if (configFile.lastModified() <= lastModified[fileCounter]) {
            continue;
          }
          log.info("Reloading Layouts file [" + fileName + "]");
          //registry.clear();
          reader = new InputStreamReader(configFileURL.openStream());
          Unmarshaller unmarshaller = new Unmarshaller(
              Class.forName(Layouts.class.getName()));
          unmarshaller.setValidation(false);
          Layouts componentLayoutConfig = (Layouts) unmarshaller.unmarshal(reader);
          if (componentLayoutConfig.getFullReload()) {
            lastModified = new long[l];
          }
          Layout[] layouts = componentLayoutConfig.getLayout();
          //HashMap tempLayoutMap = new HashMap();
          if ((layouts != null) && (layouts.length > 0)) {
            Layout compLayout = null;
            RuntimeLayout layout = null;
            RuntimeLayoutConfig layoutConfig = null;
            for (int i = 0; i < layouts.length; i ++) {
              compLayout = layouts[i];
              layoutConfig = new RuntimeLayoutConfig();
              layout = new RuntimeLayout();
              
              // Inherited from extended definition
              String extendStr = compLayout.getExtends();
              if (extendStr != null) {
                String[] extSplit = extendStr.split(";");
                for (int ext = 0; ext < extSplit.length; ext++) {
                  String extension = extSplit[ext];
                  RuntimeLayout extend = (RuntimeLayout)registry.get(extension);
                  if (extend == null) {
                    log.error("The Layout extension " + extension + " for " + compLayout.getId() + 
                        " could not be located in the registry.\n"
                        + "Check the spelling and case of the extends property and ensure it is defined before\n"
                        + "the dependent templates");
                    throw new ComponentLayoutInitializationException("Missing extension " + extension + " for " + compLayout.getId());
                  }
                  RuntimeLayoutConfig extendConfig = extend.getConfig();
                  if (extend == null) {
                    throw new ComponentLayoutInitializationException("Layout " + compLayout.getId() + 
                        " cannot extend " + extension + " cause it does not exist or has not yet been loaded");
                  }
                  layoutConfig.addParam(extendConfig.getAllParams());
                  layoutConfig.addTransformParam(extendConfig.getAllTransformParams());
                  layoutConfig.addSection(extendConfig.getAllSections());
                  layoutConfig.setNoAccessLayout(extendConfig.getNoAccessLayout());
                  //layout.addTransform(extend.getAllTransforms());
                  layout.getTransforms().putAll(extend.getTransforms());
                  //layout.setUseComponentScan(extend.isUseComponentScan());
                  layout.setEmbedded(extend.isEmbedded());
                }
              }
              
              if (compLayout.getParameters() != null) {
                layoutConfig.addParam(compLayout.getParameters().getParameter());
              }
              if (compLayout.getTransformParameters() != null) {
                layoutConfig.addTransformParam(compLayout.getTransformParameters().getParameter());
              }
              layoutConfig.addSection(compLayout.getSection());
              if (compLayout.getNoAccessLayout() != null) {
                layoutConfig.setNoAccessLayout(compLayout.getNoAccessLayout());
              }
              layout.setId(compLayout.getId());
              //layout.setUseComponentScan(compLayout.getUseComponentScan() || layout.isEmbedded());
              layout.setEmbedded(compLayout.getEmbedded() || layout.isEmbedded());
              
              //Set component pipeline properties.
              if (compLayout.getPipeline() != null) {
                Enumeration contentTypeEnum = compLayout.getPipeline().enumerateContentType();
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
                    layout.getTransforms().put(ctSplit[ct], theseTransforms);
                  }
                }
              }
              /*
              if (compLayout.getTransformCount() > 0) {
                layout.getTransforms().clear();
                for (int t = 0; t < compLayout.getTransformCount(); t++) {
                  layout.addTransform(new Transform(compLayout.getTransform(t)));
                }
              }
              */
              layout.setConfig(layoutConfig);
              
              layout.setDoItRef(compLayout.getDoItRef());
              
              if (log.isDebugEnabled()) {
                log.debug("Layout " + compLayout.getId() + " xml " + layout.getLayoutXml());
              }
              if (registry.containsKey(compLayout.getId()) && !initDone) {
                log.warn("Overriding layout with Id: " + compLayout.getId());
              }
              registry.put(compLayout.getId(), layout);
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