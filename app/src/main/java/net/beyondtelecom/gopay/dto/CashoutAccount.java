package net.beyondtelecom.gopay.dto;

/**
 * Created by Tich on 1/2/2018.
 */
public class CashoutAccount {

	private Integer cashoutAccountId;
	private FinancialInstitution financialInstitution;
	private String accountNickName;
	private String accountName;
	private String accountNumber;
	private String accountBranchCode;
	private String accountPhone;
	private String accountEmail;

	public CashoutAccount() {}

	public CashoutAccount(Integer cashoutAccountId, FinancialInstitution financialInstitution, String accountNickName,
						  String accountName, String accountNumber, String accountBranchCode,
						  String accountPhone, String accountEmail) {
		this.cashoutAccountId = cashoutAccountId;
		this.financialInstitution = financialInstitution;
		this.accountNickName = accountNickName;
		this.accountName = accountName;
		this.accountNumber = accountNumber;
		this.accountBranchCode = accountBranchCode;
		this.accountPhone = accountPhone;
		this.accountEmail = accountEmail;
	}

	public Integer getCashoutAccountId() {
		return cashoutAccountId;
	}

	public void setCashoutAccountId(Integer cashoutAccountId) {
		this.cashoutAccountId = cashoutAccountId;
	}

	public FinancialInstitution getFinancialInstitution() {
		return financialInstitution;
	}

	public void setFinancialInstitution(FinancialInstitution financialInstitution) {
		this.financialInstitution = financialInstitution;
	}

	public String getAccountNickName() {
		return accountNickName;
	}

	public void setAccountNickName(String accountNickName) {
		this.accountNickName = accountNickName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountBranchCode() {
		return accountBranchCode;
	}

	public void setAccountBranchCode(String accountBranchCode) {
		this.accountBranchCode = accountBranchCode;
	}

	public String getAccountPhone() {
		return accountPhone;
	}

	public void setAccountPhone(String accountPhone) {
		this.accountPhone = accountPhone;
	}

	public String getAccountEmail() {
		return accountEmail;
	}

	public void setAccountEmail(String accountEmail) {
		this.accountEmail = accountEmail;
	}
}

