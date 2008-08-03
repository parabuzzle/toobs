package org.toobsframework.jms.email;

import org.toobsframework.email.beans.EmailBean;

public interface IEmailMessageSender {
  public void send(EmailBean emailBean) throws JmsEmailException;
}
