package org.toobsframework.pres.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.jfree.ui.FilesystemFilter;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.app.config.Applications;
import org.toobsframework.pres.app.config.ComponentConfig;
import org.toobsframework.pres.app.config.ConfigLocation;
import org.toobsframework.pres.app.config.LayoutConfig;
import org.toobsframework.pres.app.config.ToobsApp;
import org.toobsframework.pres.app.config.XSLConfig;
import org.toobsframework.pres.app.controller.AppHandler.AppView;
import org.toobsframework.pres.component.Component;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.config.Components;
import org.toobsframework.pres.component.datasource.api.DataSourceInitializationException;
import org.toobsframework.pres.component.datasource.manager.DataSourceNotFoundException;
import org.toobsframework.pres.component.manager.ComponentManager;
import org.toobsframework.pres.layout.ComponentLayoutInitializationException;
import org.toobsframework.pres.layout.RuntimeLayout;
import org.toobsframework.pres.layout.config.Layout;
import org.toobsframework.pres.layout.config.Layouts;
import org.toobsframework.pres.layout.manager.ComponentLayoutManager;
import org.toobsframework.transformpipeline.domain.XSLUriResolverImpl;
import org.toobsframework.util.IRequest;

public class AppManager {

  private static final Log log = LogFactory.getLog(AppManager.class);

  private static String appsDirName = "apps";
  private static File appsDir;
  private static Hashtable<String,ToobsApplication> appRegistry;
  private static boolean initDone = false;

  public AppManager() {
    log.info("Constructing new AppLoader");
    appRegistry = new Hashtable<String,ToobsApplication>();
    try {
      init();
      initDone = true;
    } catch (ComponentLayoutInitializationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

  public boolean containsApp(String appRoot) {
    return appRegistry.containsKey(appRoot);
  }

  public ToobsApplication getApp(String appRoot) throws AppNotFoundException {
    ToobsApplication toobsApp = null;
    if (!appRegistry.containsKey(appRoot)) {
      throw new AppNotFoundException();
    }
    toobsApp = appRegistry.get(appRoot);
    synchronized(toobsApp) {
      return toobsApp;
    }
  }

  public void init() throws ComponentLayoutInitializationException {
    
    if ( appsDir == null || !appsDir.exists() ) {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      
      URL appsUrl = classLoader.getResource(appsDirName);
      if (appsUrl == null) {
        log.warn("The Toobs Apps directory was not found");
        return;
      }
  
      appsDir = new File(appsUrl.getFile());
      if (log.isDebugEnabled()) {
        log.debug("Apps dir " + appsDirName + " found at " + appsDir);
      }
    }
    
    if (appsDir != null && appsDir.exists()) {
      File[] apps = appsDir.listFiles();
      
      File[] appList;
      FilesystemFilter filter = new FilesystemFilter(".tapp.xml", "Toobs Application filter", false); 
      if (log.isDebugEnabled())
        log.debug("Apps Dir Listing");
      for ( int i = 0; i < apps.length; i++ ) {
        if (log.isDebugEnabled()) {
          log.debug("  App " + apps[i] + " isDir " + apps[i].isDirectory());
        }
        if (apps[i].isDirectory() && (appList = apps[i].listFiles(filter) ).length > 0) {
          ToobsApplication toobsApp = new ToobsApplication(); 
          for ( int j = 0; j < appList.length; j++ ) {
            if (log.isDebugEnabled()) {
              log.debug("  App config " + appList[j]);
            }
            toobsApp.setName(apps[i].getName());
            configureApplication(toobsApp, apps[i], appList[j]);
          }
        }
      }
    }
    
  }
  
  private void configureApplication(ToobsApplication toobsApp, File appDir, File appFile) throws ComponentLayoutInitializationException {
    InputStreamReader reader = null;

    try {
      
      reader = new InputStreamReader(new FileInputStream(appFile));
      
      Unmarshaller unmarshaller = new Unmarshaller(
          Class.forName(Applications.class.getName()));

      unmarshaller.setValidation(false);
      
      Applications applications = (Applications) unmarshaller.unmarshal(reader);
      
      ToobsApp toobsAppDef = applications.getToobsApp(0);
      
      configureRoot(toobsApp,toobsAppDef.getRoot());
      configureXSL(toobsApp,toobsAppDef.getXSLConfig());
      configureLayouts(toobsApp,toobsAppDef.getLayoutConfig(), appDir);
      configureComponents(toobsApp,toobsAppDef.getComponentConfig(), appDir);
      
      appRegistry.put(toobsApp.getRoot(), toobsApp);

    } catch (MarshalException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ValidationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignore) { }
      }
    }

  }

