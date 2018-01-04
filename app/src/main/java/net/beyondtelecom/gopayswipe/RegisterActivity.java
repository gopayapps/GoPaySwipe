package net.beyondtelecom.gopayswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.beyondtelecom.gopayswipe.common.Validator;

public class RegisterActivity extends AppCompatActivity {

	private EditText txtRegFirstname;
	private EditText txtRegLastname;
	private EditText txtRegMsisdn;
	private EditText txtRegEmail;
	private EditText txtRegPin;
	private EditText txtRegRPin;
	private Button btnRegRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		txtRegFirstname = (EditText) findViewById(R.id.txtRegFirstname);
		txtRegLastname = (EditText) findViewById(R.id.txtRegLastname);
		txtRegMsisdn = (EditText) findViewById(R.id.txtRegMsisdn);
		txtRegEmail = (EditText) findViewById(R.id.txtRegEmail);
		txtRegPin = (EditText) findViewById(R.id.txtRegPin);
		txtRegRPin = (EditText) findViewById(R.id.txtRegRPin);


		btnRegRegister = (Button) findViewById(R.id.btnRegRegister);
		btnRegRegister.setOnClickListener(new RegisterListener());

	}

	public boolean isValidInputs() {


		txtRegFirstname.setError(null);
		txtRegLastname.setError(null);
		txtRegMsisdn.setError(null);
		txtRegEmail.setError(null);
		txtRegPin.setError(null);
		txtRegRPin.setError(null);

		String fname = txtRegFirstname.getText().toString();
		String lname = txtRegLastname.getText().toString();
		String msisdn = txtRegMsisdn.getText().toString();
		String email = txtRegEmail.getText().toString();
		String pin = txtRegPin.getText().toString();
		String rpin = txtRegRPin.getText().toString();

		if (!TextUtils.isEmpty(fname) && !Validator.isValidName(fname)) {
			txtRegFirstname.setError(getString(R.string.error_invalid_name));
			txtRegFirstname.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(fname)) {
			txtRegFirstname.setError(getString(R.string.error_field_required));
			txtRegFirstname.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(lname) && !Validator.isValidName(lname)) {
			txtRegLastname.setError(getString(R.string.error_invalid_name));
			txtRegLastname.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(lname)) {
			txtRegLastname.setError(getString(R.string.error_field_required));
			txtRegLastname.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(msisdn) && !Validator.isValidMsisdn(msisdn)) {
			txtRegMsisdn.setError(getString(R.string.error_invalid_msisdn));
			txtRegMsisdn.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(msisdn)) {
			txtRegMsisdn.setError(getString(R.string.error_field_required));
			txtRegMsisdn.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(email) && !Validator.isValidEmail(email)) {
			txtRegEmail.setError(getString(R.string.error_invalid_email));
			txtRegEmail.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(pin) && !Validator.isValidPin(pin)) {
			txtRegPin.setError(getString(R.string.error_invalid_pin));
			txtRegPin.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(pin)) {
			txtRegPin.setError(getString(R.string.error_field_required));
			txtRegPin.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(rpin) && !Validator.isValidPin(rpin)) {
			txtRegRPin.setError(getString(R.string.error_invalid_pin));
			txtRegRPin.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(rpin)) {
			txtRegRPin.setError(getString(R.string.error_field_required));
			txtRegRPin.requestFocus();
			return false;
		} else if (!pin.matches(rpin)) {
			txtRegRPin.setError(getString(R.string.error_pin_must_match));
			txtRegRPin.requestFocus();
			return false;
		}

		return true;
	}


	class RegisterListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			if (isValidInputs()) {
				Intent swipeIntent = new Intent(LoginActivity.getLoginActivity(), ChargeActivity.class);
				startActivity(swipeIntent);
			}

		}
	}

}
