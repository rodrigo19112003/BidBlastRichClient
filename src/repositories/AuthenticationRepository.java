package repositories;

import api.IAuthenticationService;
import api.requests.authentication.UserCredentialsBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import api.ApiClient;
import api.responses.authentication.UserLoginJSONResponse;
import lib.Session;
import model.User;

public class AuthenticationRepository {
    public void login(UserCredentialsBody credentials, IEmptyProcessStatusListener statusListener) {
        IAuthenticationService authService = ApiClient.getInstance().getAuthenticationService();

        authService.login(credentials).enqueue(new Callback<UserLoginJSONResponse>() {
            @Override
            public void onResponse(Call<UserLoginJSONResponse> call, Response<UserLoginJSONResponse> response) {
                if(response.isSuccessful()) {
                    UserLoginJSONResponse body = response.body();

                    if(body != null) {
                        User user = new User(
                            body.getId(),
                            body.getFullName(),
                            body.getPhoneNumber(),
                            body.getAvatar(),
                            body.getEmail(),
                            body.getRoles()
                        );

                        Session session = Session.getInstance();
                        session.setToken(body.getToken());
                        session.setUser(user);

                        statusListener.onSuccess();
                    } else {
                        statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                }
            }

            @Override
            public void onFailure(Call<UserLoginJSONResponse> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
}