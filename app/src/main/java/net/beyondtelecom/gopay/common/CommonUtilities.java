package net.beyondtelecom.gopay.common;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.logging.Logger;

import static java.lang.Math.abs;
import static net.beyondtelecom.gopay.common.Validator.isNullOrEmpty;


/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/12/13
 * Time: 10:05 PM
 */
public class CommonUtilities
{
    private static final Logger logger = Logger.getLogger(CommonUtilities.class.getSimpleName());

    public static String joinWithDelimiter(Object delimiter, final Object... args) {

        if (args == null) { return null; }

        if (delimiter == null) { delimiter = ""; }

        StringBuilder stringBuilder = new StringBuilder();

        int countPresent = 0;
        for (Object arg : args) {
			if (arg != null) {
                countPresent++;
                if (!arg.toString().trim().equals("")) {
                    stringBuilder.append(arg).append(delimiter);
                }
            }
		}
        if (countPresent == 0) {
            return null;
        } else if (!delimiter.equals("") && stringBuilder.toString().endsWith(delimiter.toString())) {
			return stringBuilder.substring(0, stringBuilder.lastIndexOf(delimiter.toString()));
		} else {
			return stringBuilder.toString();
		}
	}

    public static String join(final Object... parts) { return joinWithDelimiter(null, parts); }

	public static String toCamelCase(final String str) { return toCamelCase(str, " "); }

	public static String toCamelCase(final String str, final String delimiter) {

		if (isNullOrEmpty(str)) return str;

		StringBuilder returnString = new StringBuilder(str.length());

		for (final String word : str.split(delimiter)) {
			if (!word.isEmpty()) {
				returnString.append(word.substring(0, 1).toUpperCase());
				returnString.append(word.substring(1).toLowerCase());
			}
			if (!(returnString.length() == str.length()))
				returnString.append(delimiter);
		}

		return returnString.toString();
	}

    public static String decapitalizeFirst(final String str) {
        return isNullOrEmpty(str) ? str : join(str.substring(0, 1).toLowerCase(), str.substring(1));
    }

    public static String capitalizeFirst(final String str) {
        return isNullOrEmpty(str) ? str : Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String capitalizeAll(final String str) {
        if (isNullOrEmpty(str)) { return str; }
        char stringArray[] = str.toCharArray();
        String resultStr = "";
        for (char c : stringArray) { resultStr += Character.toUpperCase(c); }
        return resultStr;
    }

    public static String decapitalizeAll(final String str) {
        if (isNullOrEmpty(str)) { return str; }
        char stringArray[] = str.toCharArray();
        String resultStr = "";
        for (char c : stringArray) { resultStr += Character.toLowerCase(c); }
        return resultStr;
    }

	public static String alignStringToLength(String str, final int length) {
        if (isNullOrEmpty(str)) str = "";
		while (str.length() < length) { str += " "; }
		return str;
	}

    public static String formatDoubleToMoney(final double value, final String currencySymbol)
    {
        DecimalFormat df = new DecimalFormat("###,##0.00");

        String formattedString = df.format(value);

        if (!formattedString.startsWith("-")) {
            return (currencySymbol == null ? "" : currencySymbol) + formattedString;
        } else {
            return "-" + (currencySymbol == null ? "" : currencySymbol) + formattedString.replaceFirst("-", "");
        }
    }

    public static String formatDoubleToMoney(final double value) { return formatDoubleToMoney(value, null); }

    public static String formatFullMsisdn(String msisdn, String countryCodePrefix) {

        if (msisdn == null || countryCodePrefix == null) { return msisdn; }

        if (msisdn.length() > 10) {
            if (msisdn.startsWith(countryCodePrefix)) { return msisdn; }
            else if (msisdn.startsWith("+")) { msisdn = msisdn.replaceFirst("\\+", ""); }
            else if (msisdn.startsWith("00")) { msisdn = msisdn.replaceFirst("00", ""); }
            return msisdn;
        }
        else if (msisdn.length() == 10 && msisdn.startsWith("0")) {
            return msisdn.replaceFirst("0", countryCodePrefix);
        }
        return msisdn;
    }

    public static long secondsBetween(final Date firstDate, final Date secondDate) {
        return abs((firstDate.getTime() - secondDate.getTime()) / 1000);
    }

    public static long minutesBetween(final Date firstDate, final Date secondDate) {
        return secondsBetween(firstDate, secondDate) / 60;
    }

    public static long hoursBetween(final Date firstDate, final Date secondDate) {
        return minutesBetween(firstDate, secondDate) / 60;
    }

    public static long daysBetween(final Date firstDate, final Date secondDate) {
        return hoursBetween(firstDate, secondDate) / 24;
    }

    public static long weeksBetween(final Date firstDate, final Date secondDate) {
        return daysBetween(firstDate, secondDate) / 7;
    }

	public static int round(final double d)
	{
		double dAbs = abs(d);
		int i = (int) dAbs;

		if((dAbs - (double)i) < 0.5)	return d < 0 ? -i		: i;		//negative value
		else							return d < 0 ? -(i+1)	: i + 1;	//positive value
	}

    public static String javaToJSRegex(String regex) {
        return '/' + regex + '/';
    }

    public static Throwable getRealThrowable(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) { cause = cause.getCause(); }
        return cause;
    }
}
