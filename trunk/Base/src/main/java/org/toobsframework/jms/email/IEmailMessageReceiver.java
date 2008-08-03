package org.toobsframework.jms.email;

import org.toobsframework.email.beans.EmailBean;

public interface IEmailMessageReceiver {
  public EmailBean recieve() throws JmsEmailException;
}
