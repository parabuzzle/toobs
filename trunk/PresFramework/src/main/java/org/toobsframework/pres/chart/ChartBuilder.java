package org.toobsframework.pres.chart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlotEx;
import org.jfree.chart.plot.CombinedRangeCategoryPlotEx;
import org.jfree.chart.plot.MultiCategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetGroup;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.toobsframework.pres.chart.config.BasePlot;
import org.toobsframework.pres.chart.config.Dataset;
import org.toobsframework.pres.chart.config.DatasetSeries;
import org.toobsframework.pres.chart.config.DomainAxisDef;
import org.toobsframework.pres.chart.config.RangeAxisDef;
import org.toobsframework.pres.chart.config.types.AxisDefNumberFormaterType;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.component.datasource.api.DataSourceNotInitializedException;
import org.toobsframework.pres.component.datasource.api.IDataSource;
import org.toobsframework.pres.component.datasource.api.InvalidSearchContextException;
import org.toobsframework.pres.component.datasource.api.InvalidSearchFilterException;
import org.toobsframework.pres.component.datasource.api.ObjectCreationException;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.IRequest;

@SuppressWarnings("unchecked")
public class ChartBuilder implements BeanFactoryAware {
  
  private static Log log = LogFactory.getLog(ChartBuilder.class);
  
  private BeanFactory beanFactory;
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  private IDataSource datasource;

  public String buildAsImage(ChartDefinition chartDef, IRequest componentRequest, int width, int height) throws ChartException {
    JFreeChart chart = this.build(chartDef, componentRequest);
    if (width <= 0)
      chartDef.getChartWidth();
    if (height <= 0)
      chartDef.getChartHeight();
    
    String genFileName = chartDef.getId() + "-" + new Date().getTime() + ".png";
    String imageOutputFileName = Configuration.getInstance().getUploadDir() + genFileName;
    try {
      File imageOutputFile = new File(imageOutputFileName);
      OutputStream os = null;
      ChartRenderingInfo chartRenderingInfo = new ChartRenderingInfo();
      try {
        os = new FileOutputStream(imageOutputFile);
        ChartUtilities.writeChartAsPNG(os, chart, width, height, chartRenderingInfo);
      } finally {
        if (os != null) {
          os.close();
        }
      }
    } catch (FileNotFoundException e) {
      throw new ChartException(e);
    } catch (IOException e) {
      throw new ChartException(e);
    }
    
    return imageOutputFileName;
  }

  public JFreeChart build(ChartDefinition chartDef, IRequest componentRequest) throws ChartException {
    
    JFreeChart chart = null;
    try {
      Map params = componentRequest.getParams();
      if(chartDef.getParameterMapping() != null){
        ParameterUtil.mapParameters("Chart:AreaChart:" + chartDef.getId(), chartDef.getParameterMapping().getParameter(), params, params, chartDef.getId(), null);
      }
      
      Plot plot = configurePlot(chartDef.getId(), chartDef.getPlot(), params);

      chart = finishChart(chartDef, plot, params);

    } catch (ParameterException e) {
      log.error("Chart build exception " + e.getMessage(), e);
      throw new ChartException(e);
    }

    return chart;
  }

