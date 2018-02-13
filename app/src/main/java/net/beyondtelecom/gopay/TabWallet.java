package net.beyondtelecom.gopay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.beyondtelecom.gopay.common.BTResponseObject;

import static java.lang.String.format;
import static net.beyondtelecom.gopay.MainActivity.getMainActivity;
import static net.beyondtelecom.gopay.common.ActivityCommon.getWallet;
import static net.beyondtelecom.gopay.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopay.common.CommonUtilities.formatDoubleToMoney;
import static net.beyondtelecom.gopay.common.Validator.isNullOrEmpty;

public class TabWallet extends Fragment {

    private View tabCashoutOptionsView;
    private TextView txvCurrentBalance;
    private FloatingActionButton btnRefreshBalance;
    private LinearLayout frmWalletHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tabCashoutOptionsView = inflater.inflate(R.layout.tab_wallet,container,false);
        txvCurrentBalance = (TextView) tabCashoutOptionsView.findViewById(R.id.txvCurrentBalance);
        btnRefreshBalance = (FloatingActionButton) tabCashoutOptionsView.findViewById(R.id.btnRefreshBalance);
        frmWalletHistory = (LinearLayout) tabCashoutOptionsView.findViewById(R.id.frmWalletHistory);

        btnRefreshBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateCurrentBalance(true);
            }
        });

        populateCurrentBalance(false);
        return tabCashoutOptionsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateCurrentBalance(false);
    }

    public void populateCurrentBalance(boolean refresh) {

        if (refresh || isNullOrEmpty(txvCurrentBalance.getText().toString())) {
            BTResponseObject<Double> walletResponse = getWallet(getMainActivity());
            if (walletResponse.getResponseCode().equals(SUCCESS)) {
                txvCurrentBalance.setText(format("Current Balance: %s", formatDoubleToMoney(walletResponse.getResponseObject())));
            }
        }
    }

    public void populateWalletHistory() {

    }

}