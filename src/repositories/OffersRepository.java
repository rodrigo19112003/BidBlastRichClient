package repositories;

import api.ApiClient;
import api.IOffersService;
import api.requests.offers.OfferCreationBody;
import api.responses.authentication.UserLoginJSONResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.Session;
import model.User;
import org.jetbrains.kotlin.com.google.gson.Gson;
import org.jetbrains.kotlin.com.google.gson.JsonObject;
import repositories.businesserrors.CreateOfferCodes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OffersRepository {
    public void createOffer(
        OfferCreationBody offer, 
        IEmptyProcessWithBusinessErrorListener<CreateOfferCodes> statusListener
    ) {
        IOffersService offersService = ApiClient.getInstance().getOffersService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        
        offersService.createOffer(authHeader, offer).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    statusListener.onSuccess();
                } else {
                    if(response.code() == 401 || response.code() == 403) {
                        statusListener.onError(CreateOfferCodes.UNAUTHORIZED);
                    } else if (response.code() == 400 && response.errorBody() != null) {
                        try {
                            String errorBodyString = response.errorBody().string();
                            JsonObject jsonErrorBody = new Gson().fromJson(errorBodyString, JsonObject.class);
                            
                            if(jsonErrorBody.has("apiErrorCode")) {
                                String apiErrorCode = jsonErrorBody.get("apiErrorCode").getAsString();
                                
                                switch(apiErrorCode) {
                                    case "COFR-400001":
                                        statusListener.onError(CreateOfferCodes.OFFER_OVERCOMED);
                                        break;
                                    case "COFR-400002":
                                        statusListener.onError(CreateOfferCodes.AUCTION_NOT_FOUND);
                                        break;
                                    case "COFR-400003":
                                        statusListener.onError(CreateOfferCodes.AUCTION_FINISHED);
                                        break;
                                    case "COFR-400004":
                                        statusListener.onError(CreateOfferCodes.AUCTION_BLOCKED);
                                        break;
                                    case "COFR-400005":
                                        statusListener.onError(CreateOfferCodes.EARLY_OFFER);
                                        break;
                                    case "COFR-400006":
                                        statusListener.onError(CreateOfferCodes.MINIMUM_BID_NOT_FULFILLED);
                                        break;
                                    case "COFR-400007":
                                        statusListener.onError(CreateOfferCodes.BASE_PRICE_NOT_FULLFILLED);
                                        break;
                                    case "COFR-400008":
                                        statusListener.onError(CreateOfferCodes.AUCTION_OWNER);
                                        break;
                                    default:
                                        statusListener.onError(CreateOfferCodes.UNKNOWN);
                                        break;
                                }
                            } else {
                                statusListener.onError(CreateOfferCodes.VALIDATION_ERROR);
                            }
                        } catch (IOException ex) {
                            statusListener.onError(CreateOfferCodes.UNKNOWN);
                        }
                    } else {
                        statusListener.onError(CreateOfferCodes.UNKNOWN);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                statusListener.onError(CreateOfferCodes.SERVER_ERROR);
            }
        });
    }
}
