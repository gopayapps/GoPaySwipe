package net.beyondtelecom.gopayswipe.server;

/**
 * Created by Tich on 1/4/2018.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import net.beyondtelecom.gopayswipe.common.BTResponseCode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import static java.lang.String.valueOf;
import static net.beyondtelecom.gopayswipe.server.HTTPBackgrounTask.TASK_TYPE.POST;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class HTTPBackgrounTask extends AsyncTask<Void, Void, Boolean> {

    public enum TASK_TYPE { POST, PUT, GET }
    public static final String SERVER_URL = "http://localhost:8080/bt_api-1.0.0/bt/server/json/";
    public static final String SESSION_URL = SERVER_URL + "session";
    private HttpURLConnection connection = null;
    private String communicationResult = null;
    private final Context context;
    private final Hashtable params;
    private final TASK_TYPE taskType;
    private final String requestURL;
    private boolean isRunning;

    public HTTPBackgrounTask(Context context, TASK_TYPE taskType, String requestURL, Hashtable params) {
        this.context = context;
        this.taskType = taskType;
        this.requestURL = requestURL;
        this.params = params;
    }

    public boolean isRunning() { return isRunning; }

    @Override
    protected Boolean doInBackground(Void... params) {
        isRunning = true;
        String responseString = sendServerRequest();
        try
        {
            JSONObject responseJSON = new JSONObject(responseString);

            if (responseJSON.getInt("response_code") == BTResponseCode.SUCCESS.getCode()) {
                return true;
            } else if (responseJSON.getInt("response_code") < 0) {
                return false;
            }
            else {
                final String loginResponseStr = responseJSON.getString("response_message");
                Toast.makeText(context, loginResponseStr, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Communication Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        isRunning = false;
    }

    @Override
    protected void onCancelled() {
        isRunning = false;
    }

    private HttpURLConnection getConnection()
    {
        try {
            URL url = new URL(requestURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(taskType.name());
            if (taskType == POST) {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches (false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return connection;
    }

    public String sendServerRequest()
    {
        try {
            connection = getConnection();

            StringBuilder postParams = new StringBuilder();
            DataOutputStream writer = null;

            if (taskType == POST) {
                Enumeration keys = params.keys();
                while (keys.hasMoreElements()) {
                    String key = (String)keys.nextElement();
                    postParams.append(key).append("=").append(params.get(key));
                    if (keys.hasMoreElements()) { postParams.append("&"); }
                }
                connection.setRequestProperty("Content-Length", valueOf(postParams.toString().length()));
                writer = new DataOutputStream(connection.getOutputStream());
                writer.writeBytes(postParams.toString());
                writer.flush();
            }


            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            communicationResult = "";
            while ((line = reader.readLine()) != null) { communicationResult += line; }

            if (writer != null) { writer.close(); }
            reader.close();
            return communicationResult;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}