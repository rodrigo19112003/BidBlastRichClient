package api;

import api.requests.register.UserRegisterBody;
import api.responses.register.UserRegisterJSONResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IRegisterService {
    @POST("accounts/")
     Call<UserRegisterJSONResponse> createAccount(@Body UserRegisterBody body);
}
