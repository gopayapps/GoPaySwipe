package net.beyondtelecom.gopayswipe.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

/***************************************************************************
 *                                                                         *
 * Created:     16 / 01 / 2018                                             *
 * Author:      Tsungai Kaviya                                             *
 * Contact:     tsungai.kaviya@gmail.com                                   *
 *                                                                         *
 ***************************************************************************/
public class ActivityCommon {

    private static final String BASE_TAG = "GoPay_";

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

}
