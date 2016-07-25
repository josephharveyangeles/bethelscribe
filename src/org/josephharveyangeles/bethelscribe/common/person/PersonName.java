package org.josephharveyangeles.bethelscribe.common.person;

import com.creosoft.hecate.lang.string.StringUtils;

public class PersonName {

	private static final String PERIOD = ".";
	private static final String SPACE = " ";
	private static final String COMMA = ", ";
	private final String firstName;
	private final String middleName;
	private final String lastName;
	private final String suffix;

	public PersonName(String first, String middle, String last) {
		this(first, middle, last, "");
	}

	public PersonName(String first, String middle, String last, String suff) {
		firstName = first;
		middleName = middle;
		lastName = last;
		suffix = suff;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getShortFullName() {
		return StringUtils.implode(SPACE, StringUtils.capitalize(firstName),
				Character.toUpperCase(middleName.charAt(0)) + PERIOD, StringUtils.capitalize(lastName))
				+ getSuffixSection();
	}

	public String getLongFullName() {
		return StringUtils.implode(SPACE, StringUtils.capitalize(firstName), StringUtils.capitalize(middleName),
				StringUtils.capitalize(lastName)) + getSuffixSection();
	}

	public String getCanonicalName() {
		return StringUtils.implode(SPACE, StringUtils.capitalize(lastName) + COMMA, StringUtils.capitalize(firstName),
				StringUtils.capitalize(middleName)) + getSuffixSection();
	}

	public String getShortCanonicalName() {
		return StringUtils.implode(SPACE, StringUtils.capitalize(lastName) + COMMA, StringUtils.capitalize(firstName),
				Character.toUpperCase(middleName.charAt(0)) + PERIOD) + getSuffixSection();
	}

	private String getSuffixSection() {
		String suffixPart = "";
		if (!StringUtils.isNullOrEmpty(suffix)) {
			suffixPart = COMMA + suffix;
		}
		return suffixPart;
	}

}
