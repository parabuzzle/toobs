package org.toobsframework.jms.doitref;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.doitref.beans.DoItRefBean;


public class DoItRefMessageSender implements IDoItRefMessageSender {

  private static Log log = LogFactory.getLog(DoItRefMessageSender.class);
  
  private JmsDoItRefSender jmsSender = null;
  
  public JmsDoItRefSender getJmsSender() {
    return this.jmsSender;
  }

  public void setJmsSender(JmsDoItRefSender jmsSender) {
    this.jmsSender = jmsSender;
  }
  
  public void send(DoItRefBean doItRefBean) throws JmsDoItRefException {
    try {
      if (log.isDebugEnabled()) {
        log.debug("Sending DoItRefBean message to JMS Sender ");
      }
      
      jmsSender.sendMessage(doItRefBean);
      
      if (log.isDebugEnabled()) {
        log.debug("DoItRefBean message sent to JMS Sender ");
      }
    } catch(Exception e) {
      log.warn("JmsDoIt failed: " + e.getMessage(), e);
      throw new JmsDoItRefException(e);
    }
  }

}
