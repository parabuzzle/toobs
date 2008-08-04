package org.toobs.framework.doitref;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.toobs.framework.doitref.beans.DoItRefBean;
import org.toobs.framework.jms.doitref.IDoItRefMessageReceiver;
import org.toobs.framework.jms.doitref.IDoItRefMessageSender;
import org.toobs.framework.scheduler.ScheduledJob;


public class DoItRefJob implements Job, ScheduledJob {

  private static Log log = LogFactory.getLog(DoItRefJob.class);
  
  private static boolean running = false;
  
  private static Object synch = new Object();
  
  private int retries = 3;
  
  public DoItRefJob() {
  }

  public void execute(JobExecutionContext context)
    throws JobExecutionException {
    if (isRunning()) {
      log.info("DoItRef job already running");
      return;
    }
    try {
      setRunning(true);
      log.info("Starting DoItRef processing");
      BeanFactoryLocator bfl = org.springframework.beans.factory.access.SingletonBeanFactoryLocator.getInstance();
      BeanFactory beanFactory = bfl.useBeanFactory("beanRefFactory").getFactory();
      if (!beanFactory.containsBean("IDoItRefMessageReceiver") || !beanFactory.containsBean("IDoItRefMessageSender")) {
        log.info("JMS Email disabled");
        return;
      }

      IDoItRefMessageReceiver dimr = (IDoItRefMessageReceiver)beanFactory.getBean("IDoItRefMessageReceiver");
      IDoItRefMessageSender dims = (IDoItRefMessageSender)beanFactory.getBean("IDoItRefMessageSender");

      IDoItRefQueue queueRunner = (IDoItRefQueue)beanFactory.getBean("IDoItRefQueue");
      DoItRefBean doItRef = null;
      while ((doItRef = dimr.recieve()) != null) {
        try {
          if (log.isDebugEnabled()) {
            log.debug("Sending doItRef: " + doItRef.toString());
          }
          queueRunner.runDoItRef(doItRef);
        } catch (Exception e) {
          log.info("Send Email failed " + e.getMessage(), e);
          doItRef.setFailureCause(e.getMessage());
          doItRef.setAttempts(doItRef.getAttempts() + 1);
          if (doItRef.getAttempts() < retries) {
            dims.send(doItRef);
          } else {
            // Send to failed queue
            //emf.send(email);
          }
        }
      }
    } catch (Exception e) {
      log.error("Error running email job", e);
    } finally {
      setRunning(false);      
    }
  }

  public static void shutdownJob() {
  }

  public void shutdown() {
    DoItRefJob.shutdownJob();
  }
  
  private boolean isRunning() {
    synchronized(synch) {
      return DoItRefJob.running;
    }
  }

  private void setRunning(boolean running) {
    synchronized(synch) {
      DoItRefJob.running = running;
    }
  }
}
