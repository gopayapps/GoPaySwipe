package net.beyondtelecom.gopay;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import net.beyondtelecom.gopay.common.BTResponseCode;
import net.beyondtelecom.gopay.common.Validator;

import java.util.Hashtable;

import static android.view.View.VISIBLE;
import static net.beyondtelecom.gopay.common.ActivityCommon.getDeviceIMEI;
import static net.beyondtelecom.gopay.common.ActivityCommon.getTag;
import static net.beyondtelecom.gopay.common.ActivityCommon.getUserDetails;
import static net.beyondtelecom.gopay.common.ActivityCommon.loginUser;
import static net.beyondtelecom.gopay.common.BTResponseCode.SUCCESS;

public class LoginActivity extends AppCompatActivity {

	private static final String TAG = getTag(LoginActivity.class);
	protected LoginActivity loginActivity;
	private ProgressBar progressBar;
	private EditText edtUsername;
//	private EditText txtName;
	private EditText edtPin;
	Button btnLogin;
	Button btnRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginActivity = this;

		progressBar = (ProgressBar) findViewById(R.id.prgLogin);

		edtUsername = (EditText) findViewById(R.id.edtUsername);
//		txtName = (EditText) findViewById(R.id.txtName);
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

	public void displayUserDetails() {
		if (getUserDetails(this) != null) {
			edtUsername.setText(getUserDetails(this).getUsername());
			edtPin.requestFocus();
		} else {
			edtUsername.requestFocus();
		}
	}

	public boolean isValidInputs() {

		edtUsername.setError(null);
		edtPin.setError(null);

		if (TextUtils.isEmpty(edtUsername.getText().toString())) {
			edtUsername.setError(getString(R.string.error_field_required));
			edtUsername.requestFocus();
			return false;
		} else if (!Validator.isValidUsername(edtUsername.getText().toString())) {
			edtUsername.setError(getString(R.string.error_invalid_username));
			edtUsername.requestFocus();
			return false;
		}

		if (TextUtils.isEmpty(edtPin.getText().toString())) {
			edtPin.setError(getString(R.string.error_field_required));
			edtPin.requestFocus();
			return false;
		} else if (!Validator.isValidPin(edtPin.getText().toString())) {
			edtPin.setError(getString(R.string.error_invalid_pin));
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
			progressBar.setVisibility(VISIBLE);
			attemptLogin();
			progressBar.setVisibility(View.GONE);

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

		if (isValidInputs()) {

			final Hashtable<String, String> loginParams = new Hashtable<>();
			loginParams.put("username", edtUsername.getText().toString());
			loginParams.put("pin", edtPin.getText().toString());
			loginParams.put("deviceId", getDeviceIMEI(this));

			BTResponseCode loginUserResponse = loginUser(this, loginParams);
			if (loginUserResponse.equals(SUCCESS)) {
				Intent swipeIntent = new Intent(loginActivity, MainActivity.class);
				startActivity(swipeIntent);
			}
		}
	}
}

