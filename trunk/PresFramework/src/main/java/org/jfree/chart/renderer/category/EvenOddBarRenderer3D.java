package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * A renderer for bars with a 3D effect, for use with the 
 * {@link org.jfree.chart.plot.CategoryPlot} class.
 */
public class EvenOddBarRenderer3D extends BarRenderer3D  {

    private boolean even = true;
    
    /**
     * Default constructor, creates a renderer with a default '3D effect'.
     */
    public EvenOddBarRenderer3D(boolean even) {
        super();
        this.even = even;
    }

    /**
     * Constructs a new renderer with the specified '3D effect'.
     *
     * @param xOffset  the x-offset for the 3D effect.
     * @param yOffset  the y-offset for the 3D effect.
     */
    public EvenOddBarRenderer3D(boolean even, double xOffset, double yOffset) {
        super(xOffset, yOffset);
        this.even = even;
    }


    /**
     * Calculates the coordinate of the first "side" of a bar.  This will be 
     * the minimum x-coordinate for a vertical bar, and the minimum 
     * y-coordinate for a horizontal bar.
     *
     * @param plot  the plot.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param state  the renderer state (has the bar width precalculated).
     * @param row  the row index.
     * @param column  the column index.
     * 
     * @return The coordinate.
     */
    protected double calculateBarW0(CategoryPlot plot, 
                                    PlotOrientation orientation, 
                                    Rectangle2D dataArea,
                                    CategoryAxis domainAxis,
                                    CategoryItemRendererState state,
                                    int row,
                                    int column) {
        // calculate bar width...
        double space = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            space = dataArea.getHeight();
        }
        else {
            space = dataArea.getWidth();
        }
        double barW0 = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = space * getItemMargin() / (categoryCount * (seriesCount - 1));
            double seriesW = calculateSeriesWidth(space, domainAxis, categoryCount, seriesCount);
            if (even) {
              barW0 = barW0 + row * (seriesW + seriesGap) + (seriesW / 2.0) - (state.getBarWidth() / 2.0);
            } else {
              barW0 = barW0 + row * (seriesW + seriesGap) + (seriesW / 2.0) + (seriesGap / 2.0);
            }
        }
        else {
          if (even) {
            barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
          } else {
            barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) + (state.getBarWidth() * 0.10);
          }
        }
        return barW0;
    }

    /**
     * Draws a 3D bar to represent one data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area for plotting the data.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column,
                         int pass) {
    
        // check the value we are plotting...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        
        double value = dataValue.doubleValue();
        
        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                dataArea.getY() + getYOffset(), 
                dataArea.getWidth() - getXOffset(), 
                dataArea.getHeight() - getYOffset());

        PlotOrientation orientation = plot.getOrientation();
        
        double barW0 = calculateBarW0(plot, orientation, adjusted, domainAxis, state, row, column);
        
        double[] barL0L1 = calculateBarL0L1(value);
        if (barL0L1 == null) {
            return;  // the bar is not visible
        }

        RectangleEdge edge = plot.getRangeAxisEdge();
        double transL0 = rangeAxis.valueToJava2D(barL0L1[0], adjusted, edge);
        double transL1 = rangeAxis.valueToJava2D(barL0L1[1], adjusted, edge);
        double barL0 = Math.min(transL0, transL1);
        double barLength = Math.abs(transL1 - transL0);
        
        // draw the bar...
        Rectangle2D bar = null;
        double barWidth;
        if (orientation == PlotOrientation.HORIZONTAL) {
            barWidth = (state.getBarWidth() / 2) - (state.getBarWidth() * 0.05);
            bar = new Rectangle2D.Double(barL0, barW0, barLength, barWidth);
        }
        else {
          barWidth = (state.getBarWidth() / 2) - (state.getBarWidth() * 0.10);
            bar = new Rectangle2D.Double(barW0, barL0, barWidth, barLength);
        }
        Paint itemPaint = getItemPaint(row, column);
        g2.setPaint(itemPaint);
        g2.fill(bar);

        double x0 = bar.getMinX();
        double x1 = x0 + getXOffset();
        double x2 = bar.getMaxX();
        double x3 = x2 + getXOffset();
        
        double y0 = bar.getMinY() - getYOffset();
        double y1 = bar.getMinY();
        double y2 = bar.getMaxY() - getYOffset();
        double y3 = bar.getMaxY();
        
        GeneralPath bar3dRight = null;
        GeneralPath bar3dTop = null;
        if (barLength > 0.0) {
            bar3dRight = new GeneralPath();
            bar3dRight.moveTo((float) x2, (float) y3);
            bar3dRight.lineTo((float) x2, (float) y1);
            bar3dRight.lineTo((float) x3, (float) y0);
            bar3dRight.lineTo((float) x3, (float) y2);
            bar3dRight.closePath();

            if (itemPaint instanceof Color) {
                g2.setPaint(((Color) itemPaint).darker());
            }
            g2.fill(bar3dRight);
        }

        bar3dTop = new GeneralPath();
        bar3dTop.moveTo((float) x0, (float) y1);
        bar3dTop.lineTo((float) x1, (float) y0);
        bar3dTop.lineTo((float) x3, (float) y0);
        bar3dTop.lineTo((float) x2, (float) y1);
        bar3dTop.closePath();
        g2.fill(bar3dTop);

        if (isDrawBarOutline() 
                && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            g2.setStroke(getItemOutlineStroke(row, column));
            g2.setPaint(getItemOutlinePaint(row, column));
            g2.draw(bar);
            if (bar3dRight != null) {
                g2.draw(bar3dRight);
            }
            if (bar3dTop != null) {
                g2.draw(bar3dTop);
            }
        }

        CategoryItemLabelGenerator generator 
            = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar, 
                    (value < 0.0));
        }        

        // add an item entity, if this information is being collected
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            GeneralPath barOutline = new GeneralPath();
            barOutline.moveTo((float) x0, (float) y3);
            barOutline.lineTo((float) x0, (float) y1);
            barOutline.lineTo((float) x1, (float) y0);
            barOutline.lineTo((float) x3, (float) y0);
            barOutline.lineTo((float) x3, (float) y2);
            barOutline.lineTo((float) x2, (float) y3);
            barOutline.closePath();
            addItemEntity(entities, dataset, row, column, barOutline);
        }

    }
}
