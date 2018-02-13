package net.beyondtelecom.gopay.dto;

/***************************************************************************
 *                                                                         *
 * Created:     17 / 01 / 2018                                             *
 * Author:      Tsungai Kaviya                                             *
 * Contact:     tsungai.kaviya@gmail.com                                   *
 *                                                                         *
 ***************************************************************************/
public class CurrencyType {

    private Integer currencyTypeId;
    private String currencyTypeName;

    public CurrencyType(Integer currencyTypeId, String currencyTypeName) {
        this.currencyTypeId = currencyTypeId;
        this.currencyTypeName = currencyTypeName;
    }

    public Integer getCurrencyTypeId() {
        return currencyTypeId;
    }

    public void setCurrencyTypeId(Integer currencyTypeId) {
        this.currencyTypeId = currencyTypeId;
    }

    public String getCurrencyTypeName() {
        return currencyTypeName;
    }

    public void setCurrencyTypeName(String currencyTypeName) {
        this.currencyTypeName = currencyTypeName;
    }
}
