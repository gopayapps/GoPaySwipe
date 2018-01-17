package net.beyondtelecom.gopayswipe.dto;

/***************************************************************************
 *                                                                         *
 * Created:     17 / 01 / 2018                                             *
 * Author:      Tsungai Kaviya                                             *
 * Contact:     tsungai.kaviya@gmail.com                                   *
 *                                                                         *
 ***************************************************************************/
public class BankType {

    private Integer bankTypeId;
    private String bankTypeName;

    public BankType(Integer bankTypeId, String bankTypeName) {
        this.bankTypeId = bankTypeId;
        this.bankTypeName = bankTypeName;
    }

    public Integer getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(Integer bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public String getBankTypeName() {
        return bankTypeName;
    }

    public void setBankTypeName(String bankTypeName) {
        this.bankTypeName = bankTypeName;
    }
}
