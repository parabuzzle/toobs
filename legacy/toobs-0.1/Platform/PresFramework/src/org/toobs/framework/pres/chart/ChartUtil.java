package org.toobs.framework.pres.chart;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.EvenOddBarRenderer;
import org.jfree.chart.renderer.category.EvenOddBarRenderer3D;
import org.jfree.chart.renderer.category.EvenOddStackedBarRenderer;
import org.jfree.chart.renderer.category.EvenOddStackedBarRenderer3D;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.SortOrder;
import org.toobs.data.beanutil.BeanMonkey;
import org.toobs.framework.pres.chart.config.BasePlot;
import org.toobs.framework.pres.chart.config.DataElement;
import org.toobs.framework.pres.chart.config.Dataset;
import org.toobs.framework.pres.chart.config.DatasetGroup;
import org.toobs.framework.pres.chart.config.DatasetSeries;
import org.toobs.framework.pres.chart.config.DomainAxisDef;
import org.toobs.framework.pres.chart.config.Legend;
import org.toobs.framework.pres.chart.config.RangeAxisDef;
import org.toobs.framework.pres.chart.config.RectangleSet;
import org.toobs.framework.pres.chart.config.TextLabel;
import org.toobs.framework.pres.chart.config.types.SubtitlePositionType;
import org.toobs.framework.pres.chart.config.types.SubtitleVerticalAlignmentType;
import org.toobs.framework.pres.util.ParameterUtil;


//@SuppressWarnings("unchecked")
public class ChartUtil {

  private static Log log = LogFactory.getLog(ChartUtil.class);
  
  public static final int RENDERER_TYPE_AREA = 0;
  public static final int RENDERER_TYPE_BAR = 1;
  public static final int RENDERER_TYPE_LINE = 2;
  public static final int RENDERER_TYPE_PIE = 3;
  public static final int RENDERER_TYPE_BAR_EVEN_ODD = 4;

  public static final int PLOT_CATEGORY_TYPE = 0;
  public static final int PLOT_XY_TYPE = 1;
  public static final int PLOT_COMBINEDDOMAINCATEGORY_TYPE = 2; 
  public static final int PLOT_COMBINEDRANGECATEGORY_TYPE = 3;
  public static final int PLOT_MULTICATEGORY_TYPE = 4;
  public static final int PLOT_SPIDER_TYPE = 5;

  public static final int DOM_LABEL_STANDARD_TYPE = 0;
  public static final int DOM_LABEL_UP_45_TYPE = 1;
  public static final int DOM_LABEL_DOWN_45_TYPE = 2;
  public static final int DOM_LABEL_UP_90_TYPE = 3;
  public static final int DOM_LABEL_DOWN_90_TYPE = 4;
  
  private static Map<String, Integer> supportedPlots;
  static {
    supportedPlots = new HashMap<String, Integer>();
    supportedPlots.put("category", PLOT_CATEGORY_TYPE );
    supportedPlots.put("xy", PLOT_XY_TYPE );
    supportedPlots.put("combinedDomainCategory", PLOT_COMBINEDDOMAINCATEGORY_TYPE );
    supportedPlots.put("combinedRangeCategory", PLOT_COMBINEDRANGECATEGORY_TYPE );
    supportedPlots.put("multiCategory", PLOT_MULTICATEGORY_TYPE );
    supportedPlots.put("spider", PLOT_SPIDER_TYPE );
  }
  private static Map<String, Integer> supportedRenderers;
  static {
    supportedRenderers = new HashMap<String, Integer>();
    supportedRenderers.put("area", RENDERER_TYPE_AREA );
    supportedRenderers.put("bar", RENDERER_TYPE_BAR );
    supportedRenderers.put("barEvenOdd", RENDERER_TYPE_BAR_EVEN_ODD );
    supportedRenderers.put("lineAndShape", RENDERER_TYPE_LINE );
    supportedRenderers.put("pie", RENDERER_TYPE_PIE );
  }
  private static Map<String, AxisLocation> axisLocations;
  static {
    axisLocations = new HashMap<String, AxisLocation>();
    axisLocations.put("bottomOrLeft", AxisLocation.BOTTOM_OR_LEFT );
    axisLocations.put("bottomOrRight", AxisLocation.BOTTOM_OR_RIGHT );
    axisLocations.put("topOrLeft", AxisLocation.TOP_OR_LEFT );
    axisLocations.put("topOrRight", AxisLocation.TOP_OR_RIGHT );
  }
  private static Map<String, Integer> domainLabelPositions;
  static {
    domainLabelPositions = new HashMap<String, Integer>();
    domainLabelPositions.put("standard", DOM_LABEL_STANDARD_TYPE );
    domainLabelPositions.put("up45", DOM_LABEL_UP_45_TYPE );
    domainLabelPositions.put("down45", DOM_LABEL_DOWN_45_TYPE );
    domainLabelPositions.put("up90", DOM_LABEL_UP_90_TYPE );
    domainLabelPositions.put("down90", DOM_LABEL_DOWN_90_TYPE );
  }
  
