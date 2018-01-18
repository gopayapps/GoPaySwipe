package net.beyondtelecom.gopayswipe.dto;

/***************************************************************************
 *                                                                         *
 * Created:     17 / 01 / 2018                                             *
 * Author:      Tsungai Kaviya                                             *
 * Contact:     tsungai.kaviya@gmail.com                                   *
 *                                                                         *
 ***************************************************************************/
public class CashoutOption {

    private Integer cashoutOptionId;
    private String cashoutOptionName;
    private AccountType accountType;

    public CashoutOption(Integer cashoutOptionId, String cashoutOptionName, AccountType accountType) {
        this.cashoutOptionId = cashoutOptionId;
        this.cashoutOptionName = cashoutOptionName;
        this.accountType = accountType;
    }

    public Integer getCashoutOptionId() {
        return cashoutOptionId;
    }

    public void setCashoutOptionId(Integer cashoutOptionId) {
        this.cashoutOptionId = cashoutOptionId;
    }

    public String getCashoutOptionName() {
        return cashoutOptionName;
    }

    public void setCashoutOptionName(String cashoutOptionName) {
        this.cashoutOptionName = cashoutOptionName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
