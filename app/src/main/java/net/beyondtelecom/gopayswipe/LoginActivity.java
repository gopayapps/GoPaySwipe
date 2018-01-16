package net.beyondtelecom.gopayswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.lang.String.format;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getDeviceIMEI;
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.SESSION_URL;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.TASK_TYPE.POST;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends AppCompatActivity {

	private static final String TAG = ActivityCommon.getTag(LoginActivity.class);
	protected static LoginActivity loginActivity;
	protected static UserDetails userDetails;
	protected GPPersistence goPayDB = null;
	private ProgressBar progressBar;
	private EditText edtUsername;
	private EditText txtName;
	private EditText edtPin;
	Button btnLogin;
	Button btnRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginActivity = this;
		goPayDB = new GPPersistence(getApplicationContext());

		progressBar = (ProgressBar) findViewById(R.id.prgLogin);

		edtUsername = (EditText) findViewById(R.id.edtUsername);
		txtName = (EditText) findViewById(R.id.txtName);
		edtPin = (EditText) findViewById(R.id.edtPin);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegister = (Button) findViewById(R.id.btnRegister);

		edtUsername.setOnEditorActionListener(new SignInListener());
		edtPin.setOnEditorActionListener(new SignInListener());

		btnLogin.setOnClickListener(new SignInListener());
		btnRegister.setOnClickListener(new RegisterListener());

		displayUserDetails();
	}

	@Override
	protected void onResume() {
		super.onResume();
		displayUserDetails();
	}

	public GPPersistence getGoPayDB() { return goPayDB; }

	public void displayUserDetails() {
		if (getUserDetails() != null) {
			txtName.setText(format("Login as %s %s : %s",
				getUserDetails().getFirstName(),
				getUserDetails().getLastName(),
				getUserDetails().getUsername()));
			txtName.setVisibility(VISIBLE);
			edtUsername.setVisibility(GONE);
			edtPin.requestFocus();
		} else {
			txtName.setVisibility(GONE);
			edtUsername.setVisibility(VISIBLE);
			edtUsername.requestFocus();
		}
	}

	public boolean isValidInputs(String username, String pin) {

		edtUsername.setError(null);
		edtPin.setError(null);

		if (!Validator.isValidUsername(username)) {
			edtUsername.setError(getString(R.string.error_invalid_username));
			edtUsername.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(username)) {
			edtUsername.setError(getString(R.string.error_field_required));
			edtUsername.requestFocus();
			return false;
		}

		if (!TextUtils.isEmpty(pin) && !Validator.isValidPin(pin)) {
			edtPin.setError(getString(R.string.error_invalid_pin));
			edtPin.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(pin)) {
			edtPin.setError(getString(R.string.error_field_required));
			edtPin.requestFocus();
			return false;
		}

		return true;
	}

	class SignInListener implements TextView.OnEditorActionListener, OnClickListener {
		@Override
		public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
			if (id == R.id.btnLogin || id == EditorInfo.IME_NULL) {
				attemptLogin();
				return true;
			}
			return false;
		}

		@Override
		public void onClick(View view) {
			attemptLogin();
		}
	}

	class RegisterListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Intent registerIntent = new Intent(loginActivity, RegisterActivity.class);
			startActivity(registerIntent);
		}
	}

	public void attemptLogin() {

		String username;
		if (getUserDetails() != null) {
			username = getUserDetails().getUsername();
		} else {
			username = edtUsername.getText().toString();
		}

		if (isValidInputs(username, edtPin.getText().toString())) {

			final Hashtable loginParams = new Hashtable();
			loginParams.put("username", username);
			loginParams.put("pin", edtPin.getText().toString());
			loginParams.put("deviceId", getDeviceIMEI(this));

			final HTTPBackgroundTask loginTask = new HTTPBackgroundTask(
				this, POST, SESSION_URL, loginParams
			);

            runOnUiThread(new Runnable() {
                public void run() {
					BTResponseCode registerResponse;
					try {
						registerResponse = loginTask.execute().get();
					} catch (InterruptedException e) {
						e.printStackTrace();
						registerResponse = BTResponseCode.GENERAL_ERROR.setMessage("Login interrupted!");
					} catch (ExecutionException e) {
						e.printStackTrace();
						registerResponse = BTResponseCode.GENERAL_ERROR.setMessage("Login failed: " + e.getMessage());
					}

					final String responseMsg = registerResponse.getMessage();
					runOnUiThread(new Runnable() {
						public void run() {
							makeText(loginActivity, responseMsg, LENGTH_LONG).show();
						}
					});

					Log.i(TAG, "Login Response: " + loginTask.getBtResponseCode().getMessage());

					if (loginTask.getBtResponseCode().equals(SUCCESS)) {
						Intent swipeIntent = new Intent(loginActivity, ChargeActivity.class);
						startActivity(swipeIntent);
					}
				}
            });
		}
	}

	public static LoginActivity getLoginActivity() {
		return loginActivity;
	}

	public UserDetails getUserDetails() {
		if (userDetails == null && getGoPayDB() != null) {
			Log.i(TAG, "Checking for existing registered user");
			userDetails = getGoPayDB().getUserDetails();
			if (userDetails != null) {
				Log.i(TAG, "Found existing registered user " + userDetails.getUsername());
			}
		}
		return userDetails;
	}
}

