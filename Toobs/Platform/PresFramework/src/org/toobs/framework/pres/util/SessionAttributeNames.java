package org.toobs.framework.pres.util;

/**
 * Catalogue of all the "well-known" session attribute names.  These variables are initialized by
 * the post login process and are for application wide consumption.
 * @author jose
 */
public class SessionAttributeNames {

  /**
   * Name of session variable used to store the users id
   */
  public static final String USER_ID = "person_id";

  /**
   * Attribute name under which the bread crumb information is stored.
   */
  public static final String BREAD_CRUMB = "breadCrumb";

  /**
   * Name of the session variable that stores the currently logged in user's roles
   */
  public static final String USER_ROLES = "user_roles";

  /**
   * Session Attribute name under which language code is stored.
   */
  public static final String USER_LANGUAGECODE = "languagecode";

  /**
   * Stores the time format, analogous to UserTimeJavaFMT in CFM session
   */
  public static final String USER_TIME_FORMAT = "timeformat";

  /**
   * Attribute name that stores the timezone of the logged in user
   */
  public static final String USER_TIMEZONE = "timezone";

  /**
   * Attribute name that stores the short date format of the logged in user
   */
  public static final String USER_SHORT_DATE_FORMAT = "timezone";
  
  /**
   * Name of session variable used to store the money field formatting pattern
   */
  public static final String MONEY_FMT_PATTERN = "moneyFieldPattern";
  
  /**
   * Attribute for saving allowed portlets in session
   */
  public static final String USER_MESSAGES_KEY = "user.messages";

  /**
   * Default constructor
   */
  public SessionAttributeNames() {
    super();
  }

}
