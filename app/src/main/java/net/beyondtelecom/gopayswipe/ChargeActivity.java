package net.beyondtelecom.gopayswipe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.square.MagRead;
import com.square.MagReadListener;

public class ChargeActivity extends AppCompatActivity {
	private Spinner chooseCurrencyType;
	private UpdateBytesHandler updateBytesHandler;
	private UpdateBitsHandler updateBitsHandler;
	private TextView decodedStringView;
	private TextView strippedBinaryView;
	private Button startBtn;
	private Button cancelBtn;
	private MagRead read;
	private ProgressBar prbScanning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge);

		chooseCurrencyType = (Spinner) findViewById(R.id.spnChooseCurrencyType);
		startBtn = (Button)findViewById(R.id.btnStartSwipe);
		cancelBtn = (Button)findViewById(R.id.btnCancelTrans);
		decodedStringView = (TextView)findViewById(R.id.edtCardBytes);
		strippedBinaryView = (TextView)findViewById(R.id.edtCardBits);
		prbScanning = (ProgressBar)findViewById(R.id.prbScanning);

		read = new MagRead();
		read.addListener(new MagReadListener() {

			@Override
			public void updateBytes(String bytes) {
				Message msg = new Message();
				msg.obj = bytes;
				updateBytesHandler.sendMessage(msg);
			}

			@Override
			public void updateBits(String bits) {
				Message msg = new Message();
				msg.obj = bits;
				updateBitsHandler.sendMessage(msg);

			}
		});
		MicListener ml = new MicListener();
		CancelListener cl = new CancelListener();
		startBtn.setOnClickListener(ml);
		cancelBtn.setOnClickListener(cl);
		updateBytesHandler = new UpdateBytesHandler();
		updateBitsHandler = new UpdateBitsHandler();

		populateCurrencies();
	}

	private void populateCurrencies() {
		ArrayAdapter<CharSequence> currencyTypeAdapter = ArrayAdapter.createFromResource(this,
				R.array.currency_type, android.R.layout.simple_spinner_item);
		currencyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		chooseCurrencyType.setAdapter(currencyTypeAdapter);
		chooseCurrencyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedCurrency = (String) chooseCurrencyType.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		read.release();
	}



	/**
	 * Listener called with the mic status button is clicked, and when the zero level or noise thresholds are changed
	 */
	private class MicListener implements View.OnClickListener {

		/**
		 * Called when the mic button is clicked
		 * @param
		 */
		@Override
		public void onClick(View v) {
			if(!startBtn.isEnabled()){//stop listening
				stopReading();
			} else if(startBtn.isEnabled()) {//start listening
				startReading();
			}
		}

	}


	/**
	 * Listener called with the mic status button is clicked, and when the zero level or noise thresholds are changed
	 */
	private class CancelListener implements View.OnClickListener {
		@Override
		public void onClick(View v) { stopReading();}
	}

	protected void stopReading() {
		startBtn.setEnabled(true);
		read.stop();
		prbScanning.setVisibility(View.GONE);
		cancelBtn.setVisibility(View.GONE);
	}

	protected void startReading() {
		startBtn.setEnabled(false);
		read.start();
		prbScanning.setVisibility(View.VISIBLE);
		cancelBtn.setVisibility(View.VISIBLE);
	}

	private class UpdateBytesHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			String bytes = (String)msg.obj;
			decodedStringView.setText(bytes);
//			stopReading();
		}

	}

	private class UpdateBitsHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			String bits = (String)msg.obj;
			strippedBinaryView.setText(bits);
//			stopReading();
		}
	}
}
