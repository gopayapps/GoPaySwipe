package net.beyondtelecom.gopayswipe.server;

/**
 * Created by Tich on 1/4/2018.
 */

import android.os.AsyncTask;

import java.util.Hashtable;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class BackgroundTask extends AsyncTask<Void, Void, Boolean> {

    private final Hashtable params;

    BackgroundTask(Hashtable params) {
        this.params = params;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
    }

    @Override
    protected void onCancelled() {
    }
}