  public static void configureLegend(JFreeChart chart, Legend legend, Map params) {
    if (legend != null) {
      if (legend.getBorder() != null) {
        RectangleInsets rect = getRectangle(legend.getBorder(), params);
        chart.getLegend().setBorder(rect.getTop(), rect.getLeft(), rect.getBottom(), rect.getRight());
      }
      if (legend.getPadding() != null) {
        chart.getLegend().setItemLabelPadding(getRectangle(legend.getPadding(), params));
      }
    }
  }

  public static VerticalAlignment getVerticalAlignment(SubtitleVerticalAlignmentType verticalAlignment) {
    VerticalAlignment valign = null;
    switch (verticalAlignment.getType()) {
      case SubtitleVerticalAlignmentType.TOP_TYPE:
       valign = VerticalAlignment.TOP;
       break;
      case SubtitleVerticalAlignmentType.BOTTOM_TYPE:
        valign = VerticalAlignment.BOTTOM;
        break;
      case SubtitleVerticalAlignmentType.CENTER_TYPE:
        valign = VerticalAlignment.CENTER;
        break;
    }
    return valign;
  }

  public static RectangleEdge getPosition(SubtitlePositionType position) {
    RectangleEdge edge = null;
    switch (position.getType()) {
      case SubtitlePositionType.TOP_TYPE:
       edge = RectangleEdge.TOP;
       break;
      case SubtitlePositionType.BOTTOM_TYPE:
        edge = RectangleEdge.BOTTOM;
        break;
      case SubtitlePositionType.LEFT_TYPE:
        edge = RectangleEdge.LEFT;
        break;
      case SubtitlePositionType.RIGHT_TYPE:
        edge = RectangleEdge.RIGHT;
        break;
    }
    return edge;
  }
  
  public static String evaluateTextLabel(TextLabel textLabel, Map params) {
    if (textLabel == null || textLabel.getPath() == null) return null;
    
    if (textLabel.getIsStatic()) {
      return ParameterUtil.resolveParam(textLabel.getPath(), params, textLabel.getDefault())[0];
    }
    JXPathContext context = JXPathContext.newContext(params);
    String labelText = (String)context.getValue(textLabel.getPath());
    if (labelText == null & textLabel.getDefault() != null) {
      labelText = textLabel.getDefault();
    }
    
    return labelText;
  }

  public static java.awt.Font getFont(TextLabel textLabel, java.awt.Font defaultFont) {
    if (textLabel != null && textLabel.getFont() != null) {
      return new java.awt.Font(
          textLabel.getFont().getName(), textLabel.getFont().getStyle().getType(), textLabel.getFont().getSize());
    } else {
      return defaultFont;
    }
  }

  public static Color getColor(String color) {
    return new Color(Integer.parseInt(color, 16));
  }

  public static RectangleInsets getRectangle(RectangleSet rectangleSet, Map params) {
    double top = Double.parseDouble( ParameterUtil.resolveParam(rectangleSet.getTop(), params, "0.0")[0] );
    double bot = Double.parseDouble( ParameterUtil.resolveParam(rectangleSet.getBottom(), params, "0.0")[0] );
    double lef = Double.parseDouble( ParameterUtil.resolveParam(rectangleSet.getLeft(), params, "0.0")[0] );
    double rit = Double.parseDouble( ParameterUtil.resolveParam(rectangleSet.getRight(), params, "0.0")[0] );
    return new RectangleInsets(top, lef, bot, rit);
  }
  
  public static Object getDatasetValue(Object datasetRow, DataElement dataElement, Object defaultValue) throws ChartException {
    try {
      if (dataElement != null && dataElement.getIsStatic()) {
        return dataElement.getPath();
      } else if (dataElement != null) {
        Object value = null;
        value = BeanMonkey.getPropertyValue(datasetRow, dataElement.getPath());
        if (value == null) {
          value = defaultValue;
        }
        return value;
      } else {
        return defaultValue;
      }
    } catch (IllegalAccessException e) {
      log.error("Chart data search exception " + e.getMessage(), e);
      throw new ChartException(e);
    } catch (InvocationTargetException e) {
      log.error("Chart data search exception " + e.getMessage(), e);
      throw new ChartException(e);
    } catch (NoSuchMethodException e) {
      log.error("Chart data search exception " + e.getMessage(), e);
      throw new ChartException(e);
    }
  }
  
