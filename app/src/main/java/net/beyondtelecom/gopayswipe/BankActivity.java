package net.beyondtelecom.gopayswipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import net.beyondtelecom.gopayswipe.common.ActivityCommon;
import net.beyondtelecom.gopayswipe.common.Validator;
import net.beyondtelecom.gopayswipe.dto.BankType;
import net.beyondtelecom.gopayswipe.dto.CashoutDetails;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopayswipe.LoginActivity.getGoPayDB;
import static net.beyondtelecom.gopayswipe.dto.AccountType.BANK_ACCOUNT;
import static net.beyondtelecom.gopayswipe.dto.AccountType.MOBILE_BANK_ACCOUNT;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class BankActivity extends AppCompatActivity {

	private static final String TAG = ActivityCommon.getTag(LoginActivity.class);
	private Activity bankActivity;
	private static ArrayList<BankType> bankDetails;
	private View mobileDetailsLayout;
	private View bankDetailsLayout;
	private Spinner spnChooseCashoutType;
	private Spinner spnChooseBank;
	private Button btnSkipCashout;
	private EditText edtCashoutMobileNumber;
	private EditText edtBankAccountName;
	private EditText edtBankAccountNumber;
	private EditText edtBankAccountPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activty_cashout);
		bankActivity = this;

		mobileDetailsLayout = findViewById(R.id.detailsMobileBank);
		bankDetailsLayout = findViewById(R.id.detailsBankAccount);
		spnChooseCashoutType = (Spinner) findViewById(R.id.spnChooseCashoutType);
		spnChooseBank = (Spinner) findViewById(R.id.spnChooseBank);
		btnSkipCashout = (Button) findViewById(R.id.btnSkipCashout);
		Button btnAddMobileCashout = (Button) findViewById(R.id.btnAddMobileCashout);
		Button btnAddBankCashout = (Button) findViewById(R.id.btnAddBankCashout);
		edtCashoutMobileNumber = (EditText) findViewById(R.id.edtMobileCashoutNumber);
		edtBankAccountName = (EditText) findViewById(R.id.edtBankAccountName);
		edtBankAccountNumber = (EditText) findViewById(R.id.edtBankAccountNumber);
		edtBankAccountPhone = (EditText) findViewById(R.id.edtBankAccountPhone);

		btnSkipCashout.setOnClickListener(new SkipCashoutListener());
		btnAddMobileCashout.setOnClickListener(new AddMobileCashoutListener());
		btnAddBankCashout.setOnClickListener(new AddBankCashoutListener());

		populateBankAccounts();
	}

	public Activity getBankActivity() {
		return bankActivity;
	}

	private void populateBankAccounts() {

		ArrayAdapter<CharSequence> cashoutTypeAdapter = ArrayAdapter.createFromResource(this,
		R.array.cashout_type, android.R.layout.simple_spinner_item);
		cashoutTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnChooseCashoutType.setAdapter(cashoutTypeAdapter);
		spnChooseCashoutType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedCashoutType = (String) spnChooseCashoutType.getSelectedItem();

				if (selectedCashoutType.equalsIgnoreCase("No Cashout Option")) {
					btnSkipCashout.setVisibility(View.VISIBLE);
					mobileDetailsLayout.setVisibility(View.GONE);
					bankDetailsLayout.setVisibility(View.GONE);
				} else if (selectedCashoutType.equalsIgnoreCase("Local Bank Account")) {
					btnSkipCashout.setVisibility(View.GONE);
					mobileDetailsLayout.setVisibility(View.GONE);
					bankDetailsLayout.setVisibility(View.VISIBLE);
				} else {
					btnSkipCashout.setVisibility(View.GONE);
					mobileDetailsLayout.setVisibility(View.VISIBLE);
					bankDetailsLayout.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mobileDetailsLayout.setVisibility(View.GONE);
				bankDetailsLayout.setVisibility(View.GONE);
			}
		});

		ArrayList<BankType> bankDetails = getBankDetails();
		ArrayAdapter<CharSequence> bankAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
		for (BankType bankDetail : bankDetails) {
			bankAdapter.add(bankDetail.getBankTypeName());
		}
		spnChooseBank.setAdapter(bankAdapter);
//		for (BankType bankDetail : bankDetails) {
//			TextView textView = new TextView(this);
//			textView.setText(bankDetail.getCurrencyTypeName());
//			spnChooseBank.addView(textView);
//		}

//		ArrayAdapter<CharSequence> bankAdapter = ArrayAdapter.createFromResource(this,
//		R.array.bank_name, android.R.layout.simple_spinner_item);
//		bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spnChooseBank.setAdapter(bankAdapter);

	}

	class SkipCashoutListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Intent swipeIntent = new Intent(bankActivity, ChargeActivity.class);
			startActivity(swipeIntent);
			finish();
		}
	}

	class AddMobileCashoutListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {

			edtCashoutMobileNumber.setError(null);
			CashoutDetails mobileCashout = new CashoutDetails(MOBILE_BANK_ACCOUNT);

			String msisdn = edtCashoutMobileNumber.getText().toString();
			if (!Validator.isValidMsisdn(msisdn)) {
				edtCashoutMobileNumber.setError("Mobile number is not valid");
				edtCashoutMobileNumber.requestFocus();
				return;
			}

			mobileCashout.setAccountNumber(edtCashoutMobileNumber.getText().toString());
			mobileCashout.setAccountNumber(msisdn);
			mobileCashout.setAccountPhone(msisdn);

			Intent swipeIntent = new Intent(bankActivity, ChargeActivity.class);
			startActivity(swipeIntent);
			finish();
		}
	}

	public ArrayList<BankType> getBankDetails() {
		if (bankDetails == null && getGoPayDB() != null) {
			Log.i(TAG, "Checking for bank details");
			bankDetails = getGoPayDB().getBankTypes();
			if (bankDetails != null) {
				Log.i(TAG, "Loaded " + bankDetails.size() + " banks");
			}
		}
		return bankDetails;
	}

	class AddBankCashoutListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {

			CashoutDetails cashoutDetails = new CashoutDetails(BANK_ACCOUNT);

			for (BankType bankType : getBankDetails()) {
				if (bankType.getBankTypeName().equals(spnChooseBank.getSelectedItem().toString())) {
					cashoutDetails.setBankType(bankType);
					break;
				}
			}

			cashoutDetails.setAccountType(BANK_ACCOUNT);
			cashoutDetails.setAccountName(edtBankAccountName.getText().toString());
			cashoutDetails.setAccountNumber(edtBankAccountNumber.getText().toString());
			cashoutDetails.setAccountPhone(edtBankAccountPhone.getText().toString());

			getGoPayDB().insertCashoutDetails(cashoutDetails);

			runOnUiThread(new Runnable() {
				public void run() { makeText(bankActivity, "Bank Account Added Successfully", LENGTH_LONG).show(); }
			});

			Intent swipeIntent = new Intent(bankActivity, ChargeActivity.class);
			startActivity(swipeIntent);
			finish();
		}
	}
}

