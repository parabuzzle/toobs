package org.toobs.framework.jms.doitref;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.toobs.framework.doitref.beans.DoItRefBean;
import org.toobs.framework.jms.AbstractJmsReceiver;


@SuppressWarnings("unchecked")
public class JmsDoItRefReceiver extends AbstractJmsReceiver {

  private static Log log = LogFactory.getLog(JmsDoItRefReceiver.class);

  public DoItRefBean recieveMessage() throws JmsDoItRefException {
    DoItRefBean bean = null;
    try {
      bean = (DoItRefBean)jmsTemplate.receiveAndConvert();
    } catch (Exception e) {
      log.error("Exception getting email from queue: " + e.getMessage(), e);
      throw new JmsDoItRefException(e);
    }
    return bean;
  }
  
}