  public static CategoryAxis createCategoryAxis(DomainAxisDef categoryAxisDef, Map params, boolean is3D) {
    CategoryAxis categoryAxis;
    if (is3D) {
      categoryAxis = new CategoryAxis3D();
    } else {
      categoryAxis = new CategoryAxis();
    }
    if (categoryAxisDef != null) {
      if (categoryAxisDef.getDomainLabel() != null) {
        categoryAxis.setLabel(evaluateTextLabel(categoryAxisDef.getDomainLabel(), params));
        if (categoryAxisDef.getDomainLabel().getFont() != null) {
          categoryAxis.setLabelFont(getFont(categoryAxisDef.getDomainLabel(), null));
        }
        categoryAxis.setLabelPaint(getColor(categoryAxisDef.getDomainLabel().getColor()));
      }
      
      if (categoryAxisDef.getLabelPosition() != null) {
        Integer labelPos = domainLabelPositions.get(ParameterUtil.resolveParam(categoryAxisDef.getLabelPosition(), params, "standard")[0]);
        if (labelPos != null) {
          switch (labelPos) {
          case DOM_LABEL_STANDARD_TYPE:
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
            break;
          case DOM_LABEL_UP_45_TYPE:
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            break;
          case DOM_LABEL_DOWN_45_TYPE:
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
            break;
          case DOM_LABEL_UP_90_TYPE:
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
            break;
          case DOM_LABEL_DOWN_90_TYPE:
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
            break;
          }
        }
      }
      double domainMargin = Double.parseDouble( ParameterUtil.resolveParam(categoryAxisDef.getDomainMargin(), params, "0.0")[0] );
      double lowerMargin = Double.parseDouble( ParameterUtil.resolveParam(categoryAxisDef.getLowerMargin(), params, "0.0")[0] );
      double upperMargin = Double.parseDouble( ParameterUtil.resolveParam(categoryAxisDef.getUpperMargin(), params, "0.0")[0] );

      categoryAxis.setCategoryMargin(domainMargin);
      categoryAxis.setLowerMargin(lowerMargin);
      categoryAxis.setUpperMargin(upperMargin);  
    }
    return categoryAxis;
  }

  public static void configurePlot(Plot plot, org.toobs.framework.pres.chart.config.BasePlot plotDef, DomainAxisDef categoryAxisDef, RangeAxisDef valueAxisDef, Map params) {
    if (plotDef != null) {
      float foregroundAlpha = Float.parseFloat( ParameterUtil.resolveParam(plotDef.getForegroundAlpha(), params, "1.0")[0] );
      plot.setForegroundAlpha(foregroundAlpha);

      if (plot instanceof CategoryPlot && ParameterUtil.resolveParam(plotDef.getOrientation(), params, "horizontal")[0].equals("horizontal")) {
        ((CategoryPlot)plot).setOrientation(PlotOrientation.HORIZONTAL);
        boolean is3D = (ParameterUtil.resolveParam(plotDef.getIs3D(), params, "false")[0].equals("false") ? false : true);
        if (is3D) {
          ((CategoryPlot)plot).setRowRenderingOrder(SortOrder.DESCENDING);
          ((CategoryPlot)plot).setColumnRenderingOrder(SortOrder.DESCENDING);
        }
      } else if (plot instanceof CategoryPlot) {
        ((CategoryPlot)plot).setOrientation(PlotOrientation.VERTICAL);
      }

      if (plotDef.getInsets() != null) {
        plot.setInsets(getRectangle(plotDef.getInsets(), params));
      }
      if (plot instanceof CategoryPlot && plotDef.getAxisOffsets() != null) {
        ((CategoryPlot)plot).setAxisOffset(getRectangle(plotDef.getAxisOffsets(), params));
      }

      if (plotDef.getBackgroundColor() != null) {
        plot.setBackgroundPaint(getColor(plotDef.getBackgroundColor()));
      }
      if (plot instanceof CategoryPlot && categoryAxisDef != null) {
        AxisLocation dLabelPos = axisLocations.get(ParameterUtil.resolveParam(categoryAxisDef.getLocation(), params, "bottomOrLeft")[0]);
        if (dLabelPos != null) {
          ((CategoryPlot)plot).setDomainAxisLocation(dLabelPos);
        }
        ((CategoryPlot)plot).setDomainGridlinesVisible(categoryAxisDef.getShowGridlines());
        if (categoryAxisDef.getGridColor() != null) {
          ((CategoryPlot)plot).setDomainGridlinePaint(getColor(categoryAxisDef.getGridColor()));
        }
      }
      if (plot instanceof CategoryPlot && valueAxisDef != null) {
        AxisLocation rLabelPos = axisLocations.get(ParameterUtil.resolveParam(valueAxisDef.getLocation(), params, "bottomOrRight")[0]);
        if (rLabelPos != null) {
          ((CategoryPlot)plot).setRangeAxisLocation(rLabelPos);
        }
        ((CategoryPlot)plot).setRangeGridlinesVisible(valueAxisDef.getShowGridlines());
        if (valueAxisDef.getGridColor() != null) {
          ((CategoryPlot)plot).setRangeGridlinePaint(getColor(valueAxisDef.getGridColor()));
        }
      }
    }
  }

