package org.toobsframework.jms.doitref;

import org.toobsframework.doitref.beans.DoItRefBean;

public interface IDoItRefMessageReceiver {

  public abstract JmsDoItRefReceiver getJmsReceiver();

  public abstract void setJmsReceiver(JmsDoItRefReceiver jmsReceiver);

  public abstract DoItRefBean recieve() throws JmsDoItRefException;

}