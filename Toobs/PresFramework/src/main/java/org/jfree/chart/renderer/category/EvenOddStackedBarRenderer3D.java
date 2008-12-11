package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DataUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.PublicCloneable;

/**
 * Renders stacked bars with 3D-effect, for use with the 
 * {@link org.jfree.chart.plot.CategoryPlot} class.
 */
public class EvenOddStackedBarRenderer3D extends StackedBarRenderer3D  {

    private boolean even = true;
    
    public EvenOddStackedBarRenderer3D(boolean even) {
        this(even, false);
    }

    /**
     * Constructs a new renderer with the specified '3D effect'.
     *
     * @param xOffset  the x-offset for the 3D effect.
     * @param yOffset  the y-offset for the 3D effect.
     */
    public EvenOddStackedBarRenderer3D(boolean even, double xOffset, double yOffset) {
        this(even, xOffset, yOffset, false);
    }
    
    /**
     * Creates a new renderer.
     * 
     * @param renderAsPercentages  a flag that controls whether the data values
     *                             are rendered as percentages.
     * 
     * @since 1.0.2
     */
    public EvenOddStackedBarRenderer3D(boolean even, boolean renderAsPercentages) {
        super(renderAsPercentages);
        this.even = even;
    }
    
    /**
     * Constructs a new renderer with the specified '3D effect'.
     *
     * @param xOffset  the x-offset for the 3D effect.
     * @param yOffset  the y-offset for the 3D effect.
     * @param renderAsPercentages  a flag that controls whether the data values
     *                             are rendered as percentages.
     * 
     * @since 1.0.2
     */
    public EvenOddStackedBarRenderer3D(boolean even, double xOffset, double yOffset, 
            boolean renderAsPercentages) {
        super(xOffset, yOffset, renderAsPercentages);
    }
    
    /**
     * Draws a stack of bars for one category, with a horizontal orientation.
     * 
     * @param values  the value list.
     * @param category  the category.
     * @param g2  the graphics device.
     * @param state  the state.
     * @param dataArea  the data area (adjusted for the 3D effect).
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     *
     * @since 1.0.4
     */
    protected void drawStackHorizontal(List values, Comparable category, 
            Graphics2D g2, CategoryItemRendererState state, 
            Rectangle2D dataArea, CategoryPlot plot, 
            CategoryAxis domainAxis, ValueAxis rangeAxis, 
            CategoryDataset dataset) {
        
        int column = dataset.getColumnIndex(category);
        double barX0;
        if (even) {
          barX0 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        } else {
          barX0 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge()) + (state.getBarWidth() * 0.05);
        }
        
        double barW = (state.getBarWidth() / 2) - (state.getBarWidth() * 0.05);
        
        // a list to store the series index and bar region, so we can draw
        // all the labels at the end...
        List itemLabelList = new ArrayList();
        
