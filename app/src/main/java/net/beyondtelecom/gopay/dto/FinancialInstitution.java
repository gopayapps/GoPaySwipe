package net.beyondtelecom.gopay.dto;

/***************************************************************************
 *                                                                         *
 * Created:     17 / 01 / 2018                                             *
 * Author:      Tsungai Kaviya                                             *
 * Contact:     tsungai.kaviya@gmail.com                                   *
 *                                                                         *
 ***************************************************************************/
public class FinancialInstitution {

    private Integer institutionId;
    private String institutionName;
    private InstitutionType institutionType;

    public FinancialInstitution(Integer institutionId, String institutionName, InstitutionType institutionType) {
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.institutionType = institutionType;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public InstitutionType getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(InstitutionType institutionType) {
        this.institutionType = institutionType;
    }
}
