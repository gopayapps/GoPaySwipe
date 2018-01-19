package net.beyondtelecom.gopayswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.beyondtelecom.gopayswipe.dto.CashoutDetails;
import net.beyondtelecom.gopayswipe.dto.WalletAccount;

import java.util.ArrayList;

import static android.view.View.inflate;
import static java.lang.String.format;
import static net.beyondtelecom.gopayswipe.LoginActivity.getGoPayDB;
import static net.beyondtelecom.gopayswipe.MainActivity.getMainActivity;

/**
 * Created by Edwin on 15/02/2015.
 */
public class TabCashoutOptions extends Fragment {

    private View tabCashoutOptionsView;
    private LinearLayout frmCashoutOptions;
    private FloatingActionButton btnAddCashoutOption;
    private static WalletAccount cashoutWalletAcount;
    private static CashoutDetails cashoutDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tabCashoutOptionsView = inflater.inflate(R.layout.tab_cashout_options,container,false);
        btnAddCashoutOption = (FloatingActionButton) tabCashoutOptionsView.findViewById(R.id.btnAddCashoutOption);
        frmCashoutOptions = (LinearLayout) tabCashoutOptionsView.findViewById(R.id.frmCashoutOptions);

        btnAddCashoutOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bankIntent = new Intent(getMainActivity(), AddCashoutActivity.class);
                startActivity(bankIntent);
            }
        });

        populateCashoutOptions();
        return tabCashoutOptionsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateCashoutOptions();
    }

    public static CashoutDetails getCashoutDetails() {
        if (cashoutDetails == null) {
            cashoutDetails = new CashoutDetails();
        }
        return cashoutDetails;
    }

    public static WalletAccount getCashoutWalletAcount() {
        return cashoutWalletAcount;
    }

    public void populateCashoutOptions() {

        frmCashoutOptions.removeAllViews();
        ArrayList<WalletAccount> cashoutDetails = getGoPayDB().getWalletAccounts();
        if (cashoutDetails == null) { return; }
        for (final WalletAccount cashoutDetail : cashoutDetails) {
            View detailsView = inflate(getMainActivity(), R.layout.layout_cashout_option, null);
            TextView txvCashoutType = (TextView)detailsView.findViewById(R.id.txvCashoutType);
            TextView txvNickname = (TextView)detailsView.findViewById(R.id.txvNickname);
            TextView txvAccountNumber = (TextView)detailsView.findViewById(R.id.txvAccountNumber);
            FloatingActionButton btnDeleteCashout = (FloatingActionButton)detailsView.findViewById(R.id.btnDeleteCashout);
            FloatingActionButton btnStartCashout = (FloatingActionButton)detailsView.findViewById(R.id.btnStartCashout);
            btnDeleteCashout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getGoPayDB().deleteWalletAccount(cashoutDetail.getWalletAccountId());
                    populateCashoutOptions();
                }
            });
            btnStartCashout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cashoutWalletAcount = cashoutDetail;
                    Intent intent = new Intent(getMainActivity(), CashoutActivity.class);
                    startActivity(intent);
                }
            });
            txvCashoutType.setText(format("%d. %s", cashoutDetail.getWalletAccountId(), cashoutDetail.getCashoutOption().getCashoutOptionName()));
            txvNickname.setText(format("Nickname: %s", cashoutDetail.getWalletNickname()));
            txvAccountNumber.setText(format("Account Number: %s", cashoutDetail.getWalletAccountNumber()));
            frmCashoutOptions.addView(detailsView);
        }
    }

}