  private Plot configurePlot(String id, org.toobsframework.pres.chart.config.Plot plotDef, Map params) throws ChartException {
    
    Plot plot = null;
    if (plotDef.getSubPlotCount() > 0) {
      boolean is3D = (ParameterUtil.resolveParam(plotDef.getIs3D(), params, "false")[0].equals("false") ? false : true);
      int plotType = ChartUtil.getSupportedPlots().get(ParameterUtil.resolveParam(plotDef.getType(), params, "multiCategory")[0]);
      PlotOrientation orientation = (ParameterUtil.resolveParam(plotDef.getOrientation(), params, "vertical")[0].equals("horizontal") ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL);
      switch (plotType) {
        case ChartUtil.PLOT_MULTICATEGORY_TYPE:
          plot = new MultiCategoryPlot();
          for (int p = 0; p<plotDef.getSubPlotCount(); p++) {
            ((MultiCategoryPlot)plot).add((CategoryPlot)this.configurePlot(id, plotDef.getSubPlot(p), params, true, plotType, plotDef));
          }
          ((MultiCategoryPlot)plot).setOrientation(orientation);
          ((MultiCategoryPlot)plot).setGap(plotDef.getGap());
          if (plotDef.getInsets() != null) {
            plot.setInsets(ChartUtil.getRectangle(plotDef.getInsets(), params));
          }
          break;
        case ChartUtil.PLOT_COMBINEDDOMAINCATEGORY_TYPE:
          CategoryAxis domainAxis = ChartUtil.createCategoryAxis(plotDef.getDomainAxisDef(), params, is3D);
          plot = new CombinedDomainCategoryPlotEx(domainAxis);
          for (int p = 0; p<plotDef.getSubPlotCount(); p++) {
            ((CombinedDomainCategoryPlotEx)plot).add((CategoryPlot)this.configurePlot(id, plotDef.getSubPlot(p), params, true, plotType, plotDef));
          }
          ((CombinedDomainCategoryPlotEx)plot).setOrientation(orientation);
          ((CombinedDomainCategoryPlotEx)plot).setGap(plotDef.getGap());
          if (plotDef.getInsets() != null) {
            plot.setInsets(ChartUtil.getRectangle(plotDef.getInsets(), params));
          }
          break;
        case ChartUtil.PLOT_COMBINEDRANGECATEGORY_TYPE:
          ValueAxis rangeAxis = createValueAxis(plotDef.getRangeAxisDef(), params, is3D);
          plot = new CombinedRangeCategoryPlotEx(rangeAxis);          
          for (int p = 0; p<plotDef.getSubPlotCount(); p++) {
            ((CombinedRangeCategoryPlotEx)plot).add((CategoryPlot)this.configurePlot(id, plotDef.getSubPlot(p), params, true, plotType, plotDef));
          }
          ((CombinedRangeCategoryPlotEx)plot).setOrientation(orientation);
          if (plotDef.getInsets() != null) {
            plot.setInsets(ChartUtil.getRectangle(plotDef.getInsets(), params));
          }
          break;
      }
    } else {
      plot = this.configurePlot(id, plotDef, params, false, -1, null);
    }

    return plot;
  }

  private Plot configurePlot(String id, BasePlot plotDef, Map params, boolean isSubPlot, int parentPlotType, BasePlot parentPlot) throws ChartException {
    
    boolean is3D = (ParameterUtil.resolveParam(plotDef.getIs3D(), params, "false")[0].equals("false") ? false : true);
    Integer plotType = ChartUtil.getSupportedPlots().get(ParameterUtil.resolveParam(plotDef.getType(), params, "multiCategory")[0]);
    if (plotType == null) {
      throw new ChartException("Unsupported Plot type " + ParameterUtil.resolveParam(plotDef.getType(), params, "multiCategory")[0]);
    }
    
    Plot plot = null;
    switch (plotType) {
      case ChartUtil.PLOT_CATEGORY_TYPE:
        
        DomainAxisDef domainAxis = null;
        RangeAxisDef rangeAxis = null;
        plot = new CategoryPlot();
        if (isSubPlot) {
          if (plotDef.getDomainAxisDef() != null && parentPlotType != ChartUtil.PLOT_COMBINEDDOMAINCATEGORY_TYPE) {
            domainAxis = plotDef.getDomainAxisDef();
          } else if (parentPlotType != ChartUtil.PLOT_COMBINEDDOMAINCATEGORY_TYPE) {
            domainAxis = parentPlot.getDomainAxisDef();
          }
          if (plotDef.getRangeAxisDef() != null && parentPlotType != ChartUtil.PLOT_COMBINEDRANGECATEGORY_TYPE) {
            rangeAxis = plotDef.getRangeAxisDef();
          } else if (parentPlotType != ChartUtil.PLOT_COMBINEDRANGECATEGORY_TYPE) {
            rangeAxis = parentPlot.getRangeAxisDef();
          }
        } else {
          domainAxis = plotDef.getDomainAxisDef();
          rangeAxis = plotDef.getRangeAxisDef();
        }
        ((CategoryPlot)plot).setDomainAxis(ChartUtil.createCategoryAxis(domainAxis, params, is3D));
        ((CategoryPlot)plot).setRangeAxis(createValueAxis(rangeAxis, params, is3D));
        
        for (int g = 0; g < plotDef.getDatasetGroupCount(); g++) {
          org.toobsframework.pres.chart.config.DatasetGroup group = plotDef.getDatasetGroup(g);
          CategoryItemRenderer renderer = (CategoryItemRenderer)ChartUtil.getRenderer(plotDef, group, params);
          if (group.getUrlBase() != null) {
            renderer.setBaseItemURLGenerator(new StandardCategoryURLGenerator(group.getUrlBase()));
          }
          ((CategoryPlot)plot).setRenderer(g, renderer);
          DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
          if (group.getId() != null) {
            DatasetGroup datasetGroup = new DatasetGroup(group.getId());
            categoryDataset.setGroup(datasetGroup);
          }
          for (int i = 0; i < group.getDatasetCount(); i++) {
            Dataset dataset = group.getDataset(i);
            generateCategoryDataset(id, categoryDataset, dataset, params);
            this.setValueAxisBounds(((CategoryPlot)plot).getRangeAxis(), rangeAxis, params);
          }
          ((CategoryPlot)plot).setDataset(g, categoryDataset);
        }

        ChartUtil.configurePlot(plot, plotDef, domainAxis, rangeAxis, params);
      break;
      case ChartUtil.PLOT_XY_TYPE:
        plot = new XYPlot();
      break;
      case ChartUtil.PLOT_SPIDER_TYPE:
        plot = new SpiderWebPlot();
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
        for (int g = 0; g < plotDef.getDatasetGroupCount(); g++) {
          org.toobsframework.pres.chart.config.DatasetGroup group = plotDef.getDatasetGroup(g);

          if (group.getUrlBase() != null) {
            ((SpiderWebPlot)plot).setURLGenerator(new StandardCategoryURLGenerator(group.getUrlBase()));
          }
          if (group.getId() != null) {
            DatasetGroup datasetGroup = new DatasetGroup(group.getId());
            categoryDataset.setGroup(datasetGroup);
          }
          for (int i = 0; i < group.getDatasetCount(); i++) {
            Dataset dataset = group.getDataset(i);
            generateCategoryDataset(id, categoryDataset, dataset, params);
            for (int s = 0; s < dataset.getDatasetSeriesCount(); s++) {
              DatasetSeries series = dataset.getDatasetSeries(s); 
              if (series.getColor() != null) {
                ((SpiderWebPlot)plot).setSeriesPaint(i + s, ChartUtil.getColor(series.getColor()));
              }
            }
          }
        }
        ((SpiderWebPlot)plot).setDataset(categoryDataset);
        ChartUtil.configurePlot(plot, plotDef, null, null, params);
      break;
    }

    return plot;
  }

