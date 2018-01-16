package net.beyondtelecom.gopayswipe.server;

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

import net.beyondtelecom.gopayswipe.common.ActivityCommon;
import net.beyondtelecom.gopayswipe.common.BTResponseCode;

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
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.CONNECTION_FAILED;
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.GENERAL_ERROR;
import static net.beyondtelecom.gopayswipe.common.BTResponseCode.SUCCESS;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgroundTask.TASK_TYPE.POST;

public class HTTPBackgroundTask extends AsyncTask<Void, Void, BTResponseCode> {

    private static final String TAG = ActivityCommon.getTag(HTTPBackgroundTask.class);
    public enum TASK_TYPE { POST, PUT, GET }
    public static final String SERVER_URL = "http://192.168.1.15:8080/bt_api-1.0.0/bt/server/json/goPay/";
    public static final Integer CONNECT_TIMEOUT = 10000;
    public static final Integer READ_TIMEOUT = 20000;
    public static final String SESSION_URL = SERVER_URL + "session";
    public static final String REGISTER_URL = SERVER_URL + "user";
    private final ProgressDialog progressDialog;
    private final Hashtable params;
    private final TASK_TYPE taskType;
    private final String requestURL;
    private BTResponseCode btResponseCode;

    public HTTPBackgroundTask(Activity activity, TASK_TYPE taskType,
                              String requestURL, Hashtable params) {
        this.progressDialog = new ProgressDialog(activity);
        this.taskType = taskType;
        this.requestURL = requestURL;
        this.params = params;
    }

    public BTResponseCode getBtResponseCode() { return btResponseCode; }

    public void setBtResponseCode(BTResponseCode btResponseCode) { this.btResponseCode = btResponseCode; }

    @Override
    protected BTResponseCode doInBackground(Void... params) {
        Enumeration keys = this.params.keys();
        StringBuilder paramsBuilder = new StringBuilder();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            paramsBuilder.append(key).append("=").append(this.params.get(key));
            if (keys.hasMoreElements()) { paramsBuilder.append("&"); }
        }

        Log.i(TAG, "Performing background tasks, params: " + paramsBuilder.toString());

        final String responseString = sendServerRequest();

        try
        {
            Log.i(TAG, "Response String: " + responseString);

            if (responseString == null) {
                return btResponseCode;
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
                return btResponseCode;
            } else if (responseCode < 0) {
                Log.w(TAG, "Operation Failed: " + responseMessage);
                return btResponseCode;
            } else {
                Log.w(TAG, "Operation Failed: " + responseMessage);
                return btResponseCode;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception occurred processing response: " + e.getMessage());
            setBtResponseCode(GENERAL_ERROR.setMessage("Failed! Invalid server response received"));
            return btResponseCode;
        }
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Processing...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(final BTResponseCode success) {
        progressDialog.hide();
    }

    @Override
    protected void onCancelled() {
        progressDialog.hide();
    }

    private HttpURLConnection getConnection()
    {
        try {
            Log.i(TAG, "Opening connection to " + requestURL);
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(taskType.name());
            if (taskType == POST) {
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

    private String sendServerRequest()
    {
        try {
            HttpURLConnection connection = getConnection();
            if (connection == null) { return null; }

            StringBuilder postParams = new StringBuilder();
            DataOutputStream writer = null;

            if (taskType == POST) {
                Log.i(TAG, "Appending parameters for POST");
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
            }

            Log.i(TAG, "Reading input stream");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder communicationResult = new StringBuilder();
            while ((line = reader.readLine()) != null) { communicationResult.append(line); }

            if (writer != null) { writer.close(); }
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