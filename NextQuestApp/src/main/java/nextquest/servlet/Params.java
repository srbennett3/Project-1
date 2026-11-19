package nextquest.servlet;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helpers that read request parameters. Each returns null when the value is
 * missing or blank, and throws IllegalArgumentException with a readable
 * message when the value is present but malformed.
 */
final class Params {

	private Params() {}

	static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	static String trimToNull(String value) {
		return isBlank(value) ? null : value.trim();
	}

	static Integer parseInt(String value, String name) {
		if (isBlank(value)) {
			return null;
		}
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(name + " must be a whole number.");
		}
	}

	static BigDecimal parseDecimal(String value, String name) {
		if (isBlank(value)) {
			return null;
		}
		try {
			return new BigDecimal(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(name + " must be a number.");
		}
	}

	/** Reads a yyyy-MM-dd date, the format an HTML date input sends. */
	static Date parseDate(String value, String name) {
		if (isBlank(value)) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setLenient(false);
		try {
			return format.parse(value.trim());
		} catch (ParseException e) {
			throw new IllegalArgumentException(name + " must be a date (yyyy-MM-dd).");
		}
	}

	/** "true" / "false" from a select; anything else means not answered. */
	static Boolean parseBoolean(String value) {
		if ("true".equalsIgnoreCase(value)) {
			return Boolean.TRUE;
		}
		if ("false".equalsIgnoreCase(value)) {
			return Boolean.FALSE;
		}
		return null;
	}

	static int parsePage(String value) {
		try {
			Integer page = parseInt(value, "page");
			return page == null ? 1 : Math.max(1, page);
		} catch (IllegalArgumentException e) {
			return 1;
		}
	}
}