  protected CategoryDataset generateCategoryDataset(String id, DefaultCategoryDataset categoryDataset, Dataset dataset, Map params) throws ChartException {
    Map outParams = new HashMap();
    Map curParams;
    curParams = new HashMap();
    curParams.putAll(params);
    ArrayList dataList = (ArrayList)this.datasetSearch(id, dataset, curParams, outParams);
    for (int j = 0; j < dataList.size(); j++) {
      Object currentRow = dataList.get(j);
      for (int s = 0; s <dataset.getDatasetSeriesCount(); s++) {
        DatasetSeries series = dataset.getDatasetSeries(s);
        String seriesName = ParameterUtil.resolveParam(series.getName(), params, "Series " + (j+s))[0];
        categoryDataset.addValue(
            Double.parseDouble(String.valueOf(ChartUtil.getDatasetValue(currentRow, series.getValueElement(), new Integer(0)))), 
            String.valueOf(ChartUtil.getDatasetValue(currentRow, series.getRowElement(), seriesName)), 
            String.valueOf(ChartUtil.getDatasetValue(currentRow, series.getColumnElement(), seriesName))
            
        );
      }
    }
    params.putAll(outParams);
    
    return categoryDataset;
  }

  protected Collection datasetSearch(String id, Dataset dataset, Map params, Map outParams) throws ChartException {
    try {
      if(dataset.getParameterMapping() != null){
        ParameterUtil.mapParameters("Component:" + id + ":Dataset:" + dataset.getDaoObject(), dataset.getParameterMapping().getParameter(), params, params, id, null);
      }
      return this.datasource.search(
          dataset.getReturnedValueObject(),
          ParameterUtil.resolveParam(dataset.getDaoObject(), params)[0], 
          dataset.getSearchCriteria(),
          dataset.getSearchMethod(), 
          "read", 
          params, outParams);
    } catch (ParameterException e) {
      log.error("Chart data search exception " + e.getMessage(), e);
      throw new ChartException(e);
    } catch (ObjectCreationException e) {
      log.error("Chart data search exception " + e.getMessage(), e);
      throw new ChartException(e);
    } catch (InvalidSearchContextException e) {
      log.error("Chart data search exception " + e.getMessage(), e);
      throw new ChartException(e);
    } catch (InvalidSearchFilterException e) {
      log.error("Chart data search exception " + e.getMessage(), e);
      throw new ChartException(e);
    } catch (DataSourceNotInitializedException e) {
      log.error("Chart data search exception " + e.getMessage(), e);
      throw new ChartException(e);
    }
  }

