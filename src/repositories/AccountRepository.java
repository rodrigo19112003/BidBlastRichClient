package repositories;

import api.ApiClient;
import api.IEmptyProcessStatusListener;
import requests.register.UserRegisterBody;
import responses.register.UserRegisterJSONResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import api.IRegisterService;

public class AccountRepository {
    private final IRegisterService registerService;

    public AccountRepository() {
        registerService = ApiClient.getInstance().getRegisterService();
    }

    public void createAccount(UserRegisterBody body, IEmptyProcessStatusListener creationListener) {
        registerService.createAccount(body).enqueue(new Callback<UserRegisterJSONResponse>() {
            @Override
            public void onResponse(Call<UserRegisterJSONResponse> call, Response<UserRegisterJSONResponse> response) {
                if (response.isSuccessful()) {
                    UserRegisterJSONResponse responseBody = response.body();
                    if (responseBody != null && responseBody.getAccount() != null) {
                        creationListener.onSuccess();
                    } else {
                        creationListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                } else {
                    creationListener.onError(ProcessErrorCodes.FATAL_ERROR);
                }
            }

            @Override
            public void onFailure(Call<UserRegisterJSONResponse> call, Throwable t) {
                creationListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
}
