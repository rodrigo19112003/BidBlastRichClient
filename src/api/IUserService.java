package api;

import api.requests.user.UserRegisterBody;
import api.responses.auctions.UserJSONResponse;
import api.responses.user.UserRegisterJSONResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IUserService {
    @GET("users/")
    Call<List<UserJSONResponse>> getUsersList(
        @Header("Authorization") String authHeader,
        @Query("query") String searchQuery,
        @Query("limit") int limit,
        @Query("offset") int offset
    );
    
    @DELETE("users/{idProfile}")
    Call<Void> deleteUser(
        @Header("Authorization") String authHeader,
        @Path("idProfile") int idProfile
    );
    
    @POST("users/")
    Call<Void> createUser(
        @Body UserRegisterBody body
    );
    
    @PUT("users/")
    Call<Void> updateUser(
        @Header("Authorization") String authHeader,
        @Body UserRegisterBody body
    );
}
