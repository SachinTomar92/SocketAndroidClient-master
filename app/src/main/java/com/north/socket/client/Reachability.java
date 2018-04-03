package com.north.socket.client;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
/**
 * Created by sree on 18/01/16.
 */



    /**
     * A utility class for determining network availability.
     */
    public class Reachability {

        /**
         * A convienence method for retreiving the ConnectivityManager in a given
         * context.
         *
         * @param context
         * @return the ConnectivityManager instance for the given context.
         */
        public static ConnectivityManager getConnectivityManager(Context context) {
            return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        /**
         * A convienence method for retreiving the {@link NetworkInfo} for the
         * currently active network.
         *
         * @param context
         * @return a {@link NetworkInfo} instance for the currently active network.
         */
        public static NetworkInfo getActiveNetworkInfo(Context context) {
            return getConnectivityManager(context).getActiveNetworkInfo();
        }

        /**
         * A convienence method for determining if networking is available for the
         *
         * @param context
         * @return true if and only if the currently active network is available and
         *         connected.
         */
        public static boolean isNetworkingAvailable(Context context) {
            NetworkInfo info = getActiveNetworkInfo(context);
            if (info==null)
            {
                return false;
            }
            else {
                return (info.isAvailable() && info.isConnected());
            }
        }

    }
