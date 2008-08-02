package org.toobs.framework.pres.doit;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.toobs.framework.pres.doit.config.DoIt;
import org.toobs.framework.pres.doit.config.DoItConfig;
import org.toobs.framework.util.Configuration;


/**
 * @author sean
 */
@SuppressWarnings("unchecked")
public final class DoItManager implements IDoItManager {

  private static Log log = LogFactory.getLog(DoItManager.class);

  private Map registry;
  private static boolean doReload = true;
  private static boolean initDone = false;
  private static long[] lastModified;

  private List configFiles = null;

  private DoItManager() throws DoItInitializationException {
    log.info("Constructing new DoItManager");
    registry = new HashMap();
  }

  public DoIt getDoIt(String Id) throws DoItInitializationException {
    if (doReload || !initDone) {
      //Date initStart = new Date();
      this.init();
      //Date initEnd = new Date();
      //log.info("Init Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    synchronized (registry) {
      if (!registry.containsKey(Id)) {
        throw new DoItInitializationException("DoIt " + Id + " not found");
      }
      return (DoIt) registry.get(Id);
    }
  }

  // Read from config file
  private void init() throws DoItInitializationException {
    synchronized (registry) {
      InputStreamReader reader = null;
      if(configFiles == null) {
        return;
      }
      int l = configFiles.size();
      if (lastModified == null) {
        //log.info("LastModified is null " + this.toString() + " Registry " + registry);
        lastModified = new long[l];
      }
      for(int fileCounter = 0; fileCounter < l; fileCounter++) {
        String fileName = (String)configFiles.get(fileCounter);
        try {
          ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
          URL configFileURL = classLoader.getResource(fileName);
          File configFile = new File(configFileURL.getFile());
          if (configFile.lastModified() <= lastModified[fileCounter]) {
            continue;
          }
          log.info("Reloading DoItConfig - " + fileName);
          //registry.clear();
          reader = new InputStreamReader(configFileURL.openStream());
          Unmarshaller unmarshaller = new Unmarshaller(Class.forName(DoItConfig.class
              .getName()));
          unmarshaller.setValidation(false);
          DoItConfig doItConfig = (DoItConfig) unmarshaller.unmarshal(reader);
          Enumeration doIts = doItConfig.enumerateDoIt();
          while(doIts.hasMoreElements()) {
            DoIt thisDoIt = (DoIt) doIts.nextElement();
            if (registry.containsKey(thisDoIt.getName()) && !initDone) {
              log.warn("Overriding doit with Id: " + thisDoIt.getName());
            }
            this.registry.put(thisDoIt.getName(), thisDoIt);
          }
          lastModified[fileCounter] = configFile.lastModified();
        } catch (MarshalException e) {
          throw new DoItInitializationException(e);
        } catch (ValidationException e) {
          throw new DoItInitializationException(e);
        } catch (IOException e) {
          throw new DoItInitializationException(e);
        } catch (ClassNotFoundException e) {
          throw new DoItInitializationException(e);
        } finally {
          if (reader != null) {
            try {
              reader.close();
            } catch (IOException e) {
            }
          }
        }
      }
      doReload = Configuration.getInstance().getReloadDoits();
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