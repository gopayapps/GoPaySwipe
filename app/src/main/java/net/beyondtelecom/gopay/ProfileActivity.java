package net.beyondtelecom.gopay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.beyondtelecom.gopay.common.ActivityCommon;
import net.beyondtelecom.gopay.common.BTResponseCode;
import net.beyondtelecom.gopay.dto.UserDetails;

import java.util.Hashtable;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopay.common.ActivityCommon.getDeviceIMEI;
import static net.beyondtelecom.gopay.common.ActivityCommon.getUserDetails;
import static net.beyondtelecom.gopay.common.ActivityCommon.updateProfile;
import static net.beyondtelecom.gopay.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopay.common.Validator.isValidEmail;
import static net.beyondtelecom.gopay.common.Validator.isValidMsisdn;
import static net.beyondtelecom.gopay.common.Validator.isValidName;
import static net.beyondtelecom.gopay.common.Validator.isValidPin;
import static net.beyondtelecom.gopay.common.Validator.isValidPlainText;
import static net.beyondtelecom.gopay.common.Validator.isValidUsername;

public class ProfileActivity extends AppCompatActivity {

	protected static final String TAG = ActivityCommon.getTag(ProfileActivity.class);
	protected ProfileActivity profileActivity;
	private ProgressBar progressBar;
	private EditText txtProfileUsername;
	private EditText txtProfileFirstName;
	private EditText txtProfileLastName;
	private EditText txtProfileCompanyName;
	private EditText txtProfileMsisdn;
	private EditText txtProfileEmail;
	private EditText txtProfilePin;
	private EditText txtProfileRPin;
	private Button btnProfileUpdate;
	private static UserDetails currentUserDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		profileActivity = this;
		progressBar = (ProgressBar) findViewById(R.id.prgProfile);

		txtProfileUsername = (EditText) findViewById(R.id.txtProfileUsername);
		txtProfileFirstName = (EditText) findViewById(R.id.txtProfileFirstName);
		txtProfileLastName = (EditText) findViewById(R.id.txtProfileLastName);
		txtProfileCompanyName = (EditText) findViewById(R.id.txtProfileCompanyName);
		txtProfileMsisdn = (EditText) findViewById(R.id.txtProfileMsisdn);
		txtProfileEmail = (EditText) findViewById(R.id.txtProfileEmail);
		txtProfilePin = (EditText) findViewById(R.id.txtProfilePin);
		txtProfileRPin = (EditText) findViewById(R.id.txtProfileRPin);
		btnProfileUpdate = (Button) findViewById(R.id.btnProfileUpdate);

		currentUserDetails = getUserDetails(this);

		txtProfileUsername.setText(currentUserDetails.getUsername());
		txtProfileFirstName.setText(currentUserDetails.getFirstName());
		txtProfileLastName.setText(currentUserDetails.getLastName());
		txtProfileCompanyName.setText(currentUserDetails.getCompanyName());
		txtProfileMsisdn.setText(currentUserDetails.getMsisdn());
		txtProfileEmail.setText(currentUserDetails.getEmail());

		btnProfileUpdate.setOnClickListener(new ProfileUpdateListener());
	}

	public boolean isValidInputs() {

		txtProfileUsername.setError(null);
		txtProfileFirstName.setError(null);
		txtProfileLastName.setError(null);
		txtProfileCompanyName.setError(null);
		txtProfileMsisdn.setError(null);
		txtProfileEmail.setError(null);
		txtProfilePin.setError(null);
		txtProfileRPin.setError(null);

		String username = txtProfileUsername.getText().toString();
		String fname = txtProfileFirstName.getText().toString();
		String lname = txtProfileLastName.getText().toString();
		String cname = txtProfileCompanyName.getText().toString();
		String msisdn = txtProfileMsisdn.getText().toString();
		String email = txtProfileEmail.getText().toString();
		String pin = txtProfilePin.getText().toString();
		String rpin = txtProfileRPin.getText().toString();

		if (!TextUtils.isEmpty(username) && !isValidUsername(username)) {
			txtProfileUsername.setError(getString(R.string.error_invalid_username));
			txtProfileUsername.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(username)) {
			txtProfileUsername.setError(getString(R.string.error_field_required));
			txtProfileUsername.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(fname) && !isValidName(fname)) {
			txtProfileFirstName.setError(getString(R.string.error_invalid_name));
			txtProfileFirstName.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(fname)) {
			txtProfileFirstName.setError(getString(R.string.error_field_required));
			txtProfileFirstName.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(lname) && !isValidName(lname)) {
			txtProfileLastName.setError(getString(R.string.error_invalid_name));
			txtProfileLastName.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(lname)) {
			txtProfileLastName.setError(getString(R.string.error_field_required));
			txtProfileLastName.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(cname) && !isValidPlainText(cname)) {
			txtProfileCompanyName.setError(getString(R.string.error_invalid_name));
			txtProfileCompanyName.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(msisdn) && !isValidMsisdn(msisdn)) {
			txtProfileMsisdn.setError(getString(R.string.error_invalid_msisdn));
			txtProfileMsisdn.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(msisdn)) {
			txtProfileMsisdn.setError(getString(R.string.error_field_required));
			txtProfileMsisdn.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
			txtProfileEmail.setError(getString(R.string.error_invalid_email));
			txtProfileEmail.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(pin) && !isValidPin(pin)) {
			txtProfilePin.setError(getString(R.string.error_invalid_pin));
			txtProfilePin.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(rpin) && !isValidPin(rpin)) {
			txtProfileRPin.setError(getString(R.string.error_invalid_pin));
			txtProfileRPin.requestFocus();
			return false;
		} else if (!TextUtils.isEmpty(pin) && TextUtils.isEmpty(rpin)) {
			txtProfileRPin.setError(getString(R.string.error_field_required));
			txtProfileRPin.requestFocus();
			return false;
		} else if (!pin.matches(rpin)) {
			txtProfileRPin.setError(getString(R.string.error_pin_must_match));
			txtProfileRPin.requestFocus();
			return false;
		}

		return true;
	}

	class ProfileUpdateListener implements TextView.OnEditorActionListener, View.OnClickListener {

		@Override
		public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
			if (id == R.id.btnLogin || id == EditorInfo.IME_NULL) {
				attemptUpdate();
				return true;
			}
			return false;
		}

		@Override
		public void onClick(View view) {
			attemptUpdate();
		}
	}

	public void attemptUpdate() {

		if (isValidInputs()) {
			final Hashtable<String, String> updateDetails = new Hashtable<>();
			updateDetails.put("username", txtProfileUsername.getText().toString());
			updateDetails.put("firstName", txtProfileFirstName.getText().toString());
			updateDetails.put("lastName", txtProfileLastName.getText().toString());
			updateDetails.put("companyName", txtProfileCompanyName.getText().toString());
			updateDetails.put("msisdn", txtProfileMsisdn.getText().toString());
			updateDetails.put("email", txtProfileEmail.getText().toString());
			updateDetails.put("deviceId", getDeviceIMEI(this));
			updateDetails.put("pin", txtProfilePin.getText().toString());

			BTResponseCode updateResponse = updateProfile(this, updateDetails);

			if (updateResponse.equals(SUCCESS)) {
				Intent mainIntent = new Intent(profileActivity, MainActivity.class);
				startActivity(mainIntent);
				runOnUiThread(new Runnable() {
					public void run() {
					makeText(profileActivity, "User profile updated successfully.", LENGTH_LONG).show();
					}
				});
			}
		}
	}
}
