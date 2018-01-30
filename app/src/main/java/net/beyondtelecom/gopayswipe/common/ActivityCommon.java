package net.beyondtelecom.gopayswipe.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.beyondtelecom.gopayswipe.dto.AccountType;
import net.beyondtelecom.gopayswipe.dto.CashoutOption;
import net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.FINANCIAL_INSTITUTIONS_URL;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.TASK_TYPE.GET;

/***************************************************************************
 *                                                                         *
 * Created:     16 / 01 / 2018                                             *
 * Author:      Tsungai Kaviya                                             *
 * Contact:     tsungai.kaviya@gmail.com                                   *
 *                                                                         *
 ***************************************************************************/
public class ActivityCommon {

    private static final String BASE_TAG = "GoPay_";
    private static final String TAG = getTag(ActivityCommon.class);

    private static ArrayList<CashoutOption> cashoutOptions;

    public static String getTag(Class _class) {
        return BASE_TAG + _class.getSimpleName();
    }

    public static String getDeviceIMEI(Activity activity) {
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            String imei = mTelephonyMgr.getDeviceId();
            int retries = 0;
            while (retries++ != 3 && (imei == null || imei.equals(""))) {
                mTelephonyMgr = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
                imei = mTelephonyMgr.getDeviceId();
            }
            return imei;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static String getDeviceIMSI(Activity activity)
    {
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            String imsi = mTelephonyMgr.getSubscriberId();
            int retries = 0;
            while (retries++ != 3 && (imsi == null || imsi.equals(""))) {
                mTelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                imsi = mTelephonyMgr.getDeviceId();
            }
            return imsi;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static ArrayList<CashoutOption> getCashoutOptions(final Activity activity)
    {
        if (cashoutOptions == null) {

            cashoutOptions = new ArrayList<>();

            final HTTPBackgroundTask getBanksTask = new HTTPBackgroundTask(
                    activity, GET, FINANCIAL_INSTITUTIONS_URL, null
            );

            activity.runOnUiThread(new Runnable() {
                public void run() {

                    BTResponseObject registerResponse;
                    try {
                        registerResponse = getBanksTask.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        registerResponse = new BTResponseObject(
                                BTResponseCode.GENERAL_ERROR.setMessage("Get financial institutions interrupted")
                        );
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        registerResponse = new BTResponseObject(
                                BTResponseCode.GENERAL_ERROR.setMessage("Get financial institutions failed: " + e.getMessage())
                        );
                    }

                    final String responseMsg = registerResponse.getMessage();

                    Log.i(TAG, "Get financial institutions response: " + responseMsg);

                    if (registerResponse.getResponseCode().equals(SUCCESS)) {

                        try {

                            JSONObject responseJSON = new JSONObject(registerResponse.getResponseObject().toString());

                            JSONArray symFinancialInstitutionData = responseJSON.getJSONArray("symFinancialInstitutionData");

                            Log.i(TAG, "Got " + symFinancialInstitutionData.length() + " financial institutions");

                            for (int c = 0; c < symFinancialInstitutionData.length(); c++) {
                                JSONObject financialInstitution = symFinancialInstitutionData.getJSONObject(c);
                                Log.i(TAG, "Adding Cashout Option " + financialInstitution.getString("institutionName"));
                                cashoutOptions.add(new CashoutOption(
                                        financialInstitution.getInt("institutionId"),
                                        financialInstitution.getString("institutionShortName"),
                                        AccountType.valueOf(financialInstitution.getString("institutionType"))
                                ));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.i(TAG, "Failed to populate cashout options " + ex.getMessage());
                        }
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                makeText(activity, responseMsg, LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });

            return cashoutOptions;
        }
        return cashoutOptions;
    }

}
