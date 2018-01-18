package net.beyondtelecom.gopayswipe;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.Enumeration;
import java.util.Hashtable;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopayswipe.LoginActivity.getGoPayDB;
import static net.beyondtelecom.gopayswipe.common.Validator.isNullOrEmpty;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidEmail;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidMsisdn;

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
	private Activity bankActivity;
	private static Hashtable<Integer, CashoutOption> cashoutOptions;
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
		bankActivity = this;

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
			Hashtable<Integer, CashoutOption> cashoutOptions = getCashoutOptions();
			Enumeration<Integer> keys = cashoutOptions.keys();
			while (keys.hasMoreElements()) {
				CashoutOption cashoutOption = cashoutOptions.get(keys.nextElement());
				if (cashoutOption.getCashoutOptionName().equals(spnChooseBank.getSelectedItem().toString())) {
					cashoutAccountType = cashoutOption.getAccountType();
					switch (cashoutAccountType) {
						case MOBILE_BANK_ACCOUNT:
							layoutBankAccount.setVisibility(View.GONE);
							break;
						case BANK_ACCOUNT:
							layoutBankAccount.setVisibility(View.VISIBLE);
							break;
						case ONLINE_BANK_ACCOUNT:
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

		Hashtable<Integer, CashoutOption> cashoutOptions = getCashoutOptions();
		ArrayAdapter<CharSequence> bankAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

		Enumeration<Integer> keys = cashoutOptions.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			bankAdapter.add(cashoutOptions.get(key).getCashoutOptionName());
		}

		spnChooseBank.setAdapter(bankAdapter);
	}

	class AddBankCashoutListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			WalletAccount walletAccount = new WalletAccount();
			Hashtable<Integer, CashoutOption> cashoutOptions = getCashoutOptions();
			Enumeration<Integer> keys = cashoutOptions.keys();
			while (keys.hasMoreElements()) {
				int key = keys.nextElement();
				if (cashoutOptions.get(key).getCashoutOptionName().equals(spnChooseBank.getSelectedItem().toString())) {
					walletAccount.setCashoutOption(cashoutOptions.get(key));
					break;
				}
			}

			walletAccount.setWalletNickname(edtAccountNickname.getText().toString());

			if (!isNullOrEmpty(edtAccountPhone.getText().toString()) && !isValidMsisdn(edtAccountPhone.getText().toString())) {
				edtAccountPhone.setError("Mobile number is not valid");
				edtAccountPhone.requestFocus();
				return;
			} else if (!isNullOrEmpty(edtAccountEmail.getText().toString()) && !isValidEmail(edtAccountEmail.getText().toString())) {
				edtAccountEmail.setError("Email is not valid");
				edtAccountEmail.requestFocus();
				return;
			}

			walletAccount.setWalletPhone(edtAccountPhone.getText().toString());
			walletAccount.setWalletEmail(edtAccountEmail.getText().toString());

			switch (walletAccount.getCashoutOption().getAccountType()) {
				case MOBILE_BANK_ACCOUNT:
					edtAccountPhone.setError(null);
					if (!isValidMsisdn(walletAccount.getWalletPhone())) {
						edtAccountPhone.setError("Mobile number is not valid");
						edtAccountPhone.requestFocus();
						return;
					}
					walletAccount.setWalletAccountNumber(walletAccount.getWalletPhone());
					break;
				case BANK_ACCOUNT:
					walletAccount.setWalletName(edtBankAccountName.getText().toString());
					walletAccount.setWalletAccountNumber(edtBankAccountNumber.getText().toString());
					walletAccount.setWalletAccountBranch(edtBankBranch.getText().toString());
					break;
				case ONLINE_BANK_ACCOUNT:
					if (!isValidEmail(walletAccount.getWalletEmail())) {
						edtAccountEmail.setError("Email is not valid");
						edtAccountEmail.requestFocus();
						return;
					}
					walletAccount.setWalletAccountNumber(edtAccountEmail.getText().toString());
					break;
			}

			getGoPayDB().insertWalletAccount(walletAccount);
			runOnUiThread(new Runnable() {
				public void run() { makeText(bankActivity, "Cashout Option Added Successfully", LENGTH_LONG).show(); }
			});

			finish();
		}
	}

	public Hashtable<Integer, CashoutOption> getCashoutOptions() {
		if (cashoutOptions == null && getGoPayDB() != null) {
			Log.i(TAG, "Checking for cashout options");
			cashoutOptions = getGoPayDB().getCashoutOptions();
			if (cashoutOptions != null) {
				Log.i(TAG, "Loaded " + cashoutOptions.size() + " cashout options");
			}
		}
		return cashoutOptions;
	}
}

