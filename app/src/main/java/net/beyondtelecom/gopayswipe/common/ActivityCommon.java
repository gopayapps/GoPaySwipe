package net.beyondtelecom.gopayswipe.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.beyondtelecom.gopayswipe.dto.CashoutAccount;
import net.beyondtelecom.gopayswipe.dto.CurrencyType;
import net.beyondtelecom.gopayswipe.dto.FinancialInstitution;
import net.beyondtelecom.gopayswipe.dto.InstitutionType;
import net.beyondtelecom.gopayswipe.dto.UserDetails;
import net.beyondtelecom.gopayswipe.persistence.GPPersistence;
import net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopayswipe.common.Validator.isNullOrEmpty;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.CURRENCY_URL;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.FINANCIAL_INSTITUTIONS_URL;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.TASK_TYPE.DELETE;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.TASK_TYPE.GET;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.TASK_TYPE.POST;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.USER_URL;

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

    private static UserDetails userDetails;
    private static Hashtable<Integer, FinancialInstitution> financialInstitutions;
    private static ArrayList<CurrencyType> currencyTypes;
    private static ArrayList<CashoutAccount> cashoutAccounts;
    private static GPPersistence goPayDB = null;

    public static GPPersistence getGoPayDB(final Activity activity) {
        if (goPayDB == null) {
            goPayDB = new GPPersistence(activity.getApplicationContext());
        }
        return goPayDB;
    }

    public static String getTag(Class _class) {
        return BASE_TAG + _class.getSimpleName();
    }

    public static String getDeviceIMEI(final Activity activity) {
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

    public static String getDeviceIMSI(final Activity activity)
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

    public static UserDetails getUserDetails(final Activity activity) {
		if (userDetails == null && getGoPayDB(activity) != null) {
            Log.i(TAG, "Checking for existing registered user");
            userDetails = getGoPayDB(activity).getUserDetails();
            if (userDetails != null) {
                Log.i(TAG, "Found existing registered user " + userDetails.getUsername());
            }
		}
        return userDetails;
    }

    public static void clearUserDetailsCache() { userDetails = null; }

    public static Hashtable<Integer, FinancialInstitution> getFinancialInstitutions(final Activity activity)
    {
        if (financialInstitutions == null) {

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
                            JSONArray symFinancialInstitutionData = responseJSON.getJSONArray("financialInstitutionData");
                            Log.i(TAG, "Got " + symFinancialInstitutionData.length() + " financial institutions");
                            financialInstitutions = new Hashtable<>();
                            for (int c = 0; c < symFinancialInstitutionData.length(); c++) {
                                JSONObject financialInstitution = symFinancialInstitutionData.getJSONObject(c);
                                Log.i(TAG, "Adding financial institution " + financialInstitution.getString("institutionName"));
                                financialInstitutions.put(financialInstitution.getInt("institutionId"),
                                    new FinancialInstitution(financialInstitution.getInt("institutionId"),
                                        financialInstitution.getString("institutionShortName"),
                                        InstitutionType.valueOf(financialInstitution.getString("institutionType"))
                                    )
                                );
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.i(TAG, "Failed to populate financial institutions " + ex.getMessage());
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

            return financialInstitutions;
        }
        return financialInstitutions;
    }

    public static ArrayList<CurrencyType> getCurrencies(final Activity activity)
    {
        if (currencyTypes == null) {

            final HTTPBackgroundTask backgroundTask = new HTTPBackgroundTask(
                    activity, GET, CURRENCY_URL, null
            );

            activity.runOnUiThread(new Runnable() {
                public void run() {

                    BTResponseObject btResponseObject;
                    try {
                        btResponseObject = backgroundTask.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        btResponseObject = new BTResponseObject(
                                BTResponseCode.GENERAL_ERROR.setMessage("Get currencies interrupted")
                        );
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        btResponseObject = new BTResponseObject(
                                BTResponseCode.GENERAL_ERROR.setMessage("Get currencies failed: " + e.getMessage())
                        );
                    }

                    final String responseMsg = btResponseObject.getMessage();

                    Log.i(TAG, "Get currencies response: " + responseMsg);

                    if (btResponseObject.getResponseCode().equals(SUCCESS)) {

                        try {

                            JSONObject responseJSON = new JSONObject(btResponseObject.getResponseObject().toString());

                            JSONArray symFinancialInstitutionData = responseJSON.getJSONArray("currencyData");

                            Log.i(TAG, "Got " + symFinancialInstitutionData.length() + " currencies");

                            currencyTypes = new ArrayList<>();

                            for (int c = 0; c < symFinancialInstitutionData.length(); c++) {
                                JSONObject financialInstitution = symFinancialInstitutionData.getJSONObject(c);
                                Log.i(TAG, "Adding currency " + financialInstitution.getString("currencyName"));
                                currencyTypes.add(new CurrencyType(
                                    financialInstitution.getInt("currencyId"),
                                    financialInstitution.getString("iso4217Code"))
                                );
                            }
                        } catch (final Exception ex) {
                            ex.printStackTrace();
                            Log.i(TAG, "Failed to populate currencies " + ex.getMessage());
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                makeText(activity, "Failed to get currencies: " + ex.getMessage(), LENGTH_LONG).show();
                                }
                            });
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

            return currencyTypes;
        }
        return currencyTypes;
    }

    public static ArrayList<CashoutAccount> getCashoutAccounts(final Activity activity) {
        if (cashoutAccounts == null) {

            final HTTPBackgroundTask backgroundTask = new HTTPBackgroundTask(
                activity, GET, USER_URL + "/" + getUserDetails(activity).getBtUserId() + "/cashoutAccount", null
            );

            activity.runOnUiThread(new Runnable() {
                public void run() {

                    BTResponseObject btResponseObject;
                    try {
                        btResponseObject = backgroundTask.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        btResponseObject = new BTResponseObject(
                                BTResponseCode.GENERAL_ERROR.setMessage("Get cashout accounts interrupted")
                        );
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        btResponseObject = new BTResponseObject(
                                BTResponseCode.GENERAL_ERROR.setMessage("Get cashout accounts failed: " + e.getMessage())
                        );
                    }

                    final String responseMsg = btResponseObject.getMessage();

                    Log.i(TAG, "Get cashout accounts response: " + responseMsg);

                    if (btResponseObject.getResponseCode().equals(SUCCESS)) {

                        try {

                            JSONObject responseJSON = new JSONObject(btResponseObject.getResponseObject().toString());

                            JSONArray cashoutAccountData = responseJSON.getJSONArray("cashoutAccountData");

                            Log.i(TAG, "Got " + cashoutAccountData.length() + " cashout accounts");

                            cashoutAccounts = new ArrayList<>();

                            for (int c = 0; c < cashoutAccountData.length(); c++) {
                                JSONObject cashoutAccount = cashoutAccountData.getJSONObject(c);
                                Log.i(TAG, "Adding cashout account " + cashoutAccount.getString("accountNickName"));
                                cashoutAccounts.add(new CashoutAccount(
                                        cashoutAccount.getInt("cashoutAccountId"),
                                        getFinancialInstitutions(activity).get(cashoutAccount.getInt("institutionId")),
                                        cashoutAccount.optString("accountNickName", null),
                                        cashoutAccount.optString("accountName", null),
                                        cashoutAccount.optString("accountNumber", null),
                                        cashoutAccount.optString("accountBranchCode", null),
                                        cashoutAccount.optString("accountPhone", null),
                                        cashoutAccount.optString("accountEmail", null)
                                    )
                                );
                            }
                        } catch (final Exception ex) {
                            ex.printStackTrace();
                            Log.i(TAG, "Failed to populate cashout accounts " + ex.getMessage());
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                makeText(activity, "Failed to get cashout accounts: " + ex.getMessage(), LENGTH_LONG).show();
                                }
                            });
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

            return cashoutAccounts;
        }
        return cashoutAccounts;
    }

    public static BTResponseCode addCashoutAccount(final Activity activity, final CashoutAccount cashoutAccount) {

        final Hashtable<String, String> addAccountParams = new Hashtable<>();
        addAccountParams.put("userId", String.valueOf(getUserDetails(activity).getBtUserId()));
        addAccountParams.put("institutionId", String.valueOf(cashoutAccount.getFinancialInstitution().getInstitutionId()));
        addAccountParams.put("accountNickName", cashoutAccount.getAccountNickName());
        if (!isNullOrEmpty(cashoutAccount.getAccountName())) { addAccountParams.put("accountName", cashoutAccount.getAccountName()); }

        switch (cashoutAccount.getFinancialInstitution().getInstitutionType()) {
            case BANK:
                if (!isNullOrEmpty(cashoutAccount.getAccountNumber())) { addAccountParams.put("accountNumber", cashoutAccount.getAccountNumber()); }
                break;
            case MOBILE_BANK:
                if (!isNullOrEmpty(cashoutAccount.getAccountPhone())) { addAccountParams.put("accountNumber", cashoutAccount.getAccountPhone()); }
                break;
            case ONLINE_BANK:
                if (!isNullOrEmpty(cashoutAccount.getAccountEmail())) { addAccountParams.put("accountNumber", cashoutAccount.getAccountEmail()); }
                break;
        }

        if (!isNullOrEmpty(cashoutAccount.getAccountBranchCode())) { addAccountParams.put("accountBranchCode", cashoutAccount.getAccountBranchCode()); }
        if (!isNullOrEmpty(cashoutAccount.getAccountPhone())) { addAccountParams.put("accountPhone", cashoutAccount.getAccountPhone()); }
        if (!isNullOrEmpty(cashoutAccount.getAccountEmail())) { addAccountParams.put("accountEmail", cashoutAccount.getAccountEmail()); }

        final HTTPBackgroundTask backgroundTask = new HTTPBackgroundTask(
            activity, POST, USER_URL + "/" + getUserDetails(activity).getBtUserId() + "/cashoutAccount", addAccountParams
        );

        final BTResponseCode[] responseCode = new BTResponseCode[1];

        activity.runOnUiThread(new Runnable() {
            public void run() {

                BTResponseObject btResponseObject;
                try {
                    btResponseObject = backgroundTask.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    btResponseObject = new BTResponseObject(
                            BTResponseCode.GENERAL_ERROR.setMessage("Add cashout account interrupted")
                    );
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    btResponseObject = new BTResponseObject(
                            BTResponseCode.GENERAL_ERROR.setMessage("Add cashout account failed: " + e.getMessage())
                    );
                }

                final String responseMsg = btResponseObject.getMessage();

                Log.i(TAG, "Add cashout account response: " + responseMsg);

                if (btResponseObject.getResponseCode().equals(SUCCESS)) {
                    cashoutAccounts = null;
                }

                responseCode[0] = btResponseObject.getResponseCode();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        makeText(activity, responseMsg, LENGTH_LONG).show();
                    }
                });
            }
        });

        return responseCode[0];
    }

    public static BTResponseCode removeCashoutAccount(final Activity activity, final CashoutAccount cashoutAccount) {

        final HTTPBackgroundTask backgroundTask = new HTTPBackgroundTask(activity, DELETE,
            USER_URL + "/" + getUserDetails(activity).getBtUserId() + "/cashoutAccount/" + cashoutAccount.getCashoutAccountId(),
            null
        );

        final BTResponseCode[] responseCode = new BTResponseCode[1];

        activity.runOnUiThread(new Runnable() {
            public void run() {

                BTResponseObject btResponseObject;
                try {
                    btResponseObject = backgroundTask.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    btResponseObject = new BTResponseObject(
                            BTResponseCode.GENERAL_ERROR.setMessage("Remove cashout account")
                    );
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    btResponseObject = new BTResponseObject(
                            BTResponseCode.GENERAL_ERROR.setMessage("Remove cashout account: " + e.getMessage())
                    );
                }

                final String responseMsg = btResponseObject.getMessage();

                Log.i(TAG, "Remove cashout account response: " + responseMsg);

                if (btResponseObject.getResponseCode().equals(SUCCESS)) {
                    cashoutAccounts = null;
                }

                responseCode[0] = btResponseObject.getResponseCode();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        makeText(activity, responseMsg, LENGTH_LONG).show();
                    }
                });
            }
        });

        return responseCode[0];
    }
}
