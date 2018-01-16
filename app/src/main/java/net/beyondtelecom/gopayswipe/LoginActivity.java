package net.beyondtelecom.gopayswipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.beyondtelecom.gopayswipe.common.UserDetails;
import net.beyondtelecom.gopayswipe.common.Validator;
import net.beyondtelecom.gopayswipe.server.HTTPBackgrounTask;

import java.util.Hashtable;

import static android.view.View.VISIBLE;
import static java.lang.String.format;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgrounTask.TASK_TYPE.POST;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends AppCompatActivity {

	protected static LoginActivity loginActivity;
	protected static UserDetails userDetails;
	private EditText txtPin;
	private EditText txtName;
	Button btnLogin;
	Button btnRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginActivity = this;

		txtName = (EditText) findViewById(R.id.txtName);

		txtPin = (EditText) findViewById(R.id.txtPin);
		txtPin.setOnEditorActionListener(new SignInListener());
		txtPin.requestFocus();

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new SignInListener());

		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(new RegisterListener());

		displayUserDetails();
	}

	public void displayUserDetails() {

		UserDetails loginUser = getUserDetails();

		if (loginUser != null) {
			txtName.setText(format("Login as %s %s",
				loginUser.getFirstName(),
				loginUser.getLastName()));
			txtName.setVisibility(VISIBLE);
		}
	}

	public boolean isValidInputs() {

		txtPin.setError(null);
		String pin = txtPin.getText().toString();

		if (!TextUtils.isEmpty(pin) && !Validator.isValidPin(pin)) {
			txtPin.setError(getString(R.string.error_invalid_pin));
			txtPin.requestFocus();
			return false;
		} else if (TextUtils.isEmpty(pin)) {
			txtPin.setError(getString(R.string.error_field_required));
			txtPin.requestFocus();
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
		public void onClick(View view) { attemptLogin(); }
	}

	class RegisterListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Intent registerIntent = new Intent(loginActivity, RegisterActivity.class);
			startActivity(registerIntent);
		}
	}

	public void attemptLogin() {

		txtPin.setError(null);

		Hashtable loginParams = new Hashtable();
		loginParams.put("username", getUserDetails().getEmail());
		loginParams.put("password", getUserDetails().getPin());
		loginParams.put("username", getUserDetails().getEmail());


		if (isValidInputs()) {
			HTTPBackgrounTask loginTask = new HTTPBackgrounTask(POST, HTTPBackgrounTask.SESSION_URL, loginParams);
			loginTask.execute();

			while (loginTask.isRunning()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Intent swipeIntent = new Intent(loginActivity, ChargeActivity.class);
			startActivity(swipeIntent);
		}
	}

	public static Activity getLoginActivity() { return loginActivity; }

	public static UserDetails getUserDetails() {

		if (userDetails == null) {
			userDetails = new UserDetails("Tsungai", "Kaviya",
				"263785107830","tsungai.kaviya@gmail.com", null, null);
		}
		return userDetails;
	}

}

