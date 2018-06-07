package util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by arkady on 21/07/16.
 */
public class PhoneHelper {

	public static String normalizePhoneNumber(String from) throws NumberFormatException {
		if (StringUtils.isEmpty(from)) return from;

		String phoneNumber = from.replaceAll("[\\s-()]+","");
		if (phoneNumber.length() == 10 && !phoneNumber.startsWith("+")) phoneNumber = "+1" + phoneNumber;

		Long.valueOf(phoneNumber.replaceAll("\\+", ""));

		return phoneNumber;
	}
}
