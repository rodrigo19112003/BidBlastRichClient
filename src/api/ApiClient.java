package api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;


public class ApiClient {
    private static final ApiClient apiClient = new ApiClient();
    public static final String API_BASE_URL = "http://localhost:3000/api/";
    private final Retrofit retrofit;
    private IAuthenticationService authenticationService;
    private IUserService userService;
    private IAuctionCategoriesService auctionCategoriesService;
    private IAuctionsService auctionsService;
    private IOffersService offersService;

    public static ApiClient getInstance() {
        return apiClient;
    }

    private ApiClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(new AuthorizationInterceptor())
            .build();

        retrofit = new Retrofit.Builder()
            .baseUrl(ApiClient.API_BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build();
    }
    
    public IAuthenticationService getAuthenticationService() {
        if (authenticationService == null) {
            authenticationService = retrofit.create(IAuthenticationService.class);
        }

        return authenticationService;
    }
    
    public IUserService getUserService() {
        if (userService == null) {
            userService = retrofit.create(IUserService.class);
        }
        return userService;
    }
    
    public IAuctionsService getAuctionsService() {
        if (auctionsService == null) {
            auctionsService = retrofit.create(IAuctionsService.class);
        }

        return auctionsService;
    }
    
    public IAuctionCategoriesService getAuctionCategoriesService() {
        if (auctionCategoriesService == null) {
            auctionCategoriesService = retrofit.create(IAuctionCategoriesService.class);
        }

        return auctionCategoriesService;
    }
    
    public IOffersService getOffersService() {
        if (offersService == null) {
            offersService = retrofit.create(IOffersService.class);
        }

        return offersService;
    }
}
