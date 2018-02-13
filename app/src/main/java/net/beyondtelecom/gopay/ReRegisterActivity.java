package net.beyondtelecom.gopay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.beyondtelecom.gopay.common.ActivityCommon;
import net.beyondtelecom.gopay.common.Validator;

public class ReRegisterActivity extends AppCompatActivity {

	protected static final String TAG = ActivityCommon.getTag(ReRegisterActivity.class);
	protected ReRegisterActivity reRegisterActivity;
	private TextView txtReRegUsername;
	private TextView txtReRegName;
	private TextView txtReRegMsisdn;
	private TextView txtReRegEmail;
	private EditText txtReRegPassword;
	private EditText txtReRegPin;
	private EditText txtReRegRPin;
	private Button btnReRegRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		reRegisterActivity = this;

		txtReRegUsername = (TextView) findViewById(R.id.txtReRegUsername);
		txtReRegName = (TextView) findViewById(R.id.txtReRegName);
		txtReRegMsisdn = (TextView) findViewById(R.id.txtReRegMsisdn);
		txtReRegEmail = (TextView) findViewById(R.id.txtReRegEmail);
		txtReRegPassword = (EditText) findViewById(R.id.txtReRegPassword);
		txtReRegPin = (EditText) findViewById(R.id.txtReRegPin);
		txtReRegRPin = (EditText) findViewById(R.id.txtReRegRPin);
		btnReRegRegister = (Button) findViewById(R.id.btnReRegRegister);

		btnReRegRegister.setOnClickListener(new RegisterListener());
	}

	public boolean isValidInputs() {

		txtReRegPassword.setError(null);
		txtReRegPin.setError(null);
		txtReRegRPin.setError(null);

		String password = txtReRegPassword.getText().toString();
		String pin = txtReRegPin.getText().toString();
		String rpin = txtReRegRPin.getText().toString();

		if (TextUtils.isEmpty(password)) {
			txtReRegPassword.setError(getString(R.string.error_field_required));
			txtReRegPassword.requestFocus();
			return false;
		} else if (!Validator.isValidPassword(password)) {
			txtReRegPassword.setError(getString(R.string.error_invalid_password));
			txtReRegPassword.requestFocus();
			return false;
		}

		if (TextUtils.isEmpty(pin)) {
			txtReRegPin.setError(getString(R.string.error_field_required));
			txtReRegPin.requestFocus();
			return false;
		} else if (!TextUtils.isEmpty(pin) && !Validator.isValidPin(pin)) {
			txtReRegPin.setError(getString(R.string.error_invalid_pin));
			txtReRegPin.requestFocus();
			return false;
		}

		if (TextUtils.isEmpty(rpin)) {
			txtReRegRPin.setError(getString(R.string.error_field_required));
			txtReRegRPin.requestFocus();
			return false;
		} else if (!TextUtils.isEmpty(rpin) && !Validator.isValidPin(rpin)) {
			txtReRegRPin.setError(getString(R.string.error_invalid_pin));
			txtReRegRPin.requestFocus();
			return false;
		} else if (!pin.matches(rpin)) {
			txtReRegRPin.setError(getString(R.string.error_pin_must_match));
			txtReRegRPin.requestFocus();
			return false;
		}

		return true;
	}


	class RegisterListener implements TextView.OnEditorActionListener, View.OnClickListener {

		@Override
		public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
			if (id == R.id.btnLogin || id == EditorInfo.IME_NULL) {
				attemptReRegister();
				return true;
			}
			return false;
		}

		@Override
		public void onClick(View view) {
			attemptReRegister();
		}
	}

	public void attemptReRegister() {

		if (isValidInputs()) {

//			final Hashtable registerParams = new Hashtable<>();
//			registerParams.put("username", txtRegUsername.getText().toString());
//			registerParams.put("firstName", txtRegFirstname.getText().toString());
//			registerParams.put("lastName", txtRegLastname.getText().toString());
//			registerParams.put("msisdn", txtRegMsisdn.getText().toString());
//			registerParams.put("email", txtRegEmail.getText().toString());
//			registerParams.put("deviceId", getDeviceIMEI(this));
//			registerParams.put("pin", txtRegPin.getText().toString());
//
//			final HTTPBackgroundTask registerTask = new HTTPBackgroundTask(
//				this, POST, HTTPBackgroundTask.REGISTER_URL, registerParams
//			);
//
//			runOnUiThread(new Runnable() {
//				public void run() {
//
//					BTResponseCode registerResponse;
//					try {
//						registerResponse = registerTask.execute().get();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//						registerResponse = BTResponseCode.GENERAL_ERROR.setMessage("Registration interrupted");
//					} catch (ExecutionException e) {
//						e.printStackTrace();
//						registerResponse = BTResponseCode.GENERAL_ERROR.setMessage("Registration failed: " + e.getMessage());
//					}
//
//					final String responseMsg = registerResponse.getMessage();
//
//					Log.i(TAG, "Registration Response: " + responseMsg);
//
//					if (registerResponse.equals(SUCCESS)) {
//						UserDetails newUserDetails = new UserDetails(
//							txtRegUsername.getText().toString(),
//							txtRegFirstname.getText().toString(),
//							txtRegLastname.getText().toString(),
//							txtRegMsisdn.getText().toString(),
//							txtRegEmail.getText().toString(),
//							txtRegPin.getText().toString()
//						);
//						GPPersistence goPayDB = LoginActivity.getLoginActivity().getGoPayDB();
//						goPayDB.setUserDetails(newUserDetails);
//						finish();
//						Intent bankIntent = new Intent(getLoginActivity(), AddCashoutActivity.class);
//						startActivity(bankIntent);
//						runOnUiThread(new Runnable() {
//							public void run() {
//								makeText(getBankActivity(), "Registration successful.", LENGTH_LONG).show();
//							}
//						});
//					} else {
//						runOnUiThread(new Runnable() {
//							public void run() { makeText(profileActivity, responseMsg, LENGTH_LONG).show(); }
//						});
//					}
//				}
//			});
		}
	}
}
