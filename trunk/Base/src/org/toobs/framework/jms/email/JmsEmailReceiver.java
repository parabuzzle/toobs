package org.toobs.framework.jms.email;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobs.framework.email.beans.EmailBean;
import org.toobs.framework.jms.AbstractJmsReceiver;


@SuppressWarnings("unchecked")
public class JmsEmailReceiver extends AbstractJmsReceiver {

  private static Log log = LogFactory.getLog(JmsEmailReceiver.class);

  public EmailBean recieveMessage() throws JmsEmailException {
    Message msg = jmsTemplate.receive();
    if (msg == null) {
      return null;
    }
    EmailBean bean = new EmailBean();
    MapMessage mapMessage = (MapMessage)msg;
    try {
      bean.setEmailSender(mapMessage.getString("sender"));
      bean.setEmailSubject(mapMessage.getString("subject"));
      bean.setRecipients(getRecipientList(mapMessage.getString("recipients")));
      bean.setMessageHtml(mapMessage.getString("messageHtml"));
      bean.setMessageText(mapMessage.getString("messageText"));
      bean.setMailSenderKey(mapMessage.getString("mailSenderKey"));
      bean.setAttempts(mapMessage.getInt("attempts"));
      bean.setType(mapMessage.getInt("type"));
      bean.setFailureCause(mapMessage.getString("failureCause"));
    } catch (JMSException e) {
      log.error("Exception getting email from queue: " + e.getMessage(), e);
      throw new JmsEmailException(e);
    }
    return bean;
  }
  
  private ArrayList getRecipientList(String recipientList) {
    String[] recipients = recipientList.split(",");
    ArrayList list = new ArrayList();
    for (int i=0; i<recipients.length; i++) {
      list.add(recipients[i]);
    }
    return list;
  }
}
