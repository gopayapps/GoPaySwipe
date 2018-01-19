package net.beyondtelecom.gopayswipe;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.beyondtelecom.gopayswipe.dto.CurrencyType;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static net.beyondtelecom.gopayswipe.LoginActivity.getGoPayDB;
import static net.beyondtelecom.gopayswipe.TabCashoutOptions.getCashoutDetails;
import static net.beyondtelecom.gopayswipe.TabCashoutOptions.getCashoutWalletAcount;
import static net.beyondtelecom.gopayswipe.common.Validator.isNullOrEmpty;
import static net.beyondtelecom.gopayswipe.common.Validator.isValidCardPin;

public class CashoutActivity extends AppCompatActivity {

    TextView txvCashoutAccountType;
    TextView txvCashoutAccountDetails;
    Spinner spnChooseCurrencyType;
    EditText edtCashoutAmount;
    EditText edtCashoutReference;
    EditText edtCashoutPin;
    Button btnCashout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashout);

        txvCashoutAccountType = (TextView) findViewById(R.id.txvCashoutAccountType);
        txvCashoutAccountDetails = (TextView) findViewById(R.id.txvCashoutAccountDetails);
        spnChooseCurrencyType = (Spinner) findViewById(R.id.spnChooseCurrencyType);
        edtCashoutAmount = (EditText) findViewById(R.id.edtCashoutAmount);
        edtCashoutReference = (EditText) findViewById(R.id.edtCashoutReference);
        edtCashoutPin = (EditText) findViewById(R.id.edtCashoutPin);
        btnCashout = (Button) findViewById(R.id.btnCashout);

        btnCashout.setOnClickListener(new CompleteCashoutListener());

        populateCurrencies();
        populateCashoutDetails();
    }

    private void populateCurrencies() {

        ArrayList<CurrencyType> currencyTypes = getGoPayDB().getCurrencyTypes();
        ArrayAdapter<CharSequence> currencyTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item);

        for (CurrencyType currencyType : currencyTypes) {
            currencyTypeAdapter.add(currencyType.getCurrencyTypeName());
        }

        spnChooseCurrencyType.setAdapter(currencyTypeAdapter);
        spnChooseCurrencyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCashoutDetails().setCashoutCurrency((String)spnChooseCurrencyType.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void populateCashoutDetails() {

        txvCashoutAccountType.setText(format("Cashout to %s : %s : %s",
            getCashoutWalletAcount().getWalletNickname(),
            getCashoutWalletAcount().getCashoutOption().getCashoutOptionName(),
            getCashoutWalletAcount().getWalletAccountNumber())
        );

        StringBuilder accountDetails = new StringBuilder();

        if (!isNullOrEmpty(getCashoutWalletAcount().getWalletName())) {
            accountDetails.append(format("Account Name: %s\n", getCashoutWalletAcount().getWalletName()));
        }
        if (!isNullOrEmpty(getCashoutWalletAcount().getWalletAccountBranch())) {
            accountDetails.append(format("Account Branch: %s\n", getCashoutWalletAcount().getWalletAccountBranch()));
        }
        if (!isNullOrEmpty(getCashoutWalletAcount().getWalletEmail())) {
            accountDetails.append(format("Account Email: %s\n", getCashoutWalletAcount().getWalletEmail()));
        }
        if (!isNullOrEmpty(getCashoutWalletAcount().getWalletPhone())) {
            accountDetails.append(format("Account Phone: %s\n", getCashoutWalletAcount().getWalletPhone()));
        }

        txvCashoutAccountDetails.setText(accountDetails.toString());
    }

    class CompleteCashoutListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!isValidCardPin(edtCashoutPin.getText().toString())) {
                edtCashoutPin.setError("This pin is not valid");
                return;
            }
            getCashoutDetails().setCashoutPin(parseInt(edtCashoutPin.getText().toString()));
            showCashoutCompletion();
        }
    }

    public void showCashoutCompletion() {
        new AlertDialog.Builder(this)
            .setTitle("Cashout Complete")
            .setMessage("Cashout successfully submitted for processing.")
            .setCancelable(false)
            .setIcon(R.drawable.success)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            }).show();
    }



}