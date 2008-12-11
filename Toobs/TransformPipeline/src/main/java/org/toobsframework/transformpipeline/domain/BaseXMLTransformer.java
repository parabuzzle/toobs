package org.toobsframework.transformpipeline.domain;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

public abstract class BaseXMLTransformer implements IXMLTransformer {

  protected URIResolver uriResolver;

  public void setURIResolver(URIResolver resolver) {
    this.uriResolver = resolver;    
  }

  protected void setFactoryResolver(TransformerFactory factory) {
    if (uriResolver == null) {
      uriResolver = new XSLUriResolverImpl();
    }
    factory.setURIResolver(uriResolver);    
  }
  

}
