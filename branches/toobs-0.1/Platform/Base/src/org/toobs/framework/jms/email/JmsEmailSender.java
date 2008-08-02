package org.toobs.framework.jms.email;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.MessageCreator;
import org.toobs.framework.email.beans.EmailBean;
import org.toobs.framework.jms.AbstractJmsSender;


public class JmsEmailSender extends AbstractJmsSender {

  private static Log log = LogFactory.getLog(JmsEmailSender.class);

  public void sendMessage(final EmailBean emailBean) {
    jmsTemplate.send(new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {

        MapMessage mapMessage = null;
        try {
          mapMessage = session.createMapMessage();
          mapMessage.setString("sender", emailBean.getEmailSender());
          mapMessage.setString("subject", emailBean.getEmailSubject());
          mapMessage.setString("recipients", getRecipientList(emailBean.getRecipients()));
          mapMessage.setString("messageText", emailBean.getMessageText());
          mapMessage.setString("messageHtml", emailBean.getMessageHtml());
          mapMessage.setString("mailSenderKey", emailBean.getMailSenderKey());
          mapMessage.setInt("attempts", emailBean.getAttempts());
          mapMessage.setInt("type", emailBean.getType());
          mapMessage.setString("failureCause", emailBean.getFailureCause());
        } catch (Exception e) {
          log.error("JMS Mail exception " + e.getMessage(), e);
          throw new JMSException(e.getMessage());
        }
        return mapMessage;
      }
    });

  }
  private String getRecipientList(List recipientList) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<recipientList.size(); i++) {
      if (i != 0) sb.append(",");
      sb.append((String)recipientList.get(i));
    }
    return sb.toString();
  }
}
