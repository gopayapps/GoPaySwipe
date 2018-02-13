package net.beyondtelecom.gopay.dto;

import java.math.BigDecimal;

public class CashoutDetails {

	CashoutAccount cashoutAccount;
	String cashoutId;
	BigDecimal cashoutAmount;
	String cashoutCurrency;
	String cashoutReference;
	Integer cashoutPin;

	public CashoutAccount getCashoutAccount() {
		return cashoutAccount;
	}

	public void setCashoutAccount(CashoutAccount cashoutAccount) {
		this.cashoutAccount = cashoutAccount;
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