  private void configureComponents(ToobsApplication toobsApp, ComponentConfig componentConfig, File appDir) {

    //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStreamReader reader = null;
    Map<String,Component> compMap = new HashMap<String,Component>();

    //String appPrefix = appsDirName + "/" + toobsApp.getName() + "/";

    for ( int i = 0; i < componentConfig.getConfigLocationCount(); i++ ) {
      ConfigLocation cLoc = componentConfig.getConfigLocation(i);
      File compDir = null;
      if (cLoc.isInApplication()) {
        compDir = findDirInDir(appDir, cLoc.getDir());
      } else {
        compDir = findDirInClasspath(cLoc.getDir());
      }
      if (compDir == null) {
        log.warn("Could not locate Layout configuration location: " + cLoc.getDir() + " for app: " + toobsApp.getName());
        continue;
      }
      
      String fileName = null;
      for ( int j = 0; j < cLoc.getConfigFile().length; j++) {

        //fileName = appPrefix + cLoc.getConfigFile(j).getName();
        fileName = compDir.getAbsolutePath() + "/" + cLoc.getConfigFile(j).getName();
        
        /*
        URL configFileURL = classLoader.getResource(fileName);
        if (configFileURL == null) {
          log.warn("Layout Configuration file " + fileName + " not found");
          continue;
        }
        */
        File configFile = new File(fileName);
        if (!configFile.exists()) {
          log.warn("Layout Configuration file " + fileName + " not found");
          continue;
        }
        try {
          reader = new InputStreamReader(new FileInputStream(configFile));
          Unmarshaller unmarshaller = new Unmarshaller(
              Class.forName(Components.class.getName()));
          unmarshaller.setValidation(false);
          Components components = (Components) unmarshaller.unmarshal(reader);

          org.toobsframework.pres.component.config.Component[] comps = components.getComponent();
          if ((comps != null) && (comps.length > 0)) {
            org.toobsframework.pres.component.config.Component compDef = null;
            Component comp = null;
            for (int k = 0; k < comps.length; k ++) {
              compDef = comps[k];
              
              comp = new Component();
              
              ComponentManager.configureComponent(compDef, comp, fileName, compMap);
              
              if (compMap.containsKey(compDef.getId())) {
                log.warn("Overriding layout with Id: " + compDef.getId());
              }
              compMap.put(compDef.getId(), comp);
            }
          }
        } catch (MarshalException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ValidationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (DataSourceInitializationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (DataSourceNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
    }
    toobsApp.setComponents(compMap);
    
  }

  private void configureLayouts(ToobsApplication toobsApp, LayoutConfig layoutConfig, File appDir) throws ComponentLayoutInitializationException {

    //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStreamReader reader = null;
    Map<String,RuntimeLayout> layoutMap = new HashMap<String,RuntimeLayout>();

    //String appPrefix = appsDirName + "/" + toobsApp.getName() + "/";

    for ( int i = 0; i < layoutConfig.getConfigLocationCount(); i++ ) {
      ConfigLocation cLoc = layoutConfig.getConfigLocation(i);
      if (log.isDebugEnabled()) {
        log.debug("Layout Config location: " + cLoc.getDir());
      }
      File layoutDir = null;
      if (cLoc.isInApplication()) {
        layoutDir = findDirInDir(appDir, cLoc.getDir());
        if (log.isDebugEnabled()) {
          log.debug("Layout dir from dir: " + layoutDir.getPath());
        }
      } else {
        layoutDir = findDirInClasspath(cLoc.getDir());
        if (log.isDebugEnabled()) {
          log.debug("Layout dir from cp: " + layoutDir.getPath());
        }
      }
      if (layoutDir == null) {
        log.warn("Could not locate Layout configuration location: " + cLoc.getDir() + " for app: " + toobsApp.getName());
        continue;
      }
      
      String fileName = null;
      for ( int j = 0; j < cLoc.getConfigFile().length; j++) {

        //fileName = appPrefix + cLoc.getConfigFile(j).getName();
        fileName = layoutDir.getAbsolutePath() + "/" + cLoc.getConfigFile(j).getName();
        
        /*
        URL configFileURL = classLoader.getResource(fileName);
        if (configFileURL == null) {
          log.warn("Layout Configuration file " + fileName + " not found");
          continue;
        }
        */
        File configFile = new File(fileName);
        if (!configFile.exists()) {
          log.warn("Layout Configuration file " + fileName + " not found");
          continue;
        }
        try {
          reader = new InputStreamReader(new FileInputStream(configFile));
          Unmarshaller unmarshaller = new Unmarshaller(
              Class.forName(Layouts.class.getName()));
          unmarshaller.setValidation(false);
          Layouts componentLayoutConfig = (Layouts) unmarshaller.unmarshal(reader);

          Layout[] layouts = componentLayoutConfig.getLayout();
          if ((layouts != null) && (layouts.length > 0)) {
            Layout compLayout = null;
            RuntimeLayout layout = null;
            for (int k = 0; k < layouts.length; k ++) {
              compLayout = layouts[k];
              
              layout = new RuntimeLayout();
              
              ComponentLayoutManager.configureLayout(compLayout, layout, layoutMap);
              
              if (layoutMap.containsKey(compLayout.getId())) {
                log.warn("Overriding layout with Id: " + compLayout.getId());
              }
              layoutMap.put(compLayout.getId(), layout);
            }
          }
        } catch (MarshalException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ValidationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
    }
    toobsApp.setLayouts(layoutMap);
    
  }

  private File findDirInDir(File dir, String name) {
    File[] tmp;
    FilesystemFilter filter = new FilesystemFilter(name, "Toobs Layout filter", false);
    if ( (tmp = dir.listFiles(filter)).length > 0 && tmp[0].isDirectory()) {
      return tmp[0];
    }
    return null;
  }

  private File findDirInClasspath(String name) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    
    return new File( classLoader.getResource(name).getFile() );
  }

  private void configureXSL(ToobsApplication toobsApp, XSLConfig config) {
    String[] locations = new String[ config.getXSLLocationCount() ];
    String appPrefix = appsDirName + "/" + toobsApp.getName() + "/";

    for (int i = 0; i < locations.length; i++) {
      locations[i] = (config.getXSLLocation(i).isInApplication() ? appPrefix : "") + config.getXSLLocation(i).getDir();
      if (log.isDebugEnabled()) {
        log.debug("App [" + toobsApp.getName() + "] - adding xsl location: " + locations[i]);
      }
    }
    toobsApp.setXslLocations(locations);
  }

  private void configureRoot(ToobsApplication toobsApp, String root) {
    if (root == null) {
      toobsApp.setRoot("/" + toobsApp.getName().toLowerCase());
    } else {
      toobsApp.setRoot(root);
    }
  }

  public String getAppsDirName() {
    return appsDirName;
  }

  public void setAppsDirName(String appsDirName) {
    this.appsDirName = appsDirName;
  }

  public void showApps() {
    log.debug("Installed Applications");
    Enumeration<String> keys = appRegistry.keys();
    while (keys.hasMoreElements()) {
     String key = keys.nextElement();
     log.debug("  Installed App: " + key);
    }
  }

  public String renderView(AppView appView, IRequest request) throws AppNotFoundException, ComponentException, ParameterException {
    if (appView.isComp) {
      return null;
    } else {
      RuntimeLayout layout = getApp(appView.appName).getLayouts().get(appView.viewName);
      return layout.render(request, new XSLUriResolverImpl(getApp(appView.appName).getXslLocations()));
    }
  }
  
}
