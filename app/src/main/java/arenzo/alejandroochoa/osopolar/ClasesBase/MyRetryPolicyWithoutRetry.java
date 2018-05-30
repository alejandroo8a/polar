package arenzo.alejandroochoa.osopolar.ClasesBase;

import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

public class MyRetryPolicyWithoutRetry implements RetryPolicy
{

    @Override
    public int getCurrentTimeout() {
        return  2000;
    }

    @Override
    public int getCurrentRetryCount() {
        return 0;
    }

    @Override
    public void retry(VolleyError error) throws VolleyError {
        throw(error);
    }
}