package org.toobsframework.jms.doitref;

import org.toobsframework.doitref.beans.DoItRefBean;

public interface IDoItRefMessageSender {

  public abstract JmsDoItRefSender getJmsSender();

  public abstract void setJmsSender(JmsDoItRefSender jmsSender);

  public abstract void send(DoItRefBean doItRefBean) throws JmsDoItRefException;

}