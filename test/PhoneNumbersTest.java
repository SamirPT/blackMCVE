import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import util.PhoneHelper;

import static org.junit.Assert.assertEquals;

/**
 * Created by arkady on 21/07/16.
 */
public class PhoneNumbersTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void phoneNumbersNormalizationTest() {
		final String TEST_PHONE = "+19656957719";
		assertEquals(TEST_PHONE, PhoneHelper.normalizePhoneNumber("+1 965 695-77-19"));
		assertEquals(TEST_PHONE, PhoneHelper.normalizePhoneNumber("+1 (965) 695-77-19"));
		assertEquals(TEST_PHONE, PhoneHelper.normalizePhoneNumber("+1(965)695-77-19"));
		assertEquals(TEST_PHONE, PhoneHelper.normalizePhoneNumber("(965) 695-77-19"));
		assertEquals(TEST_PHONE, PhoneHelper.normalizePhoneNumber("(965)695-77-19"));
		assertEquals(TEST_PHONE, PhoneHelper.normalizePhoneNumber("965-695-77-19"));
		assertEquals(TEST_PHONE, PhoneHelper.normalizePhoneNumber("965 695 77 19"));
		assertEquals("", PhoneHelper.normalizePhoneNumber(""));
		assertEquals(null, PhoneHelper.normalizePhoneNumber(null));
	}

	@Test(expected = NumberFormatException.class)
	public void invalidPhoneNumbersTest() {
		PhoneHelper.normalizePhoneNumber("+1 965 695-77-19a");
	}
}
