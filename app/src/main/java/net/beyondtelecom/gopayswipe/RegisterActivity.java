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
import net.beyondtelecom.gopayswipe.common.BTResponseObject;
import net.beyondtelecom.gopayswipe.dto.UserDetails;
import net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask;

import org.json.JSONObject;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.lang.String.format;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.clearUserDetailsCache;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getDeviceIMEI;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getGoPayDB;
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidEmail;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidMsisdn;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidName;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidPin;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidPlainText;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidUsername;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.TASK_TYPE.POST;

public class RegisterActivity extends AppCompatActivity {

	protected static final String TAG = ActivityCommon.getTag(RegisterActivity.class);
	protected RegisterActivity registerActivity;
	private ProgressBar progressBar;
	private EditText txtRegUsername;
	private EditText txtRegFirstName;
	private EditText txtRegLastName;
	private EditText txtRegCompanyName;
	private EditText txtRegMsisdn;
	private EditText txtRegEmail;
	private EditText txtRegPin;
	private EditText txtRegRPin;
	private Button btnRegRegister;
	private static UserDetails previousRegDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		registerActivity = this;
		progressBar = (ProgressBar) findViewById(R.id.prgRegister);

		txtRegUsername = (EditText) findViewById(R.id.txtRegUsername);
		txtRegFirstName = (EditText) findViewById(R.id.txtRegFirstName);
		txtRegLastName = (EditText) findViewById(R.id.txtRegLastName);
		txtRegCompanyName = (EditText) findViewById(R.id.txtRegCompanyName);
		txtRegMsisdn = (EditText) findViewById(R.id.txtRegMsisdn);
		txtRegEmail = (EditText) findViewById(R.id.txtRegEmail);
		txtRegPin = (EditText) findViewById(R.id.txtRegPin);
		txtRegRPin = (EditText) findViewById(R.id.txtRegRPin);
		btnRegRegister = (Button) findViewById(R.id.btnRegRegister);

