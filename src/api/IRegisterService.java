package api;

import requests.register.UserRegisterBody;
import responses.register.UserRegisterJSONResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IRegisterService {
    @POST("account")
    Call<UserRegisterJSONResponse> createAccount(@Body UserRegisterBody body);
}
