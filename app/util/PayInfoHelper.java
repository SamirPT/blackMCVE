package util;

import org.apache.commons.lang3.StringUtils;
import to.ErrorMessage;
import to.PayInfoTO;

/**
 * Created by arkady on 05/08/16.
 */
public class PayInfoHelper {

	public static boolean isAnyFieldPresent(PayInfoTO payInfoTO) {
		return StringUtils.isNotEmpty(payInfoTO.getFirstName()) ||
				StringUtils.isNotEmpty(payInfoTO.getLastName()) ||
				StringUtils.isNotEmpty(payInfoTO.getAddress()) ||
				StringUtils.isNotEmpty(payInfoTO.getState()) ||
				StringUtils.isNotEmpty(payInfoTO.getCity()) ||
				StringUtils.isNotEmpty(payInfoTO.getCountry()) ||
				StringUtils.isNotEmpty(payInfoTO.getPhoneNumber()) ||
				StringUtils.isNotEmpty(payInfoTO.getEmail()) ||
				StringUtils.isNotEmpty(payInfoTO.getCompanyName()) ||
				StringUtils.isNotEmpty(payInfoTO.getPostalCode()) ||
				StringUtils.isNotEmpty(payInfoTO.getDateOfBirth());
	}

	public static boolean isAllRequiredFieldsPresent(PayInfoTO payInfoTO) {
		return StringUtils.isNotEmpty(payInfoTO.getFirstName()) &&
				StringUtils.isNotEmpty(payInfoTO.getLastName()) &&
				StringUtils.isNotEmpty(payInfoTO.getAddress()) &&
				StringUtils.isNotEmpty(payInfoTO.getCity()) &&
				StringUtils.isNotEmpty(payInfoTO.getCountry()) &&
				StringUtils.isNotEmpty(payInfoTO.getPostalCode()) &&
				StringUtils.isNotEmpty(payInfoTO.getDateOfBirth());
	}

	public static ErrorMessage getErrorMessageForBadPayInfo(PayInfoTO payInfoTO) {
		ErrorMessage errorMessage = new ErrorMessage("Please fill all required fileds");
		if (StringUtils.isEmpty(payInfoTO.getFirstName())) errorMessage.addField("firstName");
		if (StringUtils.isEmpty(payInfoTO.getLastName())) errorMessage.addField("lastName");
		if (StringUtils.isEmpty(payInfoTO.getAddress())) errorMessage.addField("address");
		if (StringUtils.isEmpty(payInfoTO.getCity())) errorMessage.addField("city");
		if (StringUtils.isEmpty(payInfoTO.getCountry())) errorMessage.addField("country");
		if (StringUtils.isEmpty(payInfoTO.getPostalCode())) errorMessage.addField("postalCode");
		if (StringUtils.isEmpty(payInfoTO.getDateOfBirth())) errorMessage.addField("dateOfBirth");
		return errorMessage;
	}
}
