package org.toobs.framework.pres.client;

import org.toobs.framework.client.ClientConfiguration;
import org.toobs.framework.pres.chart.IChartManager;


public class ChartConfiguration extends ClientConfiguration {
  
  private IChartManager chartManager;
  
  public void init() {
    chartManager.addConfigFiles(configFiles);
  }

  public IChartManager getChartManager() {
    return chartManager;
  }

  public void setChartManager(IChartManager chartManager) {
    this.chartManager = chartManager;
  }
}
