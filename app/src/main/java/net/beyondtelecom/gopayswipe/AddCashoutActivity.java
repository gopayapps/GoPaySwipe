package net.beyondtelecom.gopayswipe;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.beyondtelecom.gopayswipe.common.BTResponseCode;
import net.beyondtelecom.gopayswipe.dto.CashoutAccount;
import net.beyondtelecom.gopayswipe.dto.FinancialInstitution;
import net.beyondtelecom.gopayswipe.dto.InstitutionType;

import java.util.Hashtable;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.addCashoutAccount;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getFinancialInstitutions;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getTag;
import static net.beyondtelecom.gopayswipe.common.Validator.isNullOrEmpty;
import static net.beyondtelecom.gopayswipe.common.Validator.isNumeric;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidEmail;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidMsisdn;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidName;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidPlainText;
import static net.beyondtelecom.gopayswipe.dto.InstitutionType.BANK;
import static net.beyondtelecom.gopayswipe.dto.InstitutionType.MOBILE_BANK;
import static net.beyondtelecom.gopayswipe.dto.InstitutionType.ONLINE_BANK;

public class AddCashoutActivity extends AppCompatActivity {

	private static final String TAG = getTag(LoginActivity.class);
	private Activity cashoutActivity;
	private Spinner spnChooseBank;
	private EditText edtAccountNickname;
	private EditText edtAccountPhone;
	private EditText edtAccountEmail;
	private EditText edtBankAccountName;
	private EditText edtBankAccountNumber;
	private EditText edtBankBranch;
	private Button btnAddCashoutAccount;
	private LinearLayout layoutBankAccount;
	private InstitutionType cashoutInstitutionType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activty_add_cashout);
		cashoutActivity = this;

		spnChooseBank = (Spinner) findViewById(R.id.spnChooseBank);
		edtAccountNickname = (EditText) findViewById(R.id.edtAccountNickname);
		edtAccountPhone = (EditText) findViewById(R.id.edtAccountPhone);
		edtAccountEmail = (EditText) findViewById(R.id.edtAccountEmail);
		edtBankAccountName = (EditText) findViewById(R.id.edtBankAccountName);
		edtBankAccountNumber = (EditText) findViewById(R.id.edtBankAccountNumber);
		edtBankBranch = (EditText) findViewById(R.id.edtBankBranch);
		layoutBankAccount = (LinearLayout) findViewById(R.id.layoutBankAccount);
		btnAddCashoutAccount = (Button) findViewById(R.id.btnAddCashoutAccount);

		btnAddCashoutAccount.setOnClickListener(new AddBankCashoutListener());
		spnChooseBank.setOnItemSelectedListener(new ChooseBankListener());
		populateBankAccounts();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		populateBankAccounts();
	}

	class ChooseBankListener implements AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Hashtable<Integer, FinancialInstitution> financialInstitutions = getFinancialInstitutions(cashoutActivity);
			for (FinancialInstitution financialInstitution : financialInstitutions.values()) {
				if (financialInstitution.getInstitutionName().equals(spnChooseBank.getSelectedItem().toString())) {
					cashoutInstitutionType = financialInstitution.getInstitutionType();
					switch (cashoutInstitutionType) {
						case MOBILE_BANK:
							layoutBankAccount.setVisibility(View.GONE);
							break;
						case BANK:
							layoutBankAccount.setVisibility(View.VISIBLE);
							break;
						case ONLINE_BANK:
							layoutBankAccount.setVisibility(View.GONE);
							break;
					}
				}
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			layoutBankAccount.setVisibility(View.GONE);
		}
	}

	private void populateBankAccounts() {

		ArrayAdapter<CharSequence> bankAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
		Hashtable<Integer, FinancialInstitution> financialInstitutions = getFinancialInstitutions(this);
		if (financialInstitutions == null) { return; }
		for (FinancialInstitution financialInstitution : financialInstitutions.values()) {
			bankAdapter.add(financialInstitution.getInstitutionName());
		}
		spnChooseBank.setAdapter(bankAdapter);
	}

	private boolean isValidInputs(FinancialInstitution financialInstitution) {

		edtAccountNickname.setError(null);
		edtAccountPhone.setError(null);
		edtAccountEmail.setError(null);
		edtBankAccountName.setError(null);
		edtBankAccountNumber.setError(null);
		edtBankBranch.setError(null);

		if (isNullOrEmpty(edtAccountNickname.getText().toString())) {
			edtAccountNickname.setError(getString(R.string.error_field_required));
			return false;
		} else if (!isValidPlainText(edtAccountNickname.getText().toString())) {
			edtAccountNickname.setError(getString(R.string.error_invalid_name));
			return false;
		}

		if ((!isNullOrEmpty(edtAccountPhone.getText().toString()) ||
			financialInstitution.getInstitutionType().equals(MOBILE_BANK))
				&& !isValidMsisdn(edtAccountPhone.getText().toString())) {
			edtAccountPhone.setError("Mobile number is not valid");
			edtAccountPhone.requestFocus();
			return false;
		} else if ((!isNullOrEmpty(edtAccountEmail.getText().toString()) ||
			financialInstitution.getInstitutionType().equals(ONLINE_BANK))
				&& !isValidEmail(edtAccountEmail.getText().toString())) {
			edtAccountEmail.setError("Email is not valid");
			edtAccountEmail.requestFocus();
			return false;
		}

		if ((!isNullOrEmpty(edtBankAccountName.getText().toString()) ||
			financialInstitution.getInstitutionType().equals(BANK))
				&& !isValidName(edtBankAccountName.getText().toString())) {
			edtBankAccountName.setError("Account Name is not valid");
			edtBankAccountName.requestFocus();
			return false;
		} else if ((!isNullOrEmpty(edtBankAccountNumber.getText().toString()) ||
					financialInstitution.getInstitutionType().equals(BANK))
						&& !isNumeric(edtBankAccountNumber.getText().toString())) {
			edtBankAccountNumber.setError("Account Number is not valid");
			edtBankAccountNumber.requestFocus();
			return false;
		} else if (!isNullOrEmpty(edtBankBranch.getText().toString()) && !isNumeric(edtBankBranch.getText().toString())) {
			edtBankBranch.setError("Branch code is not valid");
			edtBankBranch.requestFocus();
			return false;
		}

		return true;
	}

	class AddBankCashoutListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {

			CashoutAccount cashoutAccount = new CashoutAccount();

			Hashtable<Integer, FinancialInstitution> financialInstitutions = getFinancialInstitutions(cashoutActivity);
			if (financialInstitutions == null) { return; }
			for (FinancialInstitution financialInstitution : financialInstitutions.values()) {
				if (financialInstitution.getInstitutionName().equals(spnChooseBank.getSelectedItem().toString())) {
					cashoutAccount.setFinancialInstitution(financialInstitution);
					break;
				}
			}

			if (!isValidInputs(cashoutAccount.getFinancialInstitution())) { return; }

			cashoutAccount.setAccountNickName(edtAccountNickname.getText().toString());
			cashoutAccount.setAccountPhone(isNullOrEmpty(edtAccountPhone.getText().toString()) ? null : edtAccountPhone.getText().toString());
			cashoutAccount.setAccountEmail(isNullOrEmpty(edtAccountEmail.getText().toString()) ? null : edtAccountEmail.getText().toString());

			switch (cashoutAccount.getFinancialInstitution().getInstitutionType()) {
				case MOBILE_BANK:
					cashoutAccount.setAccountNumber(cashoutAccount.getAccountPhone());
					break;
				case BANK:
					cashoutAccount.setAccountName(isNullOrEmpty(edtBankAccountName.getText().toString()) ? null : edtBankAccountName.getText().toString());
					cashoutAccount.setAccountNumber(isNullOrEmpty(edtBankAccountNumber.getText().toString()) ? null : edtBankAccountNumber.getText().toString());
					cashoutAccount.setAccountBranchCode(isNullOrEmpty(edtBankBranch.getText().toString()) ? null : edtBankBranch.getText().toString());
					break;
				case ONLINE_BANK:
					cashoutAccount.setAccountNumber(cashoutAccount.getAccountEmail());
					break;
			}

			final BTResponseCode addResponse = addCashoutAccount(cashoutActivity, cashoutAccount);
			if (addResponse.equals(BTResponseCode.SUCCESS)) {
				runOnUiThread(new Runnable() {
					public void run() {
						makeText(cashoutActivity, "Cashout Option Added Successfully", LENGTH_LONG).show();
					}
				});
				finish();
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						makeText(cashoutActivity, "Failed to add cashout option: " + addResponse.getMessage(), LENGTH_LONG).show();
					}
				});
			}
		}
	}
}

