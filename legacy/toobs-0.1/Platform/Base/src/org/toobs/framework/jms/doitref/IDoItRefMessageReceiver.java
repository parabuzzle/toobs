package org.toobs.framework.jms.doitref;

import org.toobs.framework.doitref.beans.DoItRefBean;

public interface IDoItRefMessageReceiver {

  public abstract JmsDoItRefReceiver getJmsReceiver();

  public abstract void setJmsReceiver(JmsDoItRefReceiver jmsReceiver);

  public abstract DoItRefBean recieve() throws JmsDoItRefException;

}