package org.toobsframework.pres.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ThumbUtil {
  private static ThumbUtil instance = null;

  private ThumbUtil() {
  }
  
  public static ThumbUtil getInstance() {
    if (instance == null) {
      instance = new ThumbUtil();
    }
    return instance;
  }
  
  public boolean createThumbNail(
      File imageFile, 
      File thumbNailFile, 
      int thumbQuality, 
      int targetThumbWidth, 
      int targetThumbHeight)
    throws Exception {
    
    Image image = ImageIO.read(imageFile);
    
    int actualThumbWidth = targetThumbWidth;
    int actualThumbHeight = targetThumbHeight;
    double thumbRatio = (double) targetThumbWidth / (double) targetThumbHeight;
    // Image dimensions.
    int imageWidth = image.getWidth(null);
    int imageHeight = image.getHeight(null);
    double imageRatio = (double) imageWidth / (double) imageHeight;
    // Image thumbnail logic.
    if (thumbRatio > imageRatio) {
      actualThumbHeight = (int) (targetThumbWidth / imageRatio);
    } else {
      actualThumbWidth = (int) (targetThumbHeight * imageRatio);
    }
    BufferedImage scaled = new BufferedImage(actualThumbWidth, actualThumbHeight, BufferedImage.TYPE_INT_BGR);   
    
    // Scale operation
    Image scaledImg = image.getScaledInstance(actualThumbWidth, actualThumbHeight, Image.SCALE_SMOOTH);
    Graphics2D bg = scaled.createGraphics();
    bg.drawImage(scaledImg, 0, 0, null);
    bg.dispose();   
    
    // Crop operation
    int xOffset = (actualThumbWidth - targetThumbWidth) / 2;
    int yOffset = (actualThumbHeight - targetThumbHeight) / 2;
    BufferedImage thumbImage = scaled.getSubimage(xOffset, yOffset, targetThumbWidth, targetThumbHeight);

    BufferedOutputStream out = null;
    boolean written = false;
    try {
      out = new BufferedOutputStream(new FileOutputStream(thumbNailFile));
      written = ImageIO.write(thumbImage, "jpg", out);
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException ioe) {}
    }

    return written;
    
  }
  
  public static void main(String args[]) throws Exception {
    /*
    String baseDir = System.getProperty("user.home") + "/thumbtest";

    String indexPage = "<html>\n";
    indexPage += "<head>\n";
    indexPage += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n";
    indexPage += "<title>Site Status</title>\n";
    indexPage += "</head>\n";
    indexPage += "<body>\n";
    indexPage += "<div>\n";
    indexPage += "<ul>\n";

    int dim = 96;
    File indexFile = new File(baseDir + "/index.htm");

    String fileName = "paddy";
    String bmpFilePath = baseDir + "/" + fileName + ".bmp";
    String jpgFilePath = baseDir + "/" + fileName + ".jpg";
    
    File bmpSource = new File(bmpFilePath);
    File jpgDest   = new File(jpgFilePath);
    
    boolean done = ThumbUtil.getInstance().createThumbNail(bmpSource, jpgDest, 100, dim, dim);
    indexPage += "<li>";
    indexPage += "<img src=\"" + fileName + ".jpg\"/>";
    indexPage += "<span>" + fileName + ".jpg</span>";
    indexPage += "</li>";
    
    fileName = "787621810_l";
    bmpFilePath = baseDir + "/" + fileName + ".gif";
    jpgFilePath = baseDir + "/" + fileName + ".jpg";
    
    bmpSource = new File(bmpFilePath);
    jpgDest   = new File(jpgFilePath);
    
    done = ThumbUtil.getInstance().createThumbNail(bmpSource, jpgDest, 100, dim, dim);
    indexPage += "<li>";
    indexPage += "<img src=\"" + fileName + ".jpg\"/>";
    indexPage += "<span>" + fileName + ".jpg</span>";
    indexPage += "</li>";
    
    //fileName = "newBrowse_snap";
    //bmpFilePath = baseDir + "/" + fileName + ".tiff";
    //jpgFilePath = baseDir + "/" + fileName + ".jpg";
    
    //bmpSource = new File(bmpFilePath);
    //jpgDest   = new File(jpgFilePath);
    
    done = ThumbUtil.getInstance().createThumbNail(bmpSource, jpgDest, 100, dim, dim);
    
    fileName = "3cb4ae70-95e0-1029-8bab-00304885b7c6";
    bmpFilePath = baseDir + "/" + fileName + ".bmp";
    jpgFilePath = baseDir + "/" + fileName + ".jpg";
    
    bmpSource = new File(bmpFilePath);
    jpgDest   = new File(jpgFilePath);
    
    done = ThumbUtil.getInstance().createThumbNail(bmpSource, jpgDest, 100, dim, dim);
    indexPage += "<li>";
    indexPage += "<img src=\"" + fileName + ".jpg\"/>";
    indexPage += "<span>" + fileName + ".jpg</span>";
    indexPage += "</li>";

    //fileName = "try_mac";
    //bmpFilePath = baseDir + "/" + fileName + ".tiff";
    //jpgFilePath = baseDir + "/" + fileName + "_t_to_j.jpg";
    
    //bmpSource = new File(bmpFilePath);
    //jpgDest   = new File(jpgFilePath);
    
    //done = ThumbUtil.getInstance().createThumbNail(bmpSource, jpgDest, 100, dim, dim);

    indexPage += "</ul>";
    indexPage += "</div>";
    //indexPage += "<iframe src=\"" + rgbFile + "\" height=\"800\" width=\"300\"/>";
    indexPage += "</body>";
    indexPage += "</html>";
    
    OutputStream os = new FileOutputStream(indexFile);
    os.write(indexPage.getBytes("UTF-8"));
    os.flush();
    os.close();
    */
  }

}
