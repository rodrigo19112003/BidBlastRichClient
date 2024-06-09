package api;

import api.requests.user.UserRegisterBody;
import api.responses.auctions.UserJSONResponse;
import api.responses.user.UserRegisterJSONResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IUserService {
    @GET("users/")
    Call<List<UserJSONResponse>> getUsersList(
        @Header("Authorization") String authHeader,
        @Query("query") String searchQuery,
        @Query("limit") int limit,
        @Query("offset") int offset
    );
    
    @POST("users/")
     Call<UserRegisterJSONResponse> createUser(@Body UserRegisterBody body);
}
