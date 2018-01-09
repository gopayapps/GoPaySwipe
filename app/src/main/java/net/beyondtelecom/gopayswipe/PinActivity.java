package net.beyondtelecom.gopayswipe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static java.lang.String.format;
import static net.beyondtelecom.gopayswipe.ChargeActivity.getTransactionDetails;
import static net.beyondtelecom.gopayswipe.common.CommonUtilities.formatDoubleToMoney;
import static net.beyondtelecom.gopayswipe.common.Validator.isNullOrEmpty;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidCardPin;

public class PinActivity extends AppCompatActivity {

	TextView txtCardBytes;
	TextView txtAmountInfo;
	TextView txtTransactionReference;
	Button btnCancelTransaction;
	Button btnCompleteTransaction;
	EditText edtCardPin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin);

		txtCardBytes = (TextView) findViewById(R.id.txtCardBytes);
		txtAmountInfo = (TextView) findViewById(R.id.txtAmountInfo);
		txtTransactionReference = (TextView) findViewById(R.id.txtTransactionReference);
		btnCancelTransaction = (Button) findViewById(R.id.btnCancelTransaction);
		btnCancelTransaction.setOnClickListener(new CancelTransactionListener());
		btnCompleteTransaction = (Button) findViewById(R.id.btnCompleteTransaction);
		btnCompleteTransaction.setOnClickListener(new CompleteTransactionListener());
		edtCardPin = (EditText) findViewById(R.id.edtCardPin);
		populateTransactionDetails();
		edtCardPin.requestFocus();
	}

	private void populateTransactionDetails() {

		txtCardBytes.setText(getTransactionDetails().getCardNumber());

		String amount = getTransactionDetails().getTransactionCurrency() + " " +
			formatDoubleToMoney(getTransactionDetails().getTransactionAmount().doubleValue());
		txtAmountInfo.setText(amount);

		if (!isNullOrEmpty(getTransactionDetails().getTransactionReference())) {
			txtTransactionReference.setVisibility(View.VISIBLE);
			txtTransactionReference.setText(format("Reference: %s",
				getTransactionDetails().getTransactionReference()));
		} else {
			txtTransactionReference.setVisibility(View.GONE);
			txtTransactionReference.setText(R.string.label_no_reference);
		}
	}

	class CancelTransactionListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	class CompleteTransactionListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (!isValidCardPin(edtCardPin.getText().toString())) {
				edtCardPin.setError("This pin is not valid");
			}
			getTransactionDetails().setCardPin(Integer.parseInt(edtCardPin.getText().toString()));
		}
	}

}
