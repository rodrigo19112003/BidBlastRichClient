package api;

import java.io.IOException;
import lib.Session;
import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.header("Set-Authorization") != null) {
            String newAuthToken = response.header("Set-Authorization");
            Session.getInstance().setToken(newAuthToken);
            System.out.println("Token nuevo: " + Session.getInstance().getToken());
        }

        return response;
    }
}
