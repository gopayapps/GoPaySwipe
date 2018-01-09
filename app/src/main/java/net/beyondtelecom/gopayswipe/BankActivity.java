package net.beyondtelecom.gopayswipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import static net.beyondtelecom.gopayswipe.LoginActivity.getLoginActivity;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class BankActivity extends AppCompatActivity {

	protected Activity bankActivity;
	protected View mobileDetailsLayout;
	protected View bankDetailsLayout;
	protected Spinner chooseCashoutType;
	protected Spinner chooseBank;
	protected Button btnSkipCashout;
	protected Button btnAddMobileCashout;
	protected Button btnAddBankCashout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activty_cashout);
		bankActivity = this;

		mobileDetailsLayout = findViewById(R.id.detailsMobileBank);
		bankDetailsLayout = findViewById(R.id.detailsBankAccount);
		chooseCashoutType = (Spinner) findViewById(R.id.spnChooseCashoutType);
		chooseBank = (Spinner) findViewById(R.id.spnChooseBank);
		btnSkipCashout = (Button) findViewById(R.id.btnSkipCashout);
		btnAddMobileCashout = (Button) findViewById(R.id.btnAddMobileCashout);
		btnAddBankCashout = (Button) findViewById(R.id.btnAddBankCashout);
		btnSkipCashout.setOnClickListener(new SkipCashoutListener());
		btnAddMobileCashout.setOnClickListener(new AddMobileCashoutListener());
		btnAddBankCashout.setOnClickListener(new AddBankCashoutListener());

		populateBankAccounts();
	}

	private void populateBankAccounts() {
		ArrayAdapter<CharSequence> cashoutTypeAdapter = ArrayAdapter.createFromResource(this,
		R.array.cashout_type, android.R.layout.simple_spinner_item);
		cashoutTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		chooseCashoutType.setAdapter(cashoutTypeAdapter);
		chooseCashoutType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedCashoutType = (String) chooseCashoutType.getSelectedItem();

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

		ArrayAdapter<CharSequence> bankAdapter = ArrayAdapter.createFromResource(this,
		R.array.bank_name, android.R.layout.simple_spinner_item);
		bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		chooseBank.setAdapter(bankAdapter);
	}

	class SkipCashoutListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Intent swipeIntent = new Intent(getLoginActivity(), ChargeActivity.class);
			startActivity(swipeIntent);
		}
	}

	class AddMobileCashoutListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Intent swipeIntent = new Intent(getLoginActivity(), ChargeActivity.class);
			startActivity(swipeIntent);
		}
	}

	class AddBankCashoutListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Intent swipeIntent = new Intent(getLoginActivity(), ChargeActivity.class);
			startActivity(swipeIntent);
		}
	}
}

