package api;

import api.requests.auctioncategories.AuctionCategoryBody;
import api.responses.auctioncategories.AuctionCategoryJSONResponse;
import api.responses.auctioncategories.UpdatedAuctionCategoryJSONResponse;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IAuctionCategoriesService {
    @GET("auction-categories/")
    Call<List<AuctionCategoryJSONResponse>> getAuctionCategoriesList(
        @Header("Authorization") String authHeader
    );

    @PUT("auction-categories/{id}")
    Call<UpdatedAuctionCategoryJSONResponse> updateAuctionCategory(
        @Header("Authorization") String authHeader,
        @Path("id") int idAuctionCategory,
        @Body AuctionCategoryBody auctionCategoryBody
    );
}
