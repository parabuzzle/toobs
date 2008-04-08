package org.toobs.framework.servlet.filters.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FormattingResults;
import org.apache.fop.apps.PageSequenceResults;

public abstract class BaseExportFilter extends HttpServlet {

  /** The logger instance */
  private static Log log = LogFactory.getLog(BaseExportFilter.class);

  protected void convertFO2Mime(InputStream foIn, OutputStream mimeOut, String mimeType) throws IOException, FOPException {    
    FopFactory fopFactory = FopFactory.newInstance();    
    FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

    // configure foUserAgent as desired
    try {
      // Construct fop with desired output format
      Fop fop = fopFactory.newFop(mimeType, foUserAgent, mimeOut);
      
  
      // Setup JAXP using identity transformer
      TransformerFactory factory = TransformerFactory.newInstance();
      Transformer transformer = factory.newTransformer(); // identity transformer
  
      // Setup input stream
      Source src = new StreamSource(foIn);
  
      // Resulting SAX events (the generated FO) must be piped through to FOP
      Result res = new SAXResult(fop.getDefaultHandler());
  
      // Start XSLT transformation and FOP processing
      transformer.transform(src, res);
      
      // Debugging output
      if(log.isDebugEnabled()) {
        FormattingResults foResults = fop.getResults();
        java.util.List pageSequences = foResults.getPageSequences();
        for (java.util.Iterator it = pageSequences.iterator(); it.hasNext();) {
          PageSequenceResults pageSequenceResults = (PageSequenceResults)it.next();
          log.debug("PageSequence " 
              + (String.valueOf(pageSequenceResults.getID()).length() > 0 
                  ? pageSequenceResults.getID() : "<no id>") 
                  + " generated " + pageSequenceResults.getPageCount() + " pages.");
        }
        log.debug("Generated " + foResults.getPageCount() + " pages in total.");
      }
      
    } catch (Exception e) {
      throw new FOPException(e);
      
    } finally {      
      mimeOut.flush();
      mimeOut.close();
    }
  }

}
