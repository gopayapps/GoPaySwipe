package net.beyondtelecom.gopayswipe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.beyondtelecom.gopayswipe.dto.CashoutAccount;
import net.beyondtelecom.gopayswipe.dto.CashoutDetails;

import java.util.ArrayList;

import static android.view.View.inflate;
import static java.lang.String.format;
import static net.beyondtelecom.gopayswipe.MainActivity.getMainActivity;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.getCashoutAccounts;
import static net.beyondtelecom.gopayswipe.common.ActivityCommon.removeCashoutAccount;
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.SUCCESS;

public class TabWallet extends Fragment {

    private View tabCashoutOptionsView;
    private LinearLayout frmCashoutOptions;
    private FloatingActionButton btnAddCashoutOption;
    private static CashoutAccount cashoutAccount;
    private static CashoutDetails cashoutDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tabCashoutOptionsView = inflater.inflate(R.layout.tab_wallet,container,false);
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

    public static CashoutAccount getCashoutAccount() {
        return cashoutAccount;
    }

    public void populateCashoutOptions() {

        frmCashoutOptions.removeAllViews();
        ArrayList<CashoutAccount> cashoutAccounts = getCashoutAccounts(getMainActivity());
        if (cashoutAccounts == null) { return; }
        for (final CashoutAccount cashoutAccount : cashoutAccounts) {
            View detailsView = inflate(getMainActivity(), R.layout.layout_cashout_option, null);
            TextView txvCashoutType = (TextView)detailsView.findViewById(R.id.txvCashoutType);
            TextView txvNickname = (TextView)detailsView.findViewById(R.id.txvNickname);
            TextView txvAccountNumber = (TextView)detailsView.findViewById(R.id.txvAccountNumber);
            FloatingActionButton btnDeleteCashout = (FloatingActionButton)detailsView.findViewById(R.id.btnDeleteCashout);
            FloatingActionButton btnStartCashout = (FloatingActionButton)detailsView.findViewById(R.id.btnStartCashout);
            btnDeleteCashout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(getMainActivity()).setTitle("Exit Application?")
                        .setMessage("Are you sure you want to remove this cashout option?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (removeCashoutAccount(getMainActivity(), cashoutAccount).equals(SUCCESS)) {
                                        populateCashoutOptions();
                                    }
                                }
                            })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                        }).create().show();
                }
            });
            btnStartCashout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TabWallet.cashoutAccount = cashoutAccount;
                    Intent intent = new Intent(getMainActivity(), CashoutActivity.class);
                    startActivity(intent);
                }
            });
            txvCashoutType.setText(format("%s. %s", cashoutAccount.getCashoutAccountId(), cashoutAccount.getFinancialInstitution().getInstitutionName()));
            txvNickname.setText(format("Nickname: %s", cashoutAccount.getAccountNickName()));
            txvAccountNumber.setText(format("Account Number: %s", cashoutAccount.getAccountNumber()));
            frmCashoutOptions.addView(detailsView);
        }
    }

}