  public ValueAxis createValueAxis(RangeAxisDef valueAxisDef, Map params, boolean is3D) {
    ValueAxis valueAxis;
    if (is3D) {
      valueAxis = new NumberAxis3D();
    } else {
      valueAxis = new NumberAxis();
    }
    if (valueAxisDef != null) {
      if (valueAxisDef.getRangeLabel() != null) {
        valueAxis.setLabel(ChartUtil.evaluateTextLabel(valueAxisDef.getRangeLabel(), params));
        if (valueAxisDef.getRangeLabel().getFont() != null) {
          valueAxis.setLabelFont(ChartUtil.getFont(valueAxisDef.getRangeLabel(), null));
        }
        valueAxis.setLabelPaint(ChartUtil.getColor(valueAxisDef.getRangeLabel().getColor()));
      }
      if (valueAxisDef.getIntegerTicks()) {
        valueAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      }
      if ( valueAxisDef.getNumberFormater() != null) {
        switch (valueAxisDef.getNumberFormater().getType()) {
        case AxisDefNumberFormaterType.PERCENT_TYPE:
          ((NumberAxis)valueAxis).setNumberFormatOverride(NumberFormat.getPercentInstance());
          break;
        case AxisDefNumberFormaterType.CUSTOMBEAN_TYPE:
          ((NumberAxis)valueAxis).setNumberFormatOverride((NumberFormat)beanFactory.getBean(valueAxisDef.getCustomFormatBean()));
          
          break;
        }
      }
    }
    return valueAxis;
  }

  public void setValueAxisBounds(ValueAxis valueAxis, RangeAxisDef valueAxisDef, Map params) {
    if (valueAxisDef.getUpperBound() != null) {
      Double upper = Double.parseDouble( ParameterUtil.resolveParam(valueAxisDef.getUpperBound(), params, "0.0")[0]);
      if (valueAxis == null || valueAxis.getUpperBound() < upper)
        valueAxis.setUpperBound(upper);
    }
    if (valueAxisDef.getLowerBound() != null) {
      Double lower = Double.parseDouble( ParameterUtil.resolveParam(valueAxisDef.getLowerBound(), params, "0.0")[0]);
      if (valueAxis == null || valueAxis.getLowerBound() < lower)
        valueAxis.setLowerBound(lower);
    }

    double lowerMargin = Double.parseDouble( ParameterUtil.resolveParam(valueAxisDef.getLowerMargin(), params, "0.0")[0] );
    double upperMargin = Double.parseDouble( ParameterUtil.resolveParam(valueAxisDef.getUpperMargin(), params, "0.0")[0] );
    valueAxis.setLowerMargin(lowerMargin);
    valueAxis.setUpperMargin(upperMargin);
  }

  private JFreeChart finishChart(ChartDefinition chartDef, Plot plot, Map params) {
    
    JFreeChart chart = new JFreeChart(
        ChartUtil.evaluateTextLabel(chartDef.getTitle(), params), ChartUtil.getFont(chartDef.getTitle(), JFreeChart.DEFAULT_TITLE_FONT), plot, chartDef.isShowLegend());
    
    if (chartDef.getSubtitle() != null) {
      TextTitle subtitle = new TextTitle(ChartUtil.evaluateTextLabel(chartDef.getSubtitle(), params));
      if (chartDef.getSubtitle().getFont() != null) {
        subtitle.setFont(ChartUtil.getFont(chartDef.getSubtitle(), null));
      }
      subtitle.setPosition(ChartUtil.getPosition(chartDef.getSubtitle().getPosition()));
      subtitle.setPadding(ChartUtil.getRectangle(chartDef.getSubtitle().getPadding(), params));
      subtitle.setVerticalAlignment(ChartUtil.getVerticalAlignment(chartDef.getSubtitle().getVerticalAlignment()));
      subtitle.setPaint(ChartUtil.getColor(chartDef.getSubtitle().getColor()));
      chart.addSubtitle(subtitle);
    }
    
    chart.setBackgroundPaint(ChartUtil.getColor(chartDef.getBackgroundColor()));
    if (chartDef.getTitle() != null && chartDef.getTitle().getColor() != null) {
      chart.getTitle().setPaint(ChartUtil.getColor(chartDef.getTitle().getColor()));
    }

    if (chartDef.isShowLegend()) {
      ChartUtil.configureLegend(chart, chartDef.getLegend(), params);
    }
    
    return chart;
  }
  
  public IDataSource getDatasource() {
    return datasource;
  }

  public void setDatasource(IDataSource datasource) {
    this.datasource = datasource;
  }

}
