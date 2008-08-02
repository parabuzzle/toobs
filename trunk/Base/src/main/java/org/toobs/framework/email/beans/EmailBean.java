package org.toobs.framework.email.beans;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class EmailBean {
  public static final int MESSAGE_TYPE_TEXT = 0;
  public static final int MESSAGE_TYPE_HTML = 1;
  protected String textLayoutId;
  protected String htmlLayoutId;
  protected String mailSenderKey;
  protected String emailSender;
  protected String emailSubject;
  protected String messagePreferencesField = null;
  protected String messageText;
  protected String messageHtml;
  protected ArrayList bccs;
  protected int type = 1;

  protected ArrayList recipients;
  protected int attempts = 0;
  protected String failureCause;
  
  
  public EmailBean() {
  }
  
  public ArrayList getRecipients() {
    if (recipients == null) {
      recipients = new ArrayList();
    }
    return recipients;
  }
  public void setRecipients(ArrayList recipients) {
    this.recipients = recipients;
  }
  public String getRecipient(int index) {
    return recipients == null ? null : (String)recipients.get(index);
  }
  public void addRecipient(String recipient) {
    if (recipients == null) {
      recipients = new ArrayList();
    }
    this.recipients.add(recipient);
  }

  public String getMessageHtml() throws Exception {
    return messageHtml;
  }

  public void setMessageHtml(String messageHtml) {
    this.messageHtml = messageHtml;
  }

  public String getMessageText() throws Exception {
    return messageText;
  }

  public void setMessageText(String messageText) {
    this.messageText = messageText;
  }

  public ArrayList getBccs() {
    if (bccs == null) {
      bccs = new ArrayList();
    }
    return bccs;
  }

  public void setBccs(String[] bccsArray) {
    if (bccs == null) {
      bccs = new ArrayList();
    }
    for (int i=0; i<bccsArray.length; i++) {
      bccs.add(bccsArray[i]);
    }
  }

  public void setBccs(ArrayList bccs) {
    this.bccs = bccs;
  }
  
  public String getBcc(int index) {
    return bccs == null ? null : (String)bccs.get(index);
  }
  
  public void addBcc(String recipient) {
    if (recipients == null) {
      recipients = new ArrayList();
    }
    this.recipients.add(recipient);
  }

  public int getAttempts() {
    return attempts;
  }

  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }

  public String getFailureCause() {
    return failureCause;
  }

  public void setFailureCause(String failureCause) {
    this.failureCause = failureCause;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer("SimpleMailMessage: ");
    sb.append("sender=").append(this.emailSender).append("; ");
    sb.append("recipients=").append(this.getRecipients()).append("; ");
    sb.append("subject=").append(this.emailSubject).append("; ");
    /*
    try {
      sb.append("text=").append(this.getMessageHtml());
    } catch (Exception e) {
      sb.append("text=").append(e.getMessage());
    }
    */
    return sb.toString();
  }

  public String getEmailSubject() {
    return emailSubject;
  }

  public void setEmailSubject(String emailSubject) {
    this.emailSubject = emailSubject;
  }
  
  public int getType() {
    return type;
  }
  public void setType(int type) {
    this.type = type;
  }

  public String getMailSenderKey() {
    return mailSenderKey;
  }
  public void setMailSenderKey(String mailSenderKey) {
    this.mailSenderKey = mailSenderKey;
  }

  public String getEmailSender() {
    return emailSender;
  }

  public void setEmailSender(String emailSender) {
    this.emailSender = emailSender;
  }

  public String getHtmlLayoutId() {
  	return htmlLayoutId;
  }
  
  public void setHtmlLayoutId(String htmlLayoutId) {
  	this.htmlLayoutId = htmlLayoutId;
  }
  
  public String getTextLayoutId() {
  	return textLayoutId;
  }
  
  public void setTextLayoutId(String textLayoutId) {
  	this.textLayoutId = textLayoutId;
  }
  
  public String getMessagePreferencesField() {
    return messagePreferencesField;
  }
  
  public void setMessagePreferencesField(String messagePreferencesField) {
    this.messagePreferencesField = messagePreferencesField;
  }

  
}
