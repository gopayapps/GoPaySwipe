package net.beyondtelecom.gopay.server;

/***************************************************************************
 *                                                                         *
 * Created:     16 / 01 / 2018                                             *
 * Author:      Tsungai Kaviya                                             *
 * Contact:     tsungai.kaviya@gmail.com                                   *
 *                                                                         *
 ***************************************************************************/

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import net.beyondtelecom.gopay.common.ActivityCommon;
import net.beyondtelecom.gopay.common.BTResponseCode;
import net.beyondtelecom.gopay.common.BTResponseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import static java.lang.String.valueOf;
import static net.beyondtelecom.gopay.common.BTResponseCode.CONNECTION_FAILED;
import static net.beyondtelecom.gopay.common.BTResponseCode.GENERAL_ERROR;
import static net.beyondtelecom.gopay.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.TASK_TYPE.GET;
import static net.beyondtelecom.gopay.server.HTTPBackgroundTask.TASK_TYPE.POST;

public class HTTPBackgroundTask extends AsyncTask<Void, Void, BTResponseObject<String>> {

    private static final String TAG = ActivityCommon.getTag(HTTPBackgroundTask.class);
    private static final String SERVER_URL = "http://GHOST:8080/bt_api-1.0.0/bt/server/json/";
    public static final String SESSION_URL = SERVER_URL + "goPay/session";
    public static final String USER_URL = SERVER_URL + "goPay/user";
    public static final String WALLET_URL = SERVER_URL + "wallet";
    public static final String FINANCIAL_INSTITUTIONS_URL = SERVER_URL + "financialInstitution";
    public static final String CURRENCY_URL = SERVER_URL + "currency";
    public enum TASK_TYPE { POST, PUT, GET, DELETE }
    private static final Integer CONNECT_TIMEOUT = 10000;
    private static final Integer READ_TIMEOUT = 20000;
    private final ProgressDialog progressDialog;
    private final Hashtable<String, String> params;
    private final TASK_TYPE taskType;
    private final String requestURL;
    private BTResponseCode btResponseCode;

    public HTTPBackgroundTask(Activity activity, TASK_TYPE taskType,
                              String requestURL, Hashtable<String, String> params) {
        this.progressDialog = new ProgressDialog(activity);
        this.taskType = taskType;
        this.requestURL = requestURL;
        this.params = params;
    }

    public BTResponseCode getBtResponseCode() { return btResponseCode; }

    private void setBtResponseCode(BTResponseCode btResponseCode) { this.btResponseCode = btResponseCode; }

    @Override
    protected BTResponseObject<String> doInBackground(Void... params) {

        Log.i(TAG, "Performing background " + taskType.name() + " to " + requestURL);

        final String responseString = sendServerRequest();

        try
        {
            if (responseString == null) {
                return new BTResponseObject<>(btResponseCode);
            }

            JSONObject responseJSON = new JSONObject(responseString);

            JSONObject btResponse;
            if (!responseJSON.has("btresponse")) {
                btResponse = responseJSON;
            } else {
                btResponse = new JSONObject(responseJSON.getString("btresponse"));
            }

            Log.i(TAG, "Decoding response JSON");
            Integer responseCode = btResponse.getInt("response_code");
            final String responseMessage = btResponse.getString("response_message");

            setBtResponseCode(BTResponseCode.valueOf(responseCode).setMessage(responseMessage));

            if (responseCode == SUCCESS.getCode()) {
                Log.i(TAG, "Operation Successful: " + responseMessage);
                return new BTResponseObject<>(btResponseCode, responseString);
            } else if (responseCode < 0) {
                Log.w(TAG, "Operation Failed: " + responseMessage);
                return new BTResponseObject<>(btResponseCode, responseString);
            } else {
                Log.w(TAG, "Operation Failed: " + responseMessage);
                return new BTResponseObject<>(btResponseCode, responseString);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception occurred processing response: " + e.getMessage());
            setBtResponseCode(GENERAL_ERROR.setMessage("Failed! Invalid server response received"));
            return new BTResponseObject<>(btResponseCode);
        }
    }

    @Override
    protected void onPreExecute() {
//        progressDialog.setMessage("Processing...");
//        progressDialog.show();
    }

    @Override
    protected void onPostExecute(final BTResponseObject<String> btResponseObject) {
//        progressDialog.hide();
    }

    @Override
    protected void onCancelled() {
//        progressDialog.hide();
    }

    private HttpURLConnection getConnection()
    {
        try {

            String connectionURL = requestURL;

            if (taskType.equals(GET) && params != null && params.size() > 0) {
                StringBuilder getParams = new StringBuilder();
                Log.i(TAG, "Appending query parameters for GET");
                Enumeration keys = params.keys();
                while (keys.hasMoreElements()) {
                    String key = (String)keys.nextElement();
                    getParams.append(key).append("=").append(params.get(key));
                    if (keys.hasMoreElements()) { getParams.append("&"); }
                }
                connectionURL += "?" + getParams.toString();
            }

            Log.i(TAG, "Opening connection to " + connectionURL);
            URL url = new URL(connectionURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(taskType.name());
            if (taskType.equals(POST)) {
                connection.setDoOutput(true);
                Log.i(TAG, "Setting content-type for POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches (false);
            return connection;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            setBtResponseCode(CONNECTION_FAILED.setMessage("Failed to connect to server"));
            Log.e(TAG, "Exception Occurred opening connection: " + ex.getMessage());
            return null;
        }
    }

    private String sendServerRequest() {
        try {
            HttpURLConnection connection = getConnection();
            if (connection == null) { return null; }


            if (taskType.equals(POST)) {
                DataOutputStream writer;
                Log.i(TAG, "Appending parameters for POST");
                StringBuilder postParams = new StringBuilder();
                Enumeration keys = params.keys();
                while (keys.hasMoreElements()) {
                    String key = (String)keys.nextElement();
                    postParams.append(key).append("=").append(params.get(key));
                    if (keys.hasMoreElements()) { postParams.append("&"); }
                }
                connection.setRequestProperty("Content-Length", valueOf(postParams.toString().length()));
                Log.i(TAG, "Writing POST output data");
                writer = new DataOutputStream(connection.getOutputStream());
                writer.writeBytes(postParams.toString());
                writer.flush();
                writer.close();
            }

            Log.i(TAG, "Reading input stream");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder communicationResult = new StringBuilder();
            while ((line = reader.readLine()) != null) { communicationResult.append(line); }

            reader.close();
            Log.i(TAG, "Got response: " + communicationResult.toString());
            return communicationResult.toString();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            setBtResponseCode(CONNECTION_FAILED.setMessage("Server communication failed"));
            Log.e(TAG, "Exception Occurred sendingServerRequest: " + ex.getMessage());
            return null;
        }
    }

}