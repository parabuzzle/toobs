package org.toobs.framework.jms.email;

import org.toobs.framework.email.beans.EmailBean;

public interface IEmailMessageSender {
  public void send(EmailBean emailBean) throws JmsEmailException;
}
