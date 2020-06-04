package btm.java.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
	private static final String DATE_REG = "[0-9]{1,2}/[0-9]{1,2}/[0-9]{4,4}";
	private static Pattern p;
	private static Matcher m;

	public static Boolean isDDMMYYYY(String date) {
		p = Pattern.compile(DATE_REG);
		m = p.matcher(date);
		return m.find();
	}
}
