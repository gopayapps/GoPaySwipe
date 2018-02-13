package net.beyondtelecom.gopay.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum BTResponseCode implements Serializable
{
	/* Response codes below 0 should never be shown to user, should show general error instead */
	SUCCESS						(0,		"Successful"),

	//system errors
	GENERAL_ERROR				(1,	    "A general error occurred"),
	CONFIGURATION_INVALID		(-1,	"Specified configuration is not valid"),

	//input validation errors
	INPUT_INCOMPLETE_REQUEST	(2,		"Incomplete request specified"),
	INPUT_INVALID_REQUEST		(3,		"Invalid request specified"),
	INPUT_INVALID_EMAIL			(4,	    "Email provided was not valid"),
	INPUT_INVALID_MSISDN		(5,		"Phone number provided was not valid"),
	INPUT_INVALID_FIRST_NAME	(6,		"First name provided was not valid"),
	INPUT_INVALID_LAST_NAME		(7,		"Last name provided was not valid"),
	INPUT_INVALID_USERNAME		(8,		"Username provided was not valid"),
	INPUT_INVALID_PASSWORD		(9,		"Password provided was not valid"),
	INPUT_INVALID_NAME			(10,	"Name provided was not valid"),
	INPUT_INVALID_AMOUNT		(11,	"Invalid amount specified"),
	INPUT_INVALID_WALLET        (12,	"Invalid wallet specified"),
	INPUT_INVALID_CASHIER       (13,	"Invalid cashier name specified"),

	DATA_NOT_FOUND				(15,	"Data does not exist"),
	NOT_SUPPORTED				(16,	"Not supported"),

	//Authentication errors
	AUTH_INSUFFICIENT_PRIVILEGES	(20,	"Insufficient privileges for current operation"),
	AUTH_AUTHENTICATION_FAILED		(21,	"Authentication failed"),
	AUTH_INCORRECT_PASSWORD			(22,	"Password is incorrect"),
	AUTH_NON_EXISTENT				(23,	"Account does not exist"),

	//Account Status
	ACC_ACTIVE					(30,	"Account is active"),			//fully active account
	ACC_INACTIVE				(31,	"Account is inactive"),			//pending verification
	ACC_SUSPENDED				(32,	"Account has been suspended"),	//temporarily blocked (due to illegal activity)
	ACC_CLOSED					(33,	"Account has been closed"),		//deleted
	ACC_PASSWORD_TRIES_EXCEEDED	(34,	"Password tries exceeded"),		//temporarily blocked, must reset password
	ACC_PASSWORD_EXPIRED		(35,	"Password expired"),			//temporarily blocked, must reset password

	//Connectivity codes
	CONNECTION_FAILED	        (40,	"Connection failed"),
	UNKNOWN_HOST	            (41,	"Unknown host"),
	CONNECTION_REFUSED	        (42,	"Connection Refused"),
	TIMEOUT	                    (43,	"Timeout elapsed before transaction completion"),

	INSUFFICIENT_FUNDS			(51,	"Insufficient funds"),

	INSUFFICIENT_STOCK			(60,	"Insufficient stock"),

	EXISTING_DATA_FOUND			(80,	"Existing data found"),

	// Registration Codes
	REGISTRATION_FAILED         (351,	"Registration Failed"),
	PREVIOUS_USERNAME_FOUND     (352,	"Username has been previously registered"),
	PREVIOUS_MSISDN_FOUND       (353,	"Mobile number has been previously registered"),
	PREVIOUS_EMAIL_FOUND        (354,	"Email has been previously registered"),
	PREVIOUS_REGISTRATION_FOUND (355,	"Previous registration found.")
	;

	public final Integer code;
	public String message;

	BTResponseCode(int code, String message) { this.code = code; this.message = message; }

	BTResponseCode(BTResponseCode responseCode, String message) { this.code = responseCode.code; this.message = message; }

	static Map<Integer, BTResponseCode> enumMap;

	public static BTResponseCode valueOf(Integer value) {
		if(enumMap == null) {
			enumMap = new HashMap<>();
			for(BTResponseCode rc : BTResponseCode.values()) { enumMap.put(rc.code, rc); }
		}
		return enumMap.get(value);
	}

	public int getCode() { return code; }

	public String getMessage() { return message; }

	public BTResponseCode setMessage(String message) { this.message = message; return this; }
}
