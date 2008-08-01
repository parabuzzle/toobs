package org.toobs.framework.jms.email;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.mail.MailException;
import org.toobs.framework.email.SmtpMailSender;
import org.toobs.framework.email.beans.EmailBean;


public class EmailMessageSender implements IEmailMessageSender {

  private static Log log = LogFactory.getLog(EmailMessageSender.class);
  
  private JmsEmailSender jmsSender = null;
  
  public JmsEmailSender getJmsSender() {
    return this.jmsSender;
  }

  public void setJmsSender(JmsEmailSender jmsSender) {
    this.jmsSender = jmsSender;
  }
  
  public void send(EmailBean emailBean) throws JmsEmailException {
    try {
      if (log.isDebugEnabled()) {
        log.debug("Sending Email message to JMS Sender ");
      }
      jmsSender.sendMessage(emailBean);
      if (log.isDebugEnabled()) {
        log.debug("Email message sent to JMS Sender ");
      }
    } catch(Exception e) {
      log.warn("JmsEmail failed: " + e.getMessage() + " trying direct", e);
      BeanFactoryLocator bfl = org.springframework.beans.factory.access.SingletonBeanFactoryLocator.getInstance();
      BeanFactoryReference bf = bfl.useBeanFactory("beanRefFactory");
      SmtpMailSender sender = (SmtpMailSender)bf.getFactory().getBean("SmtpMailSender");
      try {
        sender.sendEmail(emailBean);
      } catch (MailException e1) {
        log.error("Email exception: " + e.getMessage(), e);
        throw new JmsEmailException(e);
      } catch (MessagingException e1) {
        log.error("Email exception: " + e.getMessage(), e);
        throw new JmsEmailException(e);
      }
    }
  }

}
