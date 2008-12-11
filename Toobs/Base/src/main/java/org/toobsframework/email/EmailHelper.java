package org.toobsframework.email;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.toobsframework.email.beans.ErrorEmailBean;


public class EmailHelper {
  
  private static Log log = LogFactory.getLog(EmailHelper.class);

  private static BeanFactory beanFactory;
  private static SmtpMailSender smtpSender;

  static {
    beanFactory = SingletonBeanFactoryLocator.getInstance().useBeanFactory("beanRefFactory").getFactory();
    smtpSender = (SmtpMailSender)beanFactory.getBean("SmtpMailSender");
  }
  
  public static void sendErrorEmail(String personId, String context, Throwable throwable) {
    try {
      ErrorEmailBean email = (ErrorEmailBean)beanFactory.getBean("ErrorEmailBean");
      email.setPersonId(personId);
      email.setContext(context);
      email.setThrowable(throwable);
      
      smtpSender.sendEmail(email);
    } catch (Exception e) {
      log.warn("Could not sent error notification email " + e.getMessage(), e);
    }
  }
}
