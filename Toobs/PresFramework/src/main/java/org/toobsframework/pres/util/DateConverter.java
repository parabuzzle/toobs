package org.toobsframework.pres.util;

import java.util.Date;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConversionException;

/**
 * Converts a date string into java.util.Date
 * @author stewari
 */
public class DateConverter implements Converter {

	public DateConverter() {
	}
	
	/**
	 * @param type - should be java.util.Date
	 * @param value - should be specified in milliseconds as a String
	 */
	public Object convert(Class type, Object value) 
		throws ConversionException {
		Date convertedDate = null;
		if (!java.util.Date.class.isAssignableFrom(type)) {
			throw new ConversionException("Invalid input type. Should be java.util.Date");
		}
		if (!String.class.isAssignableFrom(value.getClass())) {
			throw new ConversionException("Value should be a string");
		}
		String timeStr = (String) value;
		convertedDate = new Date((new Long(timeStr)).longValue());
		return convertedDate;
	}
}
