package org.toobsframework.biz.validation;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.springframework.validation.ValidationUtils;

/**
 *   A class encapsulating stateless utility functions to be used 
 *   from validators within the same package.
 */
public class CustomValidationUtils extends ValidationUtils {
		
  /**
   * Retrieves the stored value in the Map argument, retrieving just the first
   * value in the array if the stored value is an array of Strings.
   */
  public static String getPostedPropertyValue(String key, Map properties) {
    String postedValue = null;
    Object storedValue = properties.get(key);
    
    if(storedValue != null && storedValue.getClass().isArray()) {
      postedValue = ((String[])storedValue)[0];
    } else {
      postedValue = (String)storedValue;
    }
    
    return postedValue;
  }

	/**
	 * Checks to see if the string entered as input for an email field
	 * is a valid email address via a regex match. 
	 * @param inputEmail
	 * @return
	 */
	public static boolean rejectIfNotAValidEmail(String inputEmail)
	{
		//if input's not null, not empty, and doesn't match the pattern 
		if(isFilledField(inputEmail)
				&& !inputEmail.trim().toLowerCase().matches(".+@.+\\.[a-z]+"))
		{
			//then it's invalid
			return false;
		}
			
		//otherwise valid
		return true;
	}
	
	/**
	 * Checks to see if the string entered as input for a url field
	 * is a valid url via a regex match. 
	 * @param inputUrl
	 * @return
	 */
	public static boolean rejectIfNotAValidUrl(String inputUrl)
	{		
		//OLD PATTERN
		//"[a-zA-Z0-9.:/?&=]+"))

		//if input's not null, not empty, and doesn't match the pattern 
		if(isFilledField(inputUrl) 
				&& !inputUrl.trim().matches(
						"((www.|(http|https|ftp|news|file)+://|)[_.A-Za-z0-9-]+.[A-Za-z0-9/_:@=.+?,##%&~-]*[^.|\'|# |!|(|?|,| |>|<|;|)])"						
						))									
		{
			//then it's invalid
			return false;
		}

		//otherwise valid
		return true;
	}
	
	/**
	 * Given three integer inputs representing a date, checks to 
	 * see if the specified date is after today's date.
	 * @param day - int 1-31
	 * @param month - int 1-12
	 * @param year
	 * @return
	 */
	public static boolean rejectIfDayMonthYearBeforeToday(Integer day, Integer month, Integer year)
	{
		if(day != null && month != null && year != null)
		{
			//construct Calendar object
			//set time fields with inputted parameters
			GregorianCalendar cal = new GregorianCalendar();
			// note month conversion: Calendar expects 0 based month param
			cal.set(year, month - 1, day);
			//get a date object out of the 
			Date closingDate = cal.getTime();
			
			//if the closing date is before right now,
			//then throw a validation error
			if(closingDate.before(new Date(System.currentTimeMillis())))
			{
				return false;
			}
		}

		return true;
	}
  
  /**
   * Checks to see if the string passed as argument is non null and 
   * non empty
   * @param inputString
   * @return
   */
  public static boolean isFilledField(String inputString)
  {
    
    if(inputString != null && !"".equals(inputString.trim()))
    {
      return true;
    }   
    return false;
  }

  
}
