package org.toobs.framework.jms.doitref;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobs.framework.doitref.beans.DoItRefBean;


public class DoItRefMessageReceiver implements IDoItRefMessageReceiver  {

  private static Log log = LogFactory.getLog(DoItRefMessageReceiver.class);
  
  private JmsDoItRefReceiver jmsReceiver = null;
  
  public JmsDoItRefReceiver getJmsReceiver() {
    return this.jmsReceiver;
  }

  public void setJmsReceiver(JmsDoItRefReceiver jmsReceiver) {
    this.jmsReceiver = jmsReceiver;
  }
  
  public DoItRefBean recieve() throws JmsDoItRefException {
    if (log.isDebugEnabled()) {
      log.debug("recieve()");
    }
    return jmsReceiver.recieveMessage();
  }

}
