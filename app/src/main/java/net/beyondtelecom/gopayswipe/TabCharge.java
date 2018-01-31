package net.beyondtelecom.gopayswipe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.square.MagRead;
import com.square.MagReadListener;

import net.beyondtelecom.gopayswipe.common.ActivityCommon;
import net.beyondtelecom.gopayswipe.common.ReferenceGenerator;
import net.beyondtelecom.gopayswipe.dto.CardType;
import net.beyondtelecom.gopayswipe.dto.CurrencyType;
import net.beyondtelecom.gopayswipe.dto.TransactionDetails;

import java.math.BigDecimal;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopayswipe.MainActivity.getMainActivity;
import static net.beyondtelecom.gopayswipe.TabWallet.getCashoutDetails;
import static net.beyondtelecom.gopayswipe.common.Validator.isNullOrEmpty;
import static net.beyondtelecom.gopayswipe.common.Validator.isNumeric;
import static net.beyondtelecom.gopayswipe.dto.CardType.UNKNOWN;

public class TabCharge extends Fragment {

    private MainActivity mainActivity = getMainActivity();
    private Spinner chooseCurrencyType;
    private UpdateBytesHandler updateBytesHandler;
    private EditText edtTransactionAmount;
    private EditText edtTransactionReference;
    private Button startBtn;
    private Button cancelBtn;
    private MagRead read;
    private ProgressBar prbScanning;
    private static TransactionDetails transactionDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_charge,container,false);

        chooseCurrencyType = (Spinner) v.findViewById(R.id.spnChooseCurrencyType);
        startBtn = (Button)v.findViewById(R.id.btnStartSwipe);
        cancelBtn = (Button)v.findViewById(R.id.btnCancelTrans);
        edtTransactionAmount = (EditText)v.findViewById(R.id.edtTransactionAmount);
        edtTransactionReference = (EditText)v.findViewById(R.id.edtTransactionReference);
        prbScanning = (ProgressBar)v.findViewById(R.id.prbScanning);

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
//				updateBitsHandler.sendMessage(msg);

            }
        });
        MicListener ml = new MicListener();
        CancelListener cl = new CancelListener();
        startBtn.setOnClickListener(ml);
        cancelBtn.setOnClickListener(cl);
        updateBytesHandler = new UpdateBytesHandler();
//		updateBitsHandler = new UpdateBitsHandler();

        populateCurrencies();

        return v;
    }

    public static TransactionDetails getTransactionDetails() {
        if (transactionDetails == null) {
            transactionDetails = new TransactionDetails();
        }
        return transactionDetails;
    }

    private void populateCurrencies() {

        ArrayList<CurrencyType> currencyTypes = ActivityCommon.getCurrencies(getMainActivity());
        ArrayAdapter<CharSequence> currencyTypeAdapter = new ArrayAdapter<>(
                getMainActivity(), android.R.layout.simple_spinner_item);

        for (CurrencyType currencyType : currencyTypes) {
            currencyTypeAdapter.add(currencyType.getCurrencyTypeName());
        }

        chooseCurrencyType.setAdapter(currencyTypeAdapter);
        chooseCurrencyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCashoutDetails().setCashoutCurrency((String)chooseCurrencyType.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

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
                edtTransactionAmount.setError(null);
                String transactionAmount = edtTransactionAmount.getText().toString();

                if (!isNumeric(transactionAmount)) {
                    edtTransactionAmount.setError(getString(R.string.error_invalid_amount));
                    edtTransactionAmount.requestFocus();
                    return;
                }

                getTransactionDetails().setTransactionId(ReferenceGenerator.GenTrails());
                getTransactionDetails().setTransactionAmount(new BigDecimal(transactionAmount));
                getTransactionDetails().setTransactionCurrency((String)chooseCurrencyType.getSelectedItem());
                getTransactionDetails().setTransactionReference(edtTransactionReference.getText().toString());

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
        prbScanning.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        edtTransactionAmount.setEnabled(true);
        edtTransactionReference.setEnabled(true);
        read.stop();
    }

    protected void completeReading() {
        stopReading();
        Intent confirmTransIntent = new Intent(mainActivity, PinActivity.class);
        startActivity(confirmTransIntent);
    }

    protected void startReading() {
        startBtn.setEnabled(false);
        prbScanning.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        edtTransactionAmount.setEnabled(false);
        edtTransactionReference.setEnabled(false);
        read.start();
    }

    private class UpdateBytesHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            String bytes = (String)msg.obj;
            String cardNumber = getCardNumber(bytes);
            if (CardType.detect(cardNumber) == UNKNOWN) {
                getTransactionDetails().setCardNumber(cardNumber);
                showSwipeError(cardNumber);
            } else {
                getTransactionDetails().setCardNumber(cardNumber);
                completeReading();
            }
        }

        private String getCardNumber(String bytes) {

            makeText(mainActivity, "Card read error: '" + bytes + "'. Try Again!", LENGTH_SHORT).show();

            if (isNullOrEmpty(bytes) || bytes.length() < 6 || !bytes.contains("=")) {
                return null;
            }

            bytes = bytes.substring(1);						//remove initial ;
            return bytes.substring(0, bytes.indexOf('='));	//remove everything after =
        }
    }

    public void showSwipeError(String cardNumber) {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mainActivity);
        }
        builder.setTitle("Try Again")
                .setMessage("Card number '" + cardNumber + "' may not be valid. Try Again?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startReading();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        completeReading();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

}
