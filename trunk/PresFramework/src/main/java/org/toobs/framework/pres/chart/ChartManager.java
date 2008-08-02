package org.toobs.framework.pres.chart;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Unmarshaller;
import org.toobs.framework.pres.chart.config.Chart;
import org.toobs.framework.pres.chart.config.ChartConfig;
import org.toobs.framework.util.Configuration;


@SuppressWarnings("unchecked")
public final class ChartManager implements IChartManager {

  private static Log log = LogFactory.getLog(ChartManager.class);
  
  private static Map registry;
  private static boolean doReload = true;
  private static boolean initDone = false;
  private static long[] lastModified;
  
  private List configFiles = null;
  
  private ChartManager() throws ChartInitializationException {
    log.info("Constructing new ChartManager");
    registry = new HashMap();
  }
  
  public ChartDefinition getChartDefinition(String Id)
      throws ChartNotFoundException, ChartInitializationException {
    if (doReload || !initDone) {
      Date initStart = new Date();
      this.init();
      Date initEnd = new Date();
      log.info("Init Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    synchronized (registry) {
      if (!registry.containsKey(Id)) {
        throw new ChartNotFoundException(Id);
      }
      return (ChartDefinition) registry.get(Id);
    }
  }
  
  public void init() throws ChartInitializationException {
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
          if (configFile.lastModified() <= lastModified[fileCounter]) {
            continue;
          }
          log.info("Reloading ChartConfig file [" + fileName + "]");
          //registry.clear();
          reader = new InputStreamReader(configFileURL.openStream());
          Unmarshaller unmarshaller = new Unmarshaller(Class.forName(ChartConfig.class.getName()));
          unmarshaller.setValidation(false);
          
          ChartConfig chartConfig = (ChartConfig) unmarshaller.unmarshal(reader);

          registerCharts(chartConfig.getChart());

          doReload = Configuration.getInstance().getReloadComponents();
          lastModified[fileCounter] = configFile.lastModified();
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

  private void registerCharts(Chart[] charts) {
    if ((charts != null) && (charts.length > 0)) {
      Chart chart = null;
      ChartDefinition chartDefinition = null;

      for (int i = 0; i < charts.length; i ++) {
        chart = charts[i];
        chartDefinition = new ChartDefinition();
        
        chartDefinition.setId(chart.getId());
        chartDefinition.setChartHeight(chart.getHeight());
        chartDefinition.setChartWidth(chart.getWidth());
        chartDefinition.setBackgroundColor(chart.getBackgroundColor());
        chartDefinition.setShowLegend(chart.getShowLegend());
        chartDefinition.setDoImageWithMap(chart.getDoImageWithMap());
        chartDefinition.setUrlFragmentBean(chart.getUrlFragmentBean());
        
        if (chart.getParameterMapping() != null) {
          chartDefinition.setParameterMapping(chart.getParameterMapping());
        }
        if (chart.getTitle() != null) {
          chartDefinition.setTitle(chart.getTitle());
        }
        if (chart.getSubtitle() != null) {
          chartDefinition.setSubtitle(chart.getSubtitle());
        }
        if (chart.getLegend() != null) {
          chartDefinition.setLegend(chart.getLegend());
        }
        chartDefinition.setPlot(chart.getPlot());

        
        if (registry.containsKey(chart.getId()) && !initDone) {
          log.warn("Overriding chartDefinition with Id: " + chart.getId());
        }
        registry.put(chart.getId(), chartDefinition);
      }
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