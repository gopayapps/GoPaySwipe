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
import net.beyondtelecom.gopayswipe.common.BTResponseObject;
import net.beyondtelecom.gopayswipe.common.Validator;
import net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getDeviceIMEI;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getUserDetails;
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.SESSION_URL;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.TASK_TYPE.POST;

public class LoginActivity extends AppCompatActivity {

	private static final String TAG = ActivityCommon.getTag(LoginActivity.class);
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

		if (isValidInputs()) {

			final Hashtable<String, String> loginParams = new Hashtable<>();
			loginParams.put("username", edtUsername.getText().toString());
			loginParams.put("pin", edtPin.getText().toString());
			loginParams.put("deviceId", getDeviceIMEI(this));

			final HTTPBackgroundTask loginTask = new HTTPBackgroundTask(
				this, POST, SESSION_URL, loginParams
			);

            runOnUiThread(new Runnable() {
                public void run() {
					BTResponseObject loginResponse;
					try {
						loginResponse = loginTask.execute().get();
					} catch (InterruptedException e) {
						e.printStackTrace();
						loginResponse = new BTResponseObject(
								BTResponseCode.GENERAL_ERROR.setMessage("Login interrupted!")
						);
					} catch (ExecutionException e) {
						e.printStackTrace();
						loginResponse = new BTResponseObject(
							BTResponseCode.GENERAL_ERROR.setMessage("Login failed: " + e.getMessage())
						);
					}

					final String responseMsg = loginResponse.getMessage();
					runOnUiThread(new Runnable() {
						public void run() {
							makeText(loginActivity, responseMsg, LENGTH_LONG).show();
						}
					});

					Log.i(TAG, "Login Response: " + loginTask.getBtResponseCode().getMessage());

					if (loginResponse.getResponseCode().equals(SUCCESS)) {
						Intent swipeIntent = new Intent(loginActivity, MainActivity.class);
						startActivity(swipeIntent);
					}
				}
            });
		}
	}
}

