package org.toobs.framework.jms.doitref;

import org.toobs.framework.doitref.beans.DoItRefBean;

public interface IDoItRefMessageSender {

  public abstract JmsDoItRefSender getJmsSender();

  public abstract void setJmsSender(JmsDoItRefSender jmsSender);

  public abstract void send(DoItRefBean doItRefBean) throws JmsDoItRefException;

}