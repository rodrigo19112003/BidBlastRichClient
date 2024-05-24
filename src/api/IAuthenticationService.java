package api;

import api.requests.authentication.UserCredentialsBody;
import api.responses.authentication.UserLoginJSONResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IAuthenticationService {
    @POST("sessions/")
    Call<UserLoginJSONResponse> login(@Body UserCredentialsBody credentials);
}
