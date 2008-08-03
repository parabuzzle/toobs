package org.toobsframework.jms.doitref;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.MessageCreator;
import org.toobsframework.doitref.beans.DoItRefBean;
import org.toobsframework.jms.AbstractJmsSender;


public class JmsDoItRefSender extends AbstractJmsSender {

  private static Log log = LogFactory.getLog(JmsDoItRefSender.class);

  public void sendMessage(final DoItRefBean doItRefBean) {
    jmsTemplate.send(new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {

        Message objMessage = null;
        try {
          objMessage = session.createObjectMessage(doItRefBean);
        } catch (Exception e) {
          log.error("JMS Mail exception " + e.getMessage(), e);
          throw new JMSException(e.getMessage());
        }
        return objMessage;
      }
    });

  }
}
