package net.beyondtelecom.gopay.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.beyondtelecom.gopay.dto.CashoutAccount;
import net.beyondtelecom.gopay.dto.CurrencyType;
import net.beyondtelecom.gopay.dto.FinancialInstitution;
import net.beyondtelecom.gopay.dto.InstitutionType;
import net.beyondtelecom.gopay.dto.UserDetails;
import net.beyondtelecom.gopay.persistence.GPPersistence;
import net.beyondtelecom.gopay.server.HTTPBackgroundTask;
import net.beyondtelecom.gopay.server.HTTPBackgroundTask.TASK_TYPE;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.lang.String.format;
import static net.beyondtelecom.gopay.common.BTResponseCode.GENERAL_ERROR;
import static net.beyondtelecom.gopay.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopay.common.Validator.isNullOrEmpty;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.CURRENCY_URL;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.FINANCIAL_INSTITUTIONS_URL;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.TASK_TYPE.DELETE;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.TASK_TYPE.GET;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.TASK_TYPE.POST;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.TASK_TYPE.PUT;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.USER_URL;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.WALLET_URL;

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
    private static Double walletBalance;
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

    public static void clearWalletBalanceCache() { walletBalance = null; }

    public static void clearUserDetailsCache() { userDetails = null; }

    private static BTResponseObject<String> executeBackgroundTask(
            final Activity activity, final String taskDescription,
            TASK_TYPE taskType, String url, Hashtable<String, String> params) {

        final HTTPBackgroundTask backgroundTask = new HTTPBackgroundTask(activity, taskType, url, params);

        final BTResponseObject<String>[] btResponseObject = new BTResponseObject[1];

        activity.runOnUiThread(new Runnable() {
            public void run() {

                try {
                    btResponseObject[0] = backgroundTask.execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                    btResponseObject[0] = new BTResponseObject<>(GENERAL_ERROR.setMessage(taskDescription + " failed: " + e.getMessage()));
                    return;
                }

                Log.i(TAG, taskDescription + " response: " + btResponseObject[0].getResponseObject());

                if (!btResponseObject[0].getResponseCode().equals(SUCCESS)) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            makeText(activity, btResponseObject[0].getMessage(), LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        return btResponseObject[0];
    }

    public static Hashtable<Integer, FinancialInstitution> getFinancialInstitutions(final Activity activity)
    {
        if (financialInstitutions == null) {
            BTResponseObject<String> responseObject = executeBackgroundTask(
                activity, "Populate financial institutions", GET, FINANCIAL_INSTITUTIONS_URL, null
            );

            if (responseObject.getResponseCode().equals(SUCCESS)) {
                try {
                    JSONObject responseJSON = new JSONObject(responseObject.getResponseObject());
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
                } catch (final Exception ex) {
                    financialInstitutions = null;
                    ex.printStackTrace();
                    Log.i(TAG, "Failed to populate financial institutions: " + ex.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            makeText(activity, "Failed to populate financial institutions: " + ex.getMessage(), LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
        return financialInstitutions;
    }

    public static ArrayList<CurrencyType> getCurrencies(final Activity activity)
    {
        if (currencyTypes == null) {

            BTResponseObject<String> responseObject = executeBackgroundTask(
                activity, "Get currencies", GET, CURRENCY_URL, null
            );

            if (responseObject.getResponseCode().equals(SUCCESS)) {
                try {
                    JSONObject responseJSON = new JSONObject(responseObject.getResponseObject());
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
                    currencyTypes = null;
                    ex.printStackTrace();
                    Log.i(TAG, "Failed to populate currencies: " + ex.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            makeText(activity, "Failed to populate currencies: " + ex.getMessage(), LENGTH_LONG).show();
                        }
                    });
                }
            }
        }

        return currencyTypes;
    }

    public static ArrayList<CashoutAccount> getCashoutAccounts(final Activity activity) {
        if (cashoutAccounts == null) {
            BTResponseObject<String> responseObject = executeBackgroundTask(
                activity, "Get cashout accounts", GET,
                USER_URL + "/" + getUserDetails(activity).getBtUserId() + "/cashoutAccount",
                null
            );

            if (responseObject.getResponseCode().equals(SUCCESS)) {
                try {
                    JSONObject responseJSON = new JSONObject(responseObject.getResponseObject());
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
                        ));
                    }
                } catch (final Exception ex) {
                    cashoutAccounts = null;
                    ex.printStackTrace();
                    Log.i(TAG, "Failed to populate cashout accounts " + ex.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            makeText(activity, "Failed to populate cashout accounts: " + ex.getMessage(), LENGTH_LONG).show();
                        }
                    });
                }
            }
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

        final BTResponseObject<String> responseObject = executeBackgroundTask(
            activity, "Add cashout account", POST,
            USER_URL + "/" + getUserDetails(activity).getBtUserId() + "/cashoutAccount",
            addAccountParams
        );

        if (responseObject.getResponseCode().equals(SUCCESS)) {
            cashoutAccounts = null;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                makeText(activity, responseObject.getMessage(), LENGTH_LONG).show();
                }
            });
        }

        return responseObject.getResponseCode();
    }

    public static BTResponseCode removeCashoutAccount(final Activity activity, final CashoutAccount cashoutAccount) {

        final BTResponseObject<String> responseObject = executeBackgroundTask(
            activity, "Remove cashout account", DELETE,
            USER_URL + "/" + getUserDetails(activity).getBtUserId() + "/cashoutAccount/" + cashoutAccount.getCashoutAccountId(),
            null
        );

        if (responseObject.getResponseCode().equals(SUCCESS)) {
            cashoutAccounts = null;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                makeText(activity, responseObject.getMessage(), LENGTH_LONG).show();
                }
            });
        }

        return responseObject.getResponseCode();
    }

    public static BTResponseCode registerUser(final Activity activity, final Hashtable<String, String> registerParams) {

        final BTResponseObject<String> responseObject = executeBackgroundTask(
            activity, "Register user", POST, HTTPBackgroundTask.USER_URL, registerParams);

        if (responseObject.getResponseCode().equals(SUCCESS)) {
            clearUserDetailsCache();
            try {

                JSONObject responseJSON = new JSONObject(responseObject.getResponseObject());
                JSONObject systemUserData = responseJSON.getJSONArray("systemUserData").getJSONObject(0);
                UserDetails newUserDetails = new UserDetails(
                    systemUserData.getLong("userId"),
                    systemUserData.getLong("walletId"),
                    systemUserData.optString("username", null),
                    systemUserData.optString("firstName", null),
                    systemUserData.optString("lastName", null),
                    systemUserData.optString("companyName", null),
                    systemUserData.optString("msisdn", null),
                    systemUserData.optString("email", null)
                );

                getGoPayDB(activity).setUserDetails(newUserDetails);
                Log.i(TAG, format("Saved user %s %s (%s) with id %s",
                        newUserDetails.getFirstName(), newUserDetails.getLastName(),
                        newUserDetails.getUsername(), newUserDetails.getBtUserId()
                ));
            } catch (final Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "Failed to update profile: " + ex.getMessage());
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                    makeText(activity, "Failed to update profile: " + ex.getMessage(), LENGTH_LONG).show();
                    }
                });
            }
        }
        return responseObject.getResponseCode();
    }

    public static BTResponseCode loginUser(final Activity activity, final Hashtable<String, String> loginParams) {

        final BTResponseObject<String> responseObject = executeBackgroundTask(
            activity, "Login user", POST, HTTPBackgroundTask.SESSION_URL, loginParams);

        if (responseObject.getResponseCode().equals(SUCCESS)) {
            clearUserDetailsCache();
            try {

                JSONObject responseJSON = new JSONObject(responseObject.getResponseObject());
                JSONObject systemUserData = responseJSON.getJSONArray("systemUserData").getJSONObject(0);
                UserDetails newUserDetails = new UserDetails(
                    systemUserData.getLong("userId"),
                    systemUserData.getLong("walletId"),
                    systemUserData.optString("username", null),
                    systemUserData.optString("firstName", null),
                    systemUserData.optString("lastName", null),
                    systemUserData.optString("companyName", null),
                    systemUserData.optString("msisdn", null),
                    systemUserData.optString("email", null)
                );

                getGoPayDB(activity).setUserDetails(newUserDetails);
                Log.i(TAG, format("Saved user %s %s (%s) with id %s",
                        newUserDetails.getFirstName(), newUserDetails.getLastName(),
                        newUserDetails.getUsername(), newUserDetails.getBtUserId()
                ));
            } catch (final Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "Failed to update profile: " + ex.getMessage());
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                    makeText(activity, "Failed to update profile: " + ex.getMessage(), LENGTH_LONG).show();
                    }
                });
            }
        }
        return responseObject.getResponseCode();
    }

    public static BTResponseCode updateProfile(final Activity activity, final Hashtable<String, String> updateDetails) {

        final BTResponseObject<String> responseObject = executeBackgroundTask(
            activity, "Update profile", PUT,
            HTTPBackgroundTask.USER_URL + "/" + getUserDetails(activity).getBtUserId(),
            updateDetails);

        if (responseObject.getResponseCode().equals(SUCCESS)) {
            clearUserDetailsCache();
            try {

                JSONObject responseJSON = new JSONObject(responseObject.getResponseObject());
                JSONObject systemUserData = responseJSON.getJSONArray("systemUserData").getJSONObject(0);
                UserDetails newUserDetails = new UserDetails(
                    systemUserData.getLong("userId"),
                    systemUserData.getLong("walletId"),
                    systemUserData.optString("username", null),
                    systemUserData.optString("firstName", null),
                    systemUserData.optString("lastName", null),
                    systemUserData.optString("companyName", null),
                    systemUserData.optString("msisdn", null),
                    systemUserData.optString("email", null)
                );
                getGoPayDB(activity).setUserDetails(newUserDetails);
                Log.i(TAG, format("Saved user %s %s (%s) with id %s",
                        newUserDetails.getFirstName(), newUserDetails.getLastName(),
                        newUserDetails.getUsername(), newUserDetails.getBtUserId()
                ));
            } catch (final Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "Failed to update profile: " + ex.getMessage());
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                    makeText(activity, "Failed to update profile: " + ex.getMessage(), LENGTH_LONG).show();
                    }
                });
            }
        }
        return responseObject.getResponseCode();
    }

    public static BTResponseObject<Double> getWallet(final Activity activity) {

        if (walletBalance == null) {
            final BTResponseObject<String> responseObject = executeBackgroundTask(
                    activity, "Get wallet details", GET,
                    WALLET_URL + "/" + getUserDetails(activity).getWalletId(),
                    null
            );

            if (responseObject.getResponseCode().equals(SUCCESS)) {

                try {
                    JSONObject responseJSON = new JSONObject(responseObject.getResponseObject());
                    JSONArray walletData = responseJSON.getJSONArray("walletData");
                    JSONObject wallet = walletData.getJSONObject(0);
                    walletBalance = wallet.getDouble("currentBalance");
                } catch (final Exception ex) {
                    walletBalance = null;
                    ex.printStackTrace();
                    Log.i(TAG, "Failed to get wallet balance " + ex.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            makeText(activity, "Failed to get wallet balance: " + ex.getMessage(), LENGTH_LONG).show();
                        }
                    });
                    return new BTResponseObject<>(GENERAL_ERROR);
                }
            }
        }
        return new BTResponseObject<>(SUCCESS, walletBalance);
    }
}
