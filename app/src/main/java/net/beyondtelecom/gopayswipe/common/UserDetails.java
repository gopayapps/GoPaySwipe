package net.beyondtelecom.gopayswipe.common;

/**
 * Created by Tich on 1/2/2018.
 */
public class UserDetails {

	String username;
	String firstName;
	String lastName;
	String msisdn;
	String email;
	String pin;

	public UserDetails() {}

	public UserDetails(String username, String firstName, String lastName, String msisdn,
					   String email, String pin) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.msisdn = msisdn;
		this.email = email;
		this.pin = pin;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
}