  public static AbstractRenderer getRenderer(BasePlot plot, DatasetGroup group, Map params) {
    AbstractRenderer renderer = null;
    
    String format = ParameterUtil.resolveParam(group.getRenderer(), params, "area")[0];
    boolean is3D = (ParameterUtil.resolveParam(plot.getIs3D(), params, "false")[0].equals("false") ? false : true);
    
    switch (supportedRenderers.get(format)) {
      case ChartUtil.RENDERER_TYPE_AREA:
        if (group.getStacked()) {
          renderer = new StackedAreaRenderer();
          if (group.getRenderAsPercentages()) {
            ((StackedAreaRenderer)renderer).setRenderAsPercentages(true);
          }
        } else {
          renderer = new AreaRenderer();
        }
        break;
      case ChartUtil.RENDERER_TYPE_BAR:
        if (is3D) {
          if (group.getStacked()) {
            renderer = new StackedBarRenderer3D();
            if (group.getRenderAsPercentages()) {
              ((StackedBarRenderer3D)renderer).setRenderAsPercentages(true);
            }
          } else {
            renderer = new BarRenderer3D();
          }
        } else {
          if (group.getStacked()) {
            renderer = new StackedBarRenderer();
            if (group.getRenderAsPercentages()) {
              ((StackedBarRenderer)renderer).setRenderAsPercentages(true);
            }
          } else {
            renderer = new BarRenderer();
          }
        }
        break;
      case ChartUtil.RENDERER_TYPE_BAR_EVEN_ODD:
        if (is3D) {
          if (group.getStacked()) {
            renderer = new EvenOddStackedBarRenderer3D(group.getEven());
            if (group.getRenderAsPercentages()) {
              ((EvenOddStackedBarRenderer3D)renderer).setRenderAsPercentages(true);
            }
          } else {
            renderer = new EvenOddBarRenderer3D(group.getEven());
          }
        } else {
          if (group.getStacked()) {
            renderer = new EvenOddStackedBarRenderer(group.getEven());
            if (group.getRenderAsPercentages()) {
              ((EvenOddStackedBarRenderer)renderer).setRenderAsPercentages(true);
            }
          } else {
            renderer = new EvenOddBarRenderer(group.getEven());
          }
        }
        break;
      case ChartUtil.RENDERER_TYPE_LINE:
        renderer = new LineAndShapeRenderer();
        break;
      case ChartUtil.RENDERER_TYPE_PIE:
        break;
    }
    configureDomainItemLabels(plot, renderer, params, is3D);
    configureSeriesPaint(plot, group, renderer);
    return renderer;
  }

  public static void configureDomainItemLabels(BasePlot plot, AbstractRenderer renderer, Map params, boolean is3D) {
    if (plot.getShowDomainItemLabels()) {
      String orientation = ParameterUtil.resolveParam(plot.getOrientation(), params, "horizontal")[0];
      if (orientation.equals("horizontal")) {
        ItemLabelPosition position1;
        if (is3D) {
          position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE2, TextAnchor.CENTER_LEFT);
          renderer.setItemLabelAnchorOffset(15.0d);
        } else {
          position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT);
        }
        renderer.setPositiveItemLabelPosition(position1);
        
        ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_RIGHT);
        renderer.setNegativeItemLabelPosition(position2);
      } else if (orientation.equals("vertical")) {
        ItemLabelPosition position1;
        if (is3D) {
          position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER);
          renderer.setItemLabelAnchorOffset(10.0d);
        } else {
          position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        }
        renderer.setPositiveItemLabelPosition(position1);
        
        ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        renderer.setNegativeItemLabelPosition(position2);
      }
      renderer.setBaseItemLabelsVisible(true);
      ((CategoryItemRenderer)renderer).setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    }
  }
  
  public static void configureSeriesPaint(org.toobs.framework.pres.chart.config.BasePlot plotDef, DatasetGroup datasetGroup, AbstractRenderer renderer) {
    for (int i = 0; i < datasetGroup.getDatasetCount(); i++) {
      Dataset dataset = datasetGroup.getDataset(i);
      for (int s = 0; s < dataset.getDatasetSeriesCount(); s++) {
        DatasetSeries series = dataset.getDatasetSeries(s); 
        if (series.getColor() != null) {
          renderer.setSeriesPaint(i + s, getColor(series.getColor()));
        }
      }
    }
  }

  public static Map<String, Integer> getSupportedPlots() {
    return supportedPlots;
  }
}
