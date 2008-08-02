package org.toobs.framework.pres.util;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MimeMap {

  private static Map mimeTypes;
  private static MimeMap instance;
  
  private MimeMap() {
    mimeTypes = new HashMap();
    mimeTypes.put("application/msword", new MimeType("application/msword", "doc", true, false));
    mimeTypes.put("application/vnd.ms-excel", new MimeType("application/vnd.ms-excel", "xls", true, false));
    mimeTypes.put("application/octet-stream", new MimeType("application/octet-stream", "bin", true, false));
    mimeTypes.put("application/pdf", new MimeType("application/pdf", "pdf", true, false));
    mimeTypes.put("application/postscript", new MimeType("application/postscript", "ps", true, false));
    mimeTypes.put("application/rtf", new MimeType("application/rtf", "rtf", true, false));
    mimeTypes.put("application/vnd.ms-powerpoint", new MimeType("application/vnd.ms-powerpoint", "ppt", true, false));
    mimeTypes.put("application/x-dvi", new MimeType("application/x-dvi", "dvi", true, false));
    mimeTypes.put("application/x-futuresplash", new MimeType("application/x-futuresplash", "spl", true, false));
    mimeTypes.put("application/x-gtar", new MimeType("application/x-gtar", "gtar", true, false));
    mimeTypes.put("application/x-hdf", new MimeType("application/x-hdf", "hdf", true, false));
    mimeTypes.put("application/x-javascript", new MimeType("application/x-javascript", "js", true, false));
    mimeTypes.put("application/x-shockwave-flash", new MimeType("application/x-shockwave-flash", "swf", true, false));
    mimeTypes.put("application/x-tar", new MimeType("application/x-tar", "tar", true, false));
    mimeTypes.put("application/zip", new MimeType("application/zip", "zip", true, false));
    mimeTypes.put("audio/midi", new MimeType("audio/midi", "midi", true, false));
    mimeTypes.put("audio/mpeg", new MimeType("audio/mpeg", "mp3", true, false));
    mimeTypes.put("audio/x-pn-realaudio", new MimeType("audio/x-pn-realaudio", "rm", true, false));
    mimeTypes.put("audio/x-pn-realaudio-plugin", new MimeType("audio/x-pn-realaudio-plugin", "rpm", true, false));
    mimeTypes.put("audio/x-realaudio", new MimeType("audio/x-realaudio", "ra", true, false));
    mimeTypes.put("audio/x-wav", new MimeType("audio/x-wav", "wav", true, false));
    mimeTypes.put("image/bmp", new MimeType("image/bmp", "bmp", true, true));
    mimeTypes.put("image/gif", new MimeType("image/gif", "gif", true, true));
    mimeTypes.put("image/ief", new MimeType("image/ief", "ief", true, true));
    mimeTypes.put("image/jpeg", new MimeType("image/jpeg", "jpg", true, true));
    mimeTypes.put("image/pjpeg", new MimeType("image/pjpeg", "jpg", true, true));
    mimeTypes.put("image/png", new MimeType("image/png", "png", true, true));
    mimeTypes.put("image/tiff", new MimeType("image/tiff", "tif", false, false));
    mimeTypes.put("image/x-rgb", new MimeType("image/x-rgb", "rgb", true, true));
    mimeTypes.put("image/x-xbitmap", new MimeType("image/x-xbitmap", "xbm", true, true));
    mimeTypes.put("text/css", new MimeType("text/css", "css", true, false));
    mimeTypes.put("text/html", new MimeType("text/html", "htm", true, false));
    mimeTypes.put("text/plain", new MimeType("text/plain", "txt", true, false));
    mimeTypes.put("text/richtext", new MimeType("text/richtext", "rtx", true, false));
    mimeTypes.put("text/rtf", new MimeType("text/rtf", "rtf", true, false));
    mimeTypes.put("text/sgml", new MimeType("text/sgml", "sgml", true, false));
    mimeTypes.put("text/tab-separated-values", new MimeType("text/tab-separated-values", "tsv", true, false));
    mimeTypes.put("text/xml", new MimeType("text/xml", "xml", true, false));
    mimeTypes.put("text/x-setext", new MimeType("text/x-setext", "etx", true, false));
    mimeTypes.put("video/mpeg", new MimeType("video/mpeg", "mpg", true, false));
    mimeTypes.put("video/quicktime", new MimeType("video/quicktime", "mov", true, false));
    mimeTypes.put("video/quicktime", new MimeType("video/quicktime", "qt", true, false));
    mimeTypes.put("video/x-msvideo", new MimeType("video/x-msvideo", "avi", true, false));
    mimeTypes.put("video/x-sgi-movie", new MimeType("video/x-sgi-movie", "movie", true, false));
    
    /*
    mimeTypes.put("application/andrew-inset", new MimeType("application/andrew-inset", "ez", false, false));
    mimeTypes.put("application/mac-binhex40", new MimeType("application/mac-binhex40", "hqx", false, false));
    mimeTypes.put("application/mac-compactpro", new MimeType("application/mac-compactpro", "cpt", false, false));
    mimeTypes.put("application/octet-stream", new MimeType("application/octet-stream", "class", false, false));
    mimeTypes.put("application/octet-stream", new MimeType("application/octet-stream", "dms", false, false));
    mimeTypes.put("application/octet-stream", new MimeType("application/octet-stream", "exe", false, false));
    mimeTypes.put("application/octet-stream", new MimeType("application/octet-stream", "lha", false, false));
    mimeTypes.put("application/octet-stream", new MimeType("application/octet-stream", "lzh", false, false));
    mimeTypes.put("application/oda", new MimeType("application/oda", "oda", false, false));
    mimeTypes.put("application/postscript", new MimeType("application/postscript", "ai", false, false));
    mimeTypes.put("application/postscript", new MimeType("application/postscript", "eps", false, false));
    mimeTypes.put("application/smil", new MimeType("application/smil", "smi", false, false));
    mimeTypes.put("application/smil", new MimeType("application/smil", "smil", false, false));
    mimeTypes.put("application/vnd.mif", new MimeType("application/vnd.mif", "mif", false, false));
    mimeTypes.put("application/x-bcpio", new MimeType("application/x-bcpio", "bcpio", false, false));
    mimeTypes.put("application/x-cdlink", new MimeType("application/x-cdlink", "vcd", false, false));
    mimeTypes.put("application/x-chess-pgn", new MimeType("application/x-chess-pgn", "pgn", false, false));
    mimeTypes.put("application/x-cpio", new MimeType("application/x-cpio", "cpio", false, false));
    mimeTypes.put("application/x-csh", new MimeType("application/x-csh", "csh", false, false));
    mimeTypes.put("application/x-director", new MimeType("application/x-director", "dcr", false, false));
    mimeTypes.put("application/x-director", new MimeType("application/x-director", "dir", false, false));
    mimeTypes.put("application/x-director", new MimeType("application/x-director", "dxr", false, false));
    mimeTypes.put("application/x-koan", new MimeType("application/x-koan", "skd", false, false));
    mimeTypes.put("application/x-koan", new MimeType("application/x-koan", "skm", false, false));
    mimeTypes.put("application/x-koan", new MimeType("application/x-koan", "skp", false, false));
    mimeTypes.put("application/x-koan", new MimeType("application/x-koan", "skt", false, false));
    mimeTypes.put("application/x-latex", new MimeType("application/x-latex", "latex", false, false));
    mimeTypes.put("application/x-netcdf", new MimeType("application/x-netcdf", "cdf", false, false));
    mimeTypes.put("application/x-netcdf", new MimeType("application/x-netcdf", "nc", false, false));
    mimeTypes.put("application/x-sh", new MimeType("application/x-sh", "sh", false, false));
    mimeTypes.put("application/x-shar", new MimeType("application/x-shar", "shar", false, false));
    mimeTypes.put("application/x-stuffit", new MimeType("application/x-stuffit", "sit", false, false));
    mimeTypes.put("application/x-sv4cpio", new MimeType("application/x-sv4cpio", "sv4cpio", false, false));
    mimeTypes.put("application/x-sv4crc", new MimeType("application/x-sv4crc", "sv4crc", false, false));
    mimeTypes.put("application/x-tcl", new MimeType("application/x-tcl", "tcl", false, false));
    mimeTypes.put("application/x-tex", new MimeType("application/x-tex", "tex", false, false));
    mimeTypes.put("application/x-texinfo", new MimeType("application/x-texinfo", "texi", false, false));
    mimeTypes.put("application/x-texinfo", new MimeType("application/x-texinfo", "texinfo", false, false));
    mimeTypes.put("application/x-troff", new MimeType("application/x-troff", "roff", false, false));
    mimeTypes.put("application/x-troff", new MimeType("application/x-troff", "t", false, false));
    mimeTypes.put("application/x-troff", new MimeType("application/x-troff", "tr", false, false));
    mimeTypes.put("application/x-troff-man", new MimeType("application/x-troff-man", "man", false, false));
    mimeTypes.put("application/x-troff-me", new MimeType("application/x-troff-me", "me", false, false));
    mimeTypes.put("application/x-troff-ms", new MimeType("application/x-troff-ms", "ms", false, false));
    mimeTypes.put("application/x-ustar", new MimeType("application/x-ustar", "ustar", false, false));
    mimeTypes.put("application/x-wais-source", new MimeType("application/x-wais-source", "src", false, false));
    mimeTypes.put("audio/basic", new MimeType("audio/basic", "au", false, false));
    mimeTypes.put("audio/basic", new MimeType("audio/basic", "snd", false, false));
    mimeTypes.put("audio/midi", new MimeType("audio/midi", "kar", false, false));
    mimeTypes.put("audio/midi", new MimeType("audio/midi", "mid", false, false));
    mimeTypes.put("audio/mpeg", new MimeType("audio/mpeg", "mp2", false, false));
    mimeTypes.put("audio/mpeg", new MimeType("audio/mpeg", "mpga", false, false));
    mimeTypes.put("audio/x-aiff", new MimeType("audio/x-aiff", "aif", false, false));
    mimeTypes.put("audio/x-aiff", new MimeType("audio/x-aiff", "aifc", false, false));
    mimeTypes.put("audio/x-aiff", new MimeType("audio/x-aiff", "aiff", false, false));
    mimeTypes.put("audio/x-pn-realaudio", new MimeType("audio/x-pn-realaudio", "ram", false, false));
    mimeTypes.put("chemical/x-pdb", new MimeType("chemical/x-pdb", "pdb", false, false));
    mimeTypes.put("chemical/x-pdb", new MimeType("chemical/x-pdb", "xyz", false, false));
    mimeTypes.put("image/jpeg", new MimeType("image/jpeg", "jpe", false, false));
    mimeTypes.put("image/jpeg", new MimeType("image/jpeg", "jpeg", false, false));
    mimeTypes.put("image/tiff", new MimeType("image/tiff", "tiff", false, false));
    mimeTypes.put("image/x-cmu-raster", new MimeType("image/x-cmu-raster", "ras", false, false));
    mimeTypes.put("image/x-portable-anymap", new MimeType("image/x-portable-anymap", "pnm", false, false));
    mimeTypes.put("image/x-portable-bitmap", new MimeType("image/x-portable-bitmap", "pbm", false, false));
    mimeTypes.put("image/x-portable-graymap", new MimeType("image/x-portable-graymap", "pgm", false, false));
    mimeTypes.put("image/x-portable-pixmap", new MimeType("image/x-portable-pixmap", "ppm", false, false));
    mimeTypes.put("image/x-xpixmap", new MimeType("image/x-xpixmap", "xpm", false, false));
    mimeTypes.put("image/x-xwindowdump", new MimeType("image/x-xwindowdump", "xwd", false, false));
    mimeTypes.put("model/iges", new MimeType("model/iges", "iges", false, false));
    mimeTypes.put("model/iges", new MimeType("model/iges", "igs", false, false));
    mimeTypes.put("model/mesh", new MimeType("model/mesh", "mesh", false, false));
    mimeTypes.put("model/mesh", new MimeType("model/mesh", "msh", false, false));
    mimeTypes.put("model/mesh", new MimeType("model/mesh", "silo", false, false));
    mimeTypes.put("model/vrml", new MimeType("model/vrml", "vrml", false, false));
    mimeTypes.put("model/vrml", new MimeType("model/vrml", "wrl", false, false));
    mimeTypes.put("text/html", new MimeType("text/html", "html", false, false));
    mimeTypes.put("text/plain", new MimeType("text/plain", "asc", false, false));
    mimeTypes.put("text/sgml", new MimeType("text/sgml", "sgm", false, false));
    mimeTypes.put("video/mpeg", new MimeType("video/mpeg", "mpe", false, false));
    mimeTypes.put("video/mpeg", new MimeType("video/mpeg", "mpeg", false, false));
    mimeTypes.put("x-conference/x-cooltalk", new MimeType("x-conference/x-cooltalk", "ice", false, false));

     */
  }
  
  public static MimeMap getInstance() {
    if (instance == null) {
      instance = new MimeMap();
    }
    return instance;
  }
  
  public MimeType getMimeType(String type) {
    return (MimeType)mimeTypes.get(type);
  }
  
  public class MimeType {
    public String mimeType; 
    public String extension;
    public boolean allowed;
    public boolean thumb;
    public MimeType(String mimeType, String extension, boolean allowed, boolean thumb) {
      this.mimeType = mimeType; 
      this.extension = extension; 
      this.allowed = allowed; 
      this.thumb = thumb; 
    }
  }

}
