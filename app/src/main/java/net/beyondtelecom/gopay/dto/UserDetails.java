package net.beyondtelecom.gopay.dto;

/**
 * Created by Tich on 1/2/2018.
 */
public class UserDetails {

	Long btUserId;
	Long walletId;
	String username;
	String firstName;
	String lastName;
	String companyName;
	String msisdn;
	String email;

	public UserDetails() {}

	public UserDetails(Long btUserId, Long walletId, String username, String firstName,
					   String lastName, String companyName, String msisdn, String email) {
		this.btUserId = btUserId;
		this.walletId = walletId;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.companyName = companyName;
		this.msisdn = msisdn;
		this.email = email;
	}

	public Long getBtUserId() {
		return btUserId;
	}

	public void setBtUserId(Long btUserId) {
		this.btUserId = btUserId;
	}

	public Long getWalletId() { return walletId; }

	public void setWalletId(Long walletId) { this.walletId = walletId; }

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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
}

