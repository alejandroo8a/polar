package arenzo.alejandroochoa.osopolar.Peticiones;

import android.content.Context;

/**
 * Created by AlejandroMissael on 25/04/2017.
 */

public class webServices {

    private static final String TAG = "webServices";
    public Context context;
    SingletonVolley requestQueue;

    public webServices(Context context) {
        this.context=context;
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        requestQueue= SingletonVolley.getInstance(context);
        requestQueue.getRequestQueue();
    }
}
