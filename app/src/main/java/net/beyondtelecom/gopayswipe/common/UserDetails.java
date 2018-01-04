package net.beyondtelecom.gopayswipe.common;

/**
 * Created by Tich on 1/2/2018.
 */
public class UserDetails {

	String firstName;
	String lastName;
	String msisdn;
	String email;
	String pin;
	String rpin;

	public UserDetails(String firstName, String lastName, String msisdn, String email,
					   String pin, String rpin) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.msisdn = msisdn;
		this.email = email;
		this.pin = pin;
		this.rpin = rpin;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getRpin() {
		return rpin;
	}

	public void setRpin(String rpin) {
		this.rpin = rpin;
	}
}

