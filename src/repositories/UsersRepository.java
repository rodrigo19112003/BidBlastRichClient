package repositories;

import api.ApiClient;
import api.requests.user.UserRegisterBody;
import api.responses.user.UserRegisterJSONResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import api.IUserService;
import api.responses.auctions.UserJSONResponse;
import java.util.ArrayList;
import java.util.List;
import lib.Session;
import model.User;

public class UsersRepository {
    public void getUsersList(String searchQuery,
            int limit,
            int offset,
            IProcessStatusListener<List<User>> statusListener) {
        IUserService usersService = ApiClient.getInstance().getUserService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        
        usersService.getUsersList(authHeader, searchQuery, limit, offset).enqueue(new Callback<List<UserJSONResponse>>() {
            @Override
            public void onResponse(Call<List<UserJSONResponse>> call, Response<List<UserJSONResponse>> response) {
                if (response.isSuccessful()) {
                    List<UserJSONResponse> body = response.body();
                    
                    if (body != null) {
                        List<User> usersList = new ArrayList(); 
                        
                        for(UserJSONResponse userRes : body) {
                            User user = new User();
                            
                            user.setId(userRes.getId());
                            user.setFullName(userRes.getFullName());
                            user.setAvatar(userRes.getAvatar());
                            user.setEmail(userRes.getEmail());
                            user.setPhoneNumber(userRes.getPhoneNumber());
                            user.setRoles(userRes.getRoles());
                            user.setIsRemovable(userRes.isIsRemovable());
                            
                            usersList.add(user);
                        }
                        
                        statusListener.onSuccess(usersList);
                    } else {
                    statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<UserJSONResponse>> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void createUser(
        UserRegisterBody userBody, 
        IEmptyProcessStatusListener statusListener) {
        IUserService usersService = ApiClient.getInstance().getUserService();
        usersService.createUser(userBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    statusListener.onSuccess();
                }else{
                    if(response.code() == 400) {
                        statusListener.onError(ProcessErrorCodes.REQUEST_FORMAT_ERROR);
                    } else {
                        System.out.println(response.code());
                        statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
    
    public void deleteUser(
        int idProfile,
        IEmptyProcessStatusListener statusListener) {
        IUserService usersService = ApiClient.getInstance().getUserService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        
        usersService.deleteUser(authHeader, idProfile).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    statusListener.onSuccess();
                }else{
                    if(response.code() == 400) {
                        statusListener.onError(ProcessErrorCodes.REQUEST_FORMAT_ERROR);
                    } else {
                        statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
}