		btnRegRegister.setOnClickListener(new RegisterListener());
	}

	public boolean isValidInputs() {

		txtRegUsername.setError(null);
		txtRegFirstName.setError(null);
		txtRegLastName.setError(null);
		txtRegCompanyName.setError(null);
		txtRegMsisdn.setError(null);
		txtRegEmail.setError(null);
		txtRegPin.setError(null);
		txtRegRPin.setError(null);

		String username = txtRegUsername.getText().toString();
		String fname = txtRegFirstName.getText().toString();
		String lname = txtRegLastName.getText().toString();
		String cname = txtRegCompanyName.getText().toString();
		String msisdn = txtRegMsisdn.getText().toString();
		String email = txtRegEmail.getText().toString();
		String pin = txtRegPin.getText().toString();
		String rpin = txtRegRPin.getText().toString();

		if (!TextUtils.isEmpty(username) && !isValidUsername(username)) {
			txtRegUsername.setError(getString(R.string.error_invalid_username));
			txtRegUsername.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(username)) {
			txtRegUsername.setError(getString(R.string.error_field_required));
			txtRegUsername.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(fname) && !isValidName(fname)) {
			txtRegFirstName.setError(getString(R.string.error_invalid_name));
			txtRegFirstName.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(fname)) {
			txtRegFirstName.setError(getString(R.string.error_field_required));
			txtRegFirstName.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(lname) && !isValidName(lname)) {
			txtRegLastName.setError(getString(R.string.error_invalid_name));
			txtRegLastName.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(lname)) {
			txtRegLastName.setError(getString(R.string.error_field_required));
			txtRegLastName.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(cname) && !isValidPlainText(cname)) {
			txtRegCompanyName.setError(getString(R.string.error_invalid_name));
			txtRegCompanyName.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(msisdn) && !isValidMsisdn(msisdn)) {
			txtRegMsisdn.setError(getString(R.string.error_invalid_msisdn));
			txtRegMsisdn.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(msisdn)) {
			txtRegMsisdn.setError(getString(R.string.error_field_required));
			txtRegMsisdn.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
			txtRegEmail.setError(getString(R.string.error_invalid_email));
			txtRegEmail.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(pin) && !isValidPin(pin)) {
			txtRegPin.setError(getString(R.string.error_invalid_pin));
			txtRegPin.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(pin)) {
			txtRegPin.setError(getString(R.string.error_field_required));
			txtRegPin.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(rpin) && !isValidPin(rpin)) {
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

	public static UserDetails getPreviousRegDetails() {
		return previousRegDetails;
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
			registerParams.put("firstName", txtRegFirstName.getText().toString());
			registerParams.put("lastName", txtRegLastName.getText().toString());
			registerParams.put("companyName", txtRegCompanyName.getText().toString());
			registerParams.put("msisdn", txtRegMsisdn.getText().toString());
			registerParams.put("email", txtRegEmail.getText().toString());
			registerParams.put("deviceId", getDeviceIMEI(this));
			registerParams.put("pin", txtRegPin.getText().toString());

			final HTTPBackgroundTask registerTask = new HTTPBackgroundTask(
				this, POST, HTTPBackgroundTask.USER_URL, registerParams
			);

			runOnUiThread(new Runnable() {
				public void run() {

					BTResponseObject registerResponse;
					try {
						registerResponse = registerTask.execute().get();
					} catch (InterruptedException e) {
						e.printStackTrace();
						registerResponse = new BTResponseObject(
							BTResponseCode.GENERAL_ERROR.setMessage("Registration interrupted")
						);
					} catch (ExecutionException e) {
						e.printStackTrace();
						registerResponse = new BTResponseObject(
							BTResponseCode.GENERAL_ERROR.setMessage("Registration failed: " + e.getMessage())
						);
					}

					final String responseMsg = registerResponse.getMessage();

					Log.i(TAG, "Registration Response: " + responseMsg);

					if (registerResponse.getResponseCode().equals(SUCCESS)) {

						try {

							JSONObject responseJSON = new JSONObject(registerResponse.getResponseObject().toString());

							JSONObject systemUserData = responseJSON.getJSONArray("systemUserData").getJSONObject(0);

							UserDetails newUserDetails = new UserDetails(
								systemUserData.getLong("userId"),
								systemUserData.optString("username", null),
								systemUserData.optString("firstName", null),
								systemUserData.optString("lastName", null),
								txtRegCompanyName.getText().toString(),
								systemUserData.optString("msisdn", null),
								systemUserData.optString("email", null),
								txtRegPin.getText().toString()
							);

							clearUserDetailsCache();
							getGoPayDB(registerActivity).setUserDetails(newUserDetails);
							finish();
							Intent mainIntent = new Intent(registerActivity, MainActivity.class);
							startActivity(mainIntent);
							runOnUiThread(new Runnable() {
								public void run() {
								makeText(registerActivity, "Registration successful.", LENGTH_LONG).show();
								}
							});

							Log.i(TAG, format("Saved user %s %s (%s) with id %s",
								newUserDetails.getFirstName(), newUserDetails.getLastName(),
								newUserDetails.getUsername(), newUserDetails.getBtUserId()
							));
						} catch (final Exception ex) {
							ex.printStackTrace();
							Log.e(TAG, "Failed to register user: " + ex.getMessage());
							registerActivity.runOnUiThread(new Runnable() {
								public void run() {
								makeText(registerActivity, "Failed to register user: " + ex.getMessage(), LENGTH_LONG).show();
								}
							});
						}
					}
//					else if (registerResponse.getResponseCode().equals(PREVIOUS_EMAIL_FOUND) ||
//							   registerResponse.getResponseCode().equals(PREVIOUS_MSISDN_FOUND)) {
//
//						runOnUiThread(new Runnable() {
//							public void run() { makeText(registerActivity, responseMsg, LENGTH_LONG).show(); }
//						});
//
//						UserDetails previousDetails = getPreviousAccountDetails();
//
//						Intent reRegisterIntent = new Intent(registerActivity, ReRegisterActivity.class);
//						startActivity(reRegisterIntent);
//
//					}
					else {
						runOnUiThread(new Runnable() {
							public void run() { makeText(registerActivity, responseMsg, LENGTH_LONG).show(); }
						});
					}
				}
			});
		}
	}

//	private UserDetails getPreviousAccountDetails() {
//
//		previousRegDetails = null;
//
//		final Hashtable searchParams = new Hashtable<String, String>();
//
//		searchParams.put("msisdn", txtRegMsisdn.getText().toString());
//
//		if (!isNullOrEmpty(txtRegEmail.getText().toString())) {
//			searchParams.put("email", txtRegEmail.getText().toString());
//		}
//
//		final HTTPBackgroundTask searchTask = new HTTPBackgroundTask(
//			this, GET, HTTPBackgroundTask.USER_URL, searchParams
//		);
//
//		runOnUiThread(new Runnable() {
//			public void run() {
//
//				BTResponseObject searchResponse;
//				try {
//					searchResponse = searchTask.execute().get();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//					searchResponse = new BTResponseObject(
//						BTResponseCode.GENERAL_ERROR.setMessage("Registration interrupted")
//					);
//				} catch (ExecutionException e) {
//					e.printStackTrace();
//					searchResponse = new BTResponseObject(
//						BTResponseCode.GENERAL_ERROR.setMessage("Registration failed: " + e.getMessage())
//					);
//				}
//
//				final String responseMsg = searchResponse.getMessage();
//
//				Log.i(TAG, "Registration Response: " + responseMsg);
//
//				if (searchResponse.equals(SUCCESS)) {
//
//					try {
//						JSONObject responseJSON = new JSONObject((String) searchResponse.getResponseObject());
//
//						Log.i(TAG, (String) searchResponse.getResponseObject());
//
//						previousRegDetails = new UserDetails();
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//
//				}
//			}
//		});
//		return previousRegDetails;
//	}
}
