package org.toobs.framework.exception;

@SuppressWarnings("serial")
public class PermissionException extends BaseException {
  private String objectTypeName;
  private String action;
  private Object guid;
  private String personId;
  private String reason;
  
  public PermissionException(String objectTypeName, String action, Object guid, String personId, String reason) {
    this.objectTypeName = objectTypeName;
    this.action = action;
    this.guid = guid;
    this.personId = personId;
    this.reason = reason;
  }

  public String getMessage() {
    String message = "PermissionException";
    message += "\n  Object: " + objectTypeName;
    message += "\n  GUID  : " + guid;
    message += "\n  Action: " + action;
    message += "\n  Person: " + personId;
    message += "\n  Reason: " + reason;
    return message;
  }

  public String getAction() {
    return action;
  }

  public Object getGuid() {
    return guid;
  }

  public String getObjectTypeName() {
    return objectTypeName;
  }

  public String getPersonId() {
    return personId;
  }

  public String getReason() {
    return reason;
  }
}
