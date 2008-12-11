package org.toobsframework.jms.email;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.email.beans.EmailBean;


public class EmailMessageReceiver implements IEmailMessageReceiver {

  private static Log log = LogFactory.getLog(EmailMessageReceiver.class);
  
  private JmsEmailReceiver jmsReceiver = null;
  
  public JmsEmailReceiver getJmsReceiver() {
    return this.jmsReceiver;
  }

  public void setJmsReceiver(JmsEmailReceiver jmsReceiver) {
    this.jmsReceiver = jmsReceiver;
  }
  
  public EmailBean recieve() throws JmsEmailException {
    if (log.isDebugEnabled()) {
      log.debug("recieve()");
    }
    return jmsReceiver.recieveMessage();
  }

}
