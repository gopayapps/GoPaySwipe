package net.beyondtelecom.gopay.common;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * User: tkaviya
 * Date: 3/27/2015
 * Time: 6:44 AM
 */
public class Validator
{
	private static final Integer MIN_PASSWORD_LENGTH = 6;

	private static final Integer MAX_PASSWORD_LENGTH = 50;

    private static final Integer PIN_LEN = 4;
    private static final Integer MIN_PIN_LEN = 4;
    private static final Integer MAX_PIN_LEN = 6;

    private static final Integer MIN_NAME_LEN = 2;
    private static final Integer MAX_NAME_LEN = 50;

    private static final Integer MIN_UNAME_LEN = 2;
    private static final Integer MAX_UNAME_LEN = 50;

    private static final Integer MIN_CARD_LEN = 13;
    private static final Integer MAX_CARD_LEN = 19;

    public static final Integer MIN_PLAIN_TEXT_LEN = 2;
    public static final Integer MAX_PLAIN_TEXT_LEN = 50;

    public static final String PLAIN_TEXT_CHARS = "[_a-zA-Z0-9- ]";

    public static final String PLAIN_TEXT_REGEX = PLAIN_TEXT_CHARS + "{" + MIN_PLAIN_TEXT_LEN + "," + MAX_PLAIN_TEXT_LEN + "}";


    public static final Pattern plainTextPattern = Pattern.compile(PLAIN_TEXT_REGEX);


    public static boolean isNumeric(Object testObject) {
        if (testObject == null) return false;
        if (testObject instanceof Number) return true;
        try { new BigDecimal(String.valueOf(testObject)); return true; }
        catch (Exception e) { return false; }
    }


    public static boolean isValidPlainText(String text) {
        return !isNullOrEmpty(text) && plainTextPattern.matcher(text).matches();
    }

	public static boolean isValidEmail(String emailAddress) {
		return !isNullOrEmpty(emailAddress) &&
                emailAddress.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");
	}


	public static boolean isValidPassword(String password) {
		return !isNullOrEmpty(password) &&
                password.matches("[a-zA-Z0-9<>.!@();:#$%&*+/=?^_{|}~-]{" + MIN_PASSWORD_LENGTH + "," + MAX_PASSWORD_LENGTH +"}");
	}

    public static boolean isValidPin(String pin) {
        return !isNullOrEmpty(pin) && pin.matches("[0-9]{" + PIN_LEN +"}");
    }

    public static boolean isValidCardPin(String pin) {
        return !isNullOrEmpty(pin) && pin.matches("[0-9]{" + MIN_PIN_LEN + "," + MAX_PIN_LEN +"}");
    }

    public static boolean isValidMsisdn(String msisdn, String countryCodePrefix) {
        return isValidMsisdn(msisdn) || msisdn.matches("^(([0]{2})|[+])*(" + countryCodePrefix + ")[0-9]{9}");
	}

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.equals("");
	}

	public static boolean isValidMsisdn(String msisdn) {
	    return !isNullOrEmpty(msisdn) && msisdn.matches("^(0)[0-9]{9}");
	}

    public static boolean isValidName(String name) {
        return !isNullOrEmpty(name) &&
                name.matches("[a-zA-Z- ]{" + MIN_NAME_LEN + "," + MAX_NAME_LEN + "}");
    }

    public static boolean isValidUsername(String username) {
        return !isNullOrEmpty(username) &&
                username.matches("[a-zA-Z0-9-_.]{" + MIN_UNAME_LEN + "," + MAX_UNAME_LEN + "}");
    }

    public static boolean isValidCardNumber(String cardNum) {
        return !isNullOrEmpty(cardNum) &&
                cardNum.matches("[0-9]{" + MIN_CARD_LEN + "," + MAX_CARD_LEN + "}");
    }

}
