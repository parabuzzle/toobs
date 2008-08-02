package org.toobs.framework.jms.email;

import org.toobs.framework.email.beans.EmailBean;

public interface IEmailMessageReceiver {
  public EmailBean recieve() throws JmsEmailException;
}
