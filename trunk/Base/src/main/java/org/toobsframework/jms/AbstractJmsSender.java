package org.toobsframework.jms;

import org.springframework.jms.core.JmsTemplate;

public abstract class AbstractJmsSender {

  protected JmsTemplate jmsTemplate;

  /**
   * @return Returns the jmsTemplate.
   */
  public JmsTemplate getJmsTemplate() {
    return jmsTemplate;
  }
  /**
   * @param jmsTemplate The jmsTemplate to set.
   */
  public void setJmsTemplate(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

}
