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

import net.beyondtelecom.gopayswipe.common.ActivityCommon;
import net.beyondtelecom.gopayswipe.dto.AccountType;
import net.beyondtelecom.gopayswipe.dto.CashoutOption;
import net.beyondtelecom.gopayswipe.dto.WalletAccount;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopayswipe.LoginActivity.getGoPayDB;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getCashoutOptions;
import static net.beyondtelecom.gopayswipe.common.Validator.isNullOrEmpty;
import static net.beyondtelecom.gopayswipe.common.Validator.isNumeric;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidEmail;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidMsisdn;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidName;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class AddCashoutActivity extends AppCompatActivity {

	private static final String TAG = ActivityCommon.getTag(LoginActivity.class);
	private Activity cashoutActivity;
//	private static Hashtable<Integer, CashoutOption> cashoutOptions;
	private Spinner spnChooseBank;
	private EditText edtAccountNickname;
	private EditText edtAccountPhone;
	private EditText edtAccountEmail;
	private EditText edtBankAccountName;
	private EditText edtBankAccountNumber;
	private EditText edtBankBranch;
	private Button btnAddCashoutAccount;
	private LinearLayout layoutBankAccount;
	private AccountType cashoutAccountType;

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
			ArrayList<CashoutOption> cashoutOptions = getCashoutOptions(cashoutActivity);
			for (CashoutOption cashoutOption : cashoutOptions) {
				if (cashoutOption.getCashoutOptionName().equals(spnChooseBank.getSelectedItem().toString())) {
					cashoutAccountType = cashoutOption.getAccountType();
					switch (cashoutAccountType) {
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
		ArrayList<CashoutOption> cashoutOptions = getCashoutOptions(this);
		if (cashoutOptions == null) { return; }
		for (CashoutOption cashoutOption : cashoutOptions) {
			bankAdapter.add(cashoutOption.getCashoutOptionName());
		}
		spnChooseBank.setAdapter(bankAdapter);
	}

	class AddBankCashoutListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			WalletAccount walletAccount = new WalletAccount();

			ArrayList<CashoutOption> cashoutOptions = getCashoutOptions(cashoutActivity);
			if (cashoutOptions == null) { return; }
			for (CashoutOption cashoutOption : cashoutOptions) {
				if (cashoutOption.getCashoutOptionName().equals(spnChooseBank.getSelectedItem().toString())) {
					walletAccount.setCashoutOption(cashoutOption);
					break;
				}
			}

			walletAccount.setWalletNickname(isNullOrEmpty(edtAccountNickname.getText().toString()) ? null : edtAccountNickname.getText().toString());

			if (!isNullOrEmpty(edtAccountPhone.getText().toString()) && !isValidMsisdn(edtAccountPhone.getText().toString())) {
				edtAccountPhone.setError("Mobile number is not valid");
				edtAccountPhone.requestFocus();
				return;
			} else if (!isNullOrEmpty(edtAccountEmail.getText().toString()) && !isValidEmail(edtAccountEmail.getText().toString())) {
				edtAccountEmail.setError("Email is not valid");
				edtAccountEmail.requestFocus();
				return;
			}

			walletAccount.setWalletPhone(isNullOrEmpty(edtAccountPhone.getText().toString()) ? null : edtAccountPhone.getText().toString());
			walletAccount.setWalletEmail(isNullOrEmpty(edtAccountEmail.getText().toString()) ? null : edtAccountEmail.getText().toString());

			switch (walletAccount.getCashoutOption().getAccountType()) {
				case MOBILE_BANK:
					edtAccountPhone.setError(null);
					if (!isValidMsisdn(walletAccount.getWalletPhone())) {
						edtAccountPhone.setError("Mobile number is not valid");
						edtAccountPhone.requestFocus();
						return;
					}
					walletAccount.setWalletAccountNumber(walletAccount.getWalletPhone());
					break;
				case BANK:

					if (!isNullOrEmpty(edtBankAccountName.getText().toString()) && !isValidName(edtBankAccountName.getText().toString())) {
						edtBankAccountName.setError("Account Name is not valid");
						edtBankAccountName.requestFocus();
						return;
					} else if (!isNullOrEmpty(edtBankAccountNumber.getText().toString()) && !isNumeric(edtBankAccountNumber.getText().toString())) {
						edtBankAccountNumber.setError("Account Number is not valid");
						edtBankAccountNumber.requestFocus();
						return;
					} else if (!isNullOrEmpty(edtBankBranch.getText().toString()) && !isNumeric(edtBankBranch.getText().toString())) {
						edtBankBranch.setError("Branch code is not valid");
						edtBankBranch.requestFocus();
						return;
					}
					walletAccount.setWalletName(isNullOrEmpty(edtBankAccountName.getText().toString()) ? null : edtBankAccountName.getText().toString());
					walletAccount.setWalletAccountNumber(isNullOrEmpty(edtBankAccountNumber.getText().toString()) ? null : edtBankAccountNumber.getText().toString());
					walletAccount.setWalletAccountBranch(isNullOrEmpty(edtBankBranch.getText().toString()) ? null : edtBankBranch.getText().toString());
					break;
				case ONLINE_BANK:
					if (!isValidEmail(walletAccount.getWalletEmail())) {
						edtAccountEmail.setError("Email is not valid");
						edtAccountEmail.requestFocus();
						return;
					}
					walletAccount.setWalletAccountNumber(walletAccount.getWalletEmail());
					break;
			}

			getGoPayDB().insertWalletAccount(walletAccount);
			runOnUiThread(new Runnable() {
				public void run() { makeText(cashoutActivity, "Cashout Option Added Successfully", LENGTH_LONG).show(); }
			});

			finish();
		}
	}

//	public Hashtable<Integer, CashoutOption> getCashoutOptions() {
//		if (cashoutOptions == null && getGoPayDB() != null) {
//			Log.i(TAG, "Checking for cashout options");
//			cashoutOptions = getGoPayDB().getCashoutOptions();
//			if (cashoutOptions != null) {
//				Log.i(TAG, "Loaded " + cashoutOptions.size() + " cashout options");
//			}
//		}
//		return cashoutOptions;
//	}
}

