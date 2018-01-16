package net.beyondtelecom.gopayswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.beyondtelecom.gopayswipe.common.ActivityCommon;
import net.beyondtelecom.gopayswipe.common.BTResponseCode;
import net.beyondtelecom.gopayswipe.common.UserDetails;
import net.beyondtelecom.gopayswipe.common.Validator;
import net.beyondtelecom.gopayswipe.persistence.GPPersistence;
import net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopayswipe.BankActivity.getBankActivity;
import static net.beyondtelecom.gopayswipe.LoginActivity.getLoginActivity;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getDeviceIMEI;
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.TASK_TYPE.POST;

public class RegisterActivity extends AppCompatActivity {

	protected static final String TAG = ActivityCommon.getTag(RegisterActivity.class);
	protected RegisterActivity registerActivity;
	private ProgressBar progressBar;
	private EditText txtRegUsername;
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
		registerActivity = this;
		progressBar = (ProgressBar) findViewById(R.id.prgRegister);

		txtRegUsername = (EditText) findViewById(R.id.txtRegUsername);
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

		txtRegUsername.setError(null);
		txtRegFirstname.setError(null);
		txtRegLastname.setError(null);
		txtRegMsisdn.setError(null);
		txtRegEmail.setError(null);
		txtRegPin.setError(null);
		txtRegRPin.setError(null);

		String username = txtRegUsername.getText().toString();
		String fname = txtRegFirstname.getText().toString();
		String lname = txtRegLastname.getText().toString();
		String msisdn = txtRegMsisdn.getText().toString();
		String email = txtRegEmail.getText().toString();
		String pin = txtRegPin.getText().toString();
		String rpin = txtRegRPin.getText().toString();

		if (!TextUtils.isEmpty(username) && !Validator.isValidUsername(username)) {
			txtRegUsername.setError(getString(R.string.error_invalid_username));
			txtRegUsername.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(username)) {
			txtRegUsername.setError(getString(R.string.error_field_required));
			txtRegUsername.requestFocus();
			return false;
		}

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


	class RegisterListener implements TextView.OnEditorActionListener, View.OnClickListener {

		@Override
		public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
			if (id == R.id.btnLogin || id == EditorInfo.IME_NULL) {
				attemptRegister();
				return true;
			}
			return false;
		}

		@Override
		public void onClick(View view) {
			attemptRegister();
		}
	}

	public void attemptRegister() {

		if (isValidInputs()) {

			final Hashtable registerParams = new Hashtable<>();
			registerParams.put("username", txtRegUsername.getText().toString());
			registerParams.put("firstName", txtRegFirstname.getText().toString());
			registerParams.put("lastName", txtRegLastname.getText().toString());
			registerParams.put("msisdn", txtRegMsisdn.getText().toString());
			registerParams.put("email", txtRegEmail.getText().toString());
			registerParams.put("deviceId", getDeviceIMEI(this));
			registerParams.put("pin", txtRegPin.getText().toString());

			final HTTPBackgroundTask registerTask = new HTTPBackgroundTask(
				this, POST, HTTPBackgroundTask.REGISTER_URL, registerParams
			);

			runOnUiThread(new Runnable() {
				public void run() {

					BTResponseCode registerResponse;
					try {
						registerResponse = registerTask.execute().get();
					} catch (InterruptedException e) {
						e.printStackTrace();
						registerResponse = BTResponseCode.GENERAL_ERROR.setMessage("Registration interrupted");
					} catch (ExecutionException e) {
						e.printStackTrace();
						registerResponse = BTResponseCode.GENERAL_ERROR.setMessage("Registration failed: " + e.getMessage());
					}

					final String responseMsg = registerResponse.getMessage();

					Log.i(TAG, "Registration Response: " + responseMsg);

					if (registerResponse.equals(SUCCESS)) {
						UserDetails newUserDetails = new UserDetails(
							txtRegUsername.getText().toString(),
							txtRegFirstname.getText().toString(),
							txtRegLastname.getText().toString(),
							txtRegMsisdn.getText().toString(),
							txtRegEmail.getText().toString(),
							txtRegPin.getText().toString()
						);
						GPPersistence goPayDB = LoginActivity.getLoginActivity().getGoPayDB();
						goPayDB.setUserDetails(newUserDetails);
						finish();
						Intent bankIntent = new Intent(getLoginActivity(), BankActivity.class);
						startActivity(bankIntent);
						runOnUiThread(new Runnable() {
							public void run() {
								makeText(getBankActivity(), "Registration successful.", LENGTH_LONG).show();
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() { makeText(registerActivity, responseMsg, LENGTH_LONG).show(); }
						});
					}
				}
			});
		}
	}
}
