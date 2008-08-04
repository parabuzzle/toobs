package org.toobs.framework.pres.util;

public class CookieVO {
  private String cookieName;
  private String cookieValue;
  
  public String getCookieName() {
    return cookieName;
  }
  public void setCookieName(String cookieName) {
    this.cookieName = cookieName;
  }
  public String getCookieValue() {
    return cookieValue;
  }
  public void setCookieValue(String cookieValue) {
    this.cookieValue = cookieValue;
  }
  public CookieVO(String cookieName, String cookieValue) {
    super();
    this.cookieName = cookieName;
    this.cookieValue = cookieValue;
  }
}