        // draw the blocks
        boolean inverted = rangeAxis.isInverted();
        int blockCount = values.size() - 1;
        for (int k = 0; k < blockCount; k++) {
            int index = (inverted ? blockCount - k - 1 : k);
            Object[] prev = (Object[]) values.get(index);
            Object[] curr = (Object[]) values.get(index + 1);
            int series = 0;
            if (curr[0] == null) {
                series = -((Integer) prev[0]).intValue();
            }
            else {
                series = ((Integer) curr[0]).intValue();
                if (series < 0) {
                    series = -((Integer) prev[0]).intValue();
                }
            }
            double v0 = ((Double) prev[1]).doubleValue();
            double vv0 = rangeAxis.valueToJava2D(v0, dataArea, 
                    plot.getRangeAxisEdge());

            double v1 = ((Double) curr[1]).doubleValue();
            double vv1 = rangeAxis.valueToJava2D(v1, dataArea, 
                    plot.getRangeAxisEdge());

            Shape[] faces = createHorizontalBlock(barX0, barW, vv0, vv1, 
                    inverted);
            Paint fillPaint = getItemPaint(series, column);
            Paint outlinePaint = getItemOutlinePaint(series, column);
            g2.setStroke(getItemOutlineStroke(series, column));
            
            for (int f = 0; f < 6; f++) {
                g2.setPaint(fillPaint);
                g2.fill(faces[f]);
                g2.setPaint(outlinePaint);
                g2.draw(faces[f]); 
            }
                        
            itemLabelList.add(new Object[] {new Integer(series), 
                    faces[5].getBounds2D(), 
                    BooleanUtilities.valueOf(v0 < getBase())});

            // add an item entity, if this information is being collected
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, series, column, faces[5]);
            }

        }        

        for (int i = 0; i < itemLabelList.size(); i++) {
            Object[] record = (Object[]) itemLabelList.get(i);
            int series = ((Integer) record[0]).intValue();
            Rectangle2D bar = (Rectangle2D) record[1];
            boolean neg = ((Boolean) record[2]).booleanValue();
            CategoryItemLabelGenerator generator 
                    = getItemLabelGenerator(series, column);
            if (generator != null && isItemLabelVisible(series, column)) {
                drawItemLabel(g2, dataset, series, column, plot, generator, 
                        bar, neg);
            }

        }
    }
    
    /**
     * Creates an array of shapes representing the six sides of a block in a
     * horizontal stack.
     * 
     * @param x0  left edge of bar (in Java2D space).
     * @param width  the width of the bar (in Java2D units).
     * @param y0  the base of the block (in Java2D space).
     * @param y1  the top of the block (in Java2D space).
     * @param inverted  a flag indicating whether or not the block is inverted
     *     (this changes the order of the faces of the block).
     * 
     * @return The sides of the block.
     */
    private Shape[] createHorizontalBlock(double x0, double width, double y0, 
            double y1, boolean inverted) {
        Shape[] result = new Shape[6];
        Point2D p00 = new Point2D.Double(y0, x0);
        Point2D p01 = new Point2D.Double(y0, x0 + width);
        Point2D p02 = new Point2D.Double(p01.getX() + getXOffset(), 
                p01.getY() - getYOffset());
        Point2D p03 = new Point2D.Double(p00.getX() + getXOffset(), 
                p00.getY() - getYOffset());

        Point2D p0 = new Point2D.Double(y1, x0);
        Point2D p1 = new Point2D.Double(y1, x0 + width);
        Point2D p2 = new Point2D.Double(p1.getX() + getXOffset(), 
                p1.getY() - getYOffset());
        Point2D p3 = new Point2D.Double(p0.getX() + getXOffset(), 
                p0.getY() - getYOffset());
        
        GeneralPath bottom = new GeneralPath();
        bottom.moveTo((float) p1.getX(), (float) p1.getY());
        bottom.lineTo((float) p01.getX(), (float) p01.getY());
        bottom.lineTo((float) p02.getX(), (float) p02.getY());
        bottom.lineTo((float) p2.getX(), (float) p2.getY());
        bottom.closePath();
        
        GeneralPath top = new GeneralPath();
        top.moveTo((float) p0.getX(), (float) p0.getY());
        top.lineTo((float) p00.getX(), (float) p00.getY());
        top.lineTo((float) p03.getX(), (float) p03.getY());
        top.lineTo((float) p3.getX(), (float) p3.getY());
        top.closePath();

        GeneralPath back = new GeneralPath();
        back.moveTo((float) p2.getX(), (float) p2.getY());
        back.lineTo((float) p02.getX(), (float) p02.getY());
        back.lineTo((float) p03.getX(), (float) p03.getY());
        back.lineTo((float) p3.getX(), (float) p3.getY());
        back.closePath();
        
        GeneralPath front = new GeneralPath();
        front.moveTo((float) p0.getX(), (float) p0.getY());
        front.lineTo((float) p1.getX(), (float) p1.getY());
        front.lineTo((float) p01.getX(), (float) p01.getY());
        front.lineTo((float) p00.getX(), (float) p00.getY());
        front.closePath();

        GeneralPath left = new GeneralPath();
        left.moveTo((float) p0.getX(), (float) p0.getY());
        left.lineTo((float) p1.getX(), (float) p1.getY());
        left.lineTo((float) p2.getX(), (float) p2.getY());
        left.lineTo((float) p3.getX(), (float) p3.getY());
        left.closePath();
        
        GeneralPath right = new GeneralPath();
        right.moveTo((float) p00.getX(), (float) p00.getY());
        right.lineTo((float) p01.getX(), (float) p01.getY());
        right.lineTo((float) p02.getX(), (float) p02.getY());
        right.lineTo((float) p03.getX(), (float) p03.getY());
        right.closePath();
        result[0] = bottom;
        result[1] = back;
        if (inverted) {
            result[2] = right;
            result[3] = left;
        }
        else {
            result[2] = left;
            result[3] = right;
        }
        result[4] = top;
        result[5] = front;
        return result;
    }

    /**
     * Draws a stack of bars for one category, with a vertical orientation.
     * 
     * @param values  the value list.
     * @param category  the category.
     * @param g2  the graphics device.
     * @param state  the state.
     * @param dataArea  the data area (adjusted for the 3D effect).
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     *
     * @since 1.0.4
     */
    protected void drawStackVertical(List values, Comparable category, 
            Graphics2D g2, CategoryItemRendererState state, 
            Rectangle2D dataArea, CategoryPlot plot, 
            CategoryAxis domainAxis, ValueAxis rangeAxis, 
            CategoryDataset dataset) {
        
        int column = dataset.getColumnIndex(category);
        double barX0;
        if (even) {
          barX0 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        } else {
          barX0 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge()) + (state.getBarWidth() * 0.10);
        }
        
        double barW = (state.getBarWidth() / 2) - (state.getBarWidth() * 0.10);

        // a list to store the series index and bar region, so we can draw
        // all the labels at the end...
        List itemLabelList = new ArrayList();
        
        // draw the blocks
        boolean inverted = rangeAxis.isInverted();
        int blockCount = values.size() - 1;
        for (int k = 0; k < blockCount; k++) {
            int index = (inverted ? blockCount - k - 1 : k);
            Object[] prev = (Object[]) values.get(index);
            Object[] curr = (Object[]) values.get(index + 1);
            int series = 0;
            if (curr[0] == null) {
                series = -((Integer) prev[0]).intValue();
            }
            else {
                series = ((Integer) curr[0]).intValue();
                if (series < 0) {
                    series = -((Integer) prev[0]).intValue();
                }
            }
            double v0 = ((Double) prev[1]).doubleValue();
            double vv0 = rangeAxis.valueToJava2D(v0, dataArea, 
                    plot.getRangeAxisEdge());

            double v1 = ((Double) curr[1]).doubleValue();
            double vv1 = rangeAxis.valueToJava2D(v1, dataArea, 
                    plot.getRangeAxisEdge());
            
            Shape[] faces = createVerticalBlock(barX0, barW, vv0, vv1, 
                    inverted);
            Paint fillPaint = getItemPaint(series, column);
            Paint outlinePaint = getItemOutlinePaint(series, column);
            g2.setStroke(getItemOutlineStroke(series, column));
            
            for (int f = 0; f < 6; f++) {
                g2.setPaint(fillPaint);
                g2.fill(faces[f]);
                g2.setPaint(outlinePaint);
                g2.draw(faces[f]); 
            }

            itemLabelList.add(new Object[] {new Integer(series), 
                    faces[5].getBounds2D(), 
                    BooleanUtilities.valueOf(v0 < getBase())});
            
            // add an item entity, if this information is being collected
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, series, column, faces[5]);
            }

        }
        
        for (int i = 0; i < itemLabelList.size(); i++) {
            Object[] record = (Object[]) itemLabelList.get(i);
            int series = ((Integer) record[0]).intValue();
            Rectangle2D bar = (Rectangle2D) record[1];
            boolean neg = ((Boolean) record[2]).booleanValue();
            CategoryItemLabelGenerator generator 
                    = getItemLabelGenerator(series, column);
            if (generator != null && isItemLabelVisible(series, column)) {
                drawItemLabel(g2, dataset, series, column, plot, generator, 
                        bar, neg);
            }

        }
    }
    
    /**
     * Creates an array of shapes representing the six sides of a block in a
     * vertical stack.
     * 
     * @param x0  left edge of bar (in Java2D space).
     * @param width  the width of the bar (in Java2D units).
     * @param y0  the base of the block (in Java2D space).
     * @param y1  the top of the block (in Java2D space).
     * @param inverted  a flag indicating whether or not the block is inverted
     *     (this changes the order of the faces of the block).
     * 
     * @return The sides of the block.
     */
    private Shape[] createVerticalBlock(double x0, double width, double y0, 
            double y1, boolean inverted) {
        Shape[] result = new Shape[6];
        Point2D p00 = new Point2D.Double(x0, y0);
        Point2D p01 = new Point2D.Double(x0 + width, y0);
        Point2D p02 = new Point2D.Double(p01.getX() + getXOffset(), 
                p01.getY() - getYOffset());
        Point2D p03 = new Point2D.Double(p00.getX() + getXOffset(), 
                p00.getY() - getYOffset());


        Point2D p0 = new Point2D.Double(x0, y1);
        Point2D p1 = new Point2D.Double(x0 + width, y1);
        Point2D p2 = new Point2D.Double(p1.getX() + getXOffset(), 
                p1.getY() - getYOffset());
        Point2D p3 = new Point2D.Double(p0.getX() + getXOffset(), 
                p0.getY() - getYOffset());
        
        GeneralPath right = new GeneralPath();
        right.moveTo((float) p1.getX(), (float) p1.getY());
        right.lineTo((float) p01.getX(), (float) p01.getY());
        right.lineTo((float) p02.getX(), (float) p02.getY());
        right.lineTo((float) p2.getX(), (float) p2.getY());
        right.closePath();
        
        GeneralPath left = new GeneralPath();
        left.moveTo((float) p0.getX(), (float) p0.getY());
        left.lineTo((float) p00.getX(), (float) p00.getY());
        left.lineTo((float) p03.getX(), (float) p03.getY());
        left.lineTo((float) p3.getX(), (float) p3.getY());
        left.closePath();

        GeneralPath back = new GeneralPath();
        back.moveTo((float) p2.getX(), (float) p2.getY());
        back.lineTo((float) p02.getX(), (float) p02.getY());
        back.lineTo((float) p03.getX(), (float) p03.getY());
        back.lineTo((float) p3.getX(), (float) p3.getY());
        back.closePath();
        
        GeneralPath front = new GeneralPath();
        front.moveTo((float) p0.getX(), (float) p0.getY());
        front.lineTo((float) p1.getX(), (float) p1.getY());
        front.lineTo((float) p01.getX(), (float) p01.getY());
        front.lineTo((float) p00.getX(), (float) p00.getY());
        front.closePath();

        GeneralPath top = new GeneralPath();
        top.moveTo((float) p0.getX(), (float) p0.getY());
        top.lineTo((float) p1.getX(), (float) p1.getY());
        top.lineTo((float) p2.getX(), (float) p2.getY());
        top.lineTo((float) p3.getX(), (float) p3.getY());
        top.closePath();
        
        GeneralPath bottom = new GeneralPath();
        bottom.moveTo((float) p00.getX(), (float) p00.getY());
        bottom.lineTo((float) p01.getX(), (float) p01.getY());
        bottom.lineTo((float) p02.getX(), (float) p02.getY());
        bottom.lineTo((float) p03.getX(), (float) p03.getY());
        bottom.closePath();
        
        result[0] = bottom;
        result[1] = back;
        result[2] = left;
        result[3] = right;
        result[4] = top;
        result[5] = front;
        if (inverted) {
            result[0] = top;
            result[4] = bottom;
        }
        return result;
    }
    
}
