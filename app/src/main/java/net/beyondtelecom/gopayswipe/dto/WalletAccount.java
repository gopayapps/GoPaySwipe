package net.beyondtelecom.gopayswipe.dto;

/**
 * Created by Tich on 1/2/2018.
 */
public class WalletAccount {

	private Integer walletAccountId;
	private CashoutOption cashoutOption;
	private String walletNickname;
	private String walletName;
	private String walletAccountNumber;
	private String walletAccountBranch;
	private String walletPhone;
	private String walletEmail;

	public WalletAccount() {}

	public WalletAccount(Integer walletAccountId, CashoutOption cashoutOption, String walletNickname,
						 String walletName, String walletAccountNumber, String walletAccountBranch,
						 String walletPhone, String walletEmail) {
		this.walletAccountId = walletAccountId;
		this.cashoutOption = cashoutOption;
		this.walletNickname = walletNickname;
		this.walletName = walletName;
		this.walletAccountNumber = walletAccountNumber;
		this.walletAccountBranch = walletAccountBranch;
		this.walletPhone = walletPhone;
		this.walletEmail = walletEmail;
	}

	public Integer getWalletAccountId() {
		return walletAccountId;
	}

	public void setWalletAccountId(Integer walletAccountId) {
		this.walletAccountId = walletAccountId;
	}

	public CashoutOption getCashoutOption() {
		return cashoutOption;
	}

	public void setCashoutOption(CashoutOption cashoutOption) {
		this.cashoutOption = cashoutOption;
	}

	public String getWalletNickname() {
		return walletNickname;
	}

	public void setWalletNickname(String walletNickname) {
		this.walletNickname = walletNickname;
	}

	public String getWalletName() {
		return walletName;
	}

	public void setWalletName(String walletName) {
		this.walletName = walletName;
	}

	public String getWalletAccountNumber() {
		return walletAccountNumber;
	}

	public void setWalletAccountNumber(String walletAccountNumber) {
		this.walletAccountNumber = walletAccountNumber;
	}

	public String getWalletAccountBranch() {
		return walletAccountBranch;
	}

	public void setWalletAccountBranch(String walletAccountBranch) {
		this.walletAccountBranch = walletAccountBranch;
	}

	public String getWalletPhone() {
		return walletPhone;
	}

	public void setWalletPhone(String walletPhone) {
		this.walletPhone = walletPhone;
	}

	public String getWalletEmail() {
		return walletEmail;
	}

	public void setWalletEmail(String walletEmail) {
		this.walletEmail = walletEmail;
	}
}

