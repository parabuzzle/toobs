package org.toobsframework.data.criteria;

import java.util.regex.Pattern;

public interface ICriteriaSearchParameter {

  public static final Pattern PATTERN = Pattern.compile("\\.");

  public static final int LIKE_COMPARATOR = 0;

  public static final int INSENSITIVE_LIKE_COMPARATOR = 1;

  public static final int EQUAL_COMPARATOR = 2;

  public static final int GREATER_THAN_OR_EQUAL_COMPARATOR = 3;

  public static final int GREATER_THAN_COMPARATOR = 4;

  public static final int LESS_THAN_OR_EQUAL_COMPARATOR = 5;

  public static final int LESS_THAN_COMPARATOR = 6;

  public static final int IN_COMPARATOR = 7;

  public static final int NOT_EQUAL_COMPARATOR = 8;

  /** Order unset */
  public static final int ORDER_UNSET = -1;

  /** Ascending order */
  public static final int ORDER_ASC = 0;

  /** Descending order */
  public static final int ORDER_DESC = 1;

  /** Order relevance not set */
  public static final int RELEVANCE_UNSET = -1;

  /**
   * @return The comparator to be used (e.g. like, =, <, ...).
   */
  public abstract int getComparatorID();

  /**
   * Sets the comparator to be used (e.g. like, =, <, ...).
   *
   * @param comparatorID The comprator ID.
   */
  public abstract void setComparatorID(int comparatorID);

  /**
   * @return The pattern of this parameter (dot-seperated path e.g. person.address.street).
   */
  public abstract String getParameterPattern();

  /**
   * Sets the pattern of this parameter.
   *
   * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street).
   */
  public abstract void setParameterPattern(String parameterPattern);

  /**
   * @return The last part of the parameter pattern, i.e. the attribute name.
   */
  public abstract String getParameterName();

  /**
   * @return The value of this parameter.
   */
  public abstract Object getParameterValue();

  /**
   * Sets the value of this parameter.
   *
   * @param parameterValue The value of this parameter.
   */
  public abstract void setParameterValue(Object parameterValue);

  /**
   * @return Whether this parameter will be included in the search even if it is <code>null</code>.
   */
  public abstract boolean isSearchIfIsNull();

  /**
   * Defines whether parameter will be included in the search even if it is <code>null</code>.
   *
   * @param searchIfNull <code>true</code> if the parameter should be included in the search
   *                     even if it is null, <code>false</code> otherwise.
   */
  public abstract void setSearchIfIsNull(boolean searchIfNull);

  /**
   * @return The order (ascending or descending) for this parameter.
   * @see ORDER_ASC
   * @see ORDER_DESC
   * @see ORDER_UNSET
   */
  public abstract int getOrderDirection();

  /**
   * Sets the ordering for this parameter.
   *
   * @param orderDirection The ordering for this parameter.
   */
  public abstract void setOrderDirection(int orderDirection);

  /**
   * @return The relevance for this parameter.
   * @see RELEVANCE_UNSET
   */
  public abstract int getOrderRelevance();

  /**
   * Sets the ordering relevance for this parameter.
   *
   * @param order The ordering relevance for this parameter.
   */
  public abstract void setOrderRelevance(int relevance);

  public abstract int getJoinTypeId();

  public abstract void setJoinTypeId(int joinTypeId);

}