package net.beyondtelecom.gopayswipe.dto;

import java.math.BigDecimal;

public class CashoutDetails {

	WalletAccount walletAccount;
	String cashoutId;
	BigDecimal cashoutAmount;
	String cashoutCurrency;
	String cashoutReference;
	Integer cashoutPin;

	public WalletAccount getWalletAccount() {
		return walletAccount;
	}

	public void setWalletAccount(WalletAccount walletAccount) {
		this.walletAccount = walletAccount;
	}

	public String getCashoutId() {
		return cashoutId;
	}

	public void setCashoutId(String cashoutId) {
		this.cashoutId = cashoutId;
	}

	public BigDecimal getCashoutAmount() {
		return cashoutAmount;
	}

	public void setCashoutAmount(BigDecimal cashoutAmount) {
		this.cashoutAmount = cashoutAmount;
	}

	public String getCashoutCurrency() {
		return cashoutCurrency;
	}

	public void setCashoutCurrency(String cashoutCurrency) {
		this.cashoutCurrency = cashoutCurrency;
	}

	public String getCashoutReference() {
		return cashoutReference;
	}

	public void setCashoutReference(String cashoutReference) {
		this.cashoutReference = cashoutReference;
	}

	public Integer getCashoutPin() {
		return cashoutPin;
	}

	public void setCashoutPin(Integer cashoutPin) {
		this.cashoutPin = cashoutPin;
	}
}
