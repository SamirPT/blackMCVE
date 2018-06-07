package tech.peller.mrblackandroidwatch.api.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tech.peller.mrblackandroidwatch.api.response.ResponseMessage;
import tech.peller.mrblackandroidwatch.loader.reservations.LiveSpendTO;

/**
 * Created by Sam (salyasov@gmail.com) on 19.04.2018
 */
public interface ReservationsService {
    @PUT("/api/v1/reservation/{id}/state")
    Call<ResponseMessage> changeReservationState(@Path("id") Long id,
                                                 @Query("api_key") String apiKey,
                                                 @Query("newState") String newState,
                                                 @Query("message") String cancellationMessage);

    @PUT("/api/v1/reservation/{id}/liveSpend")
    Call<ResponseMessage> updateLiveSpendValue(@Path("id") Long id,
                                               @Query("api_key") String apiKey,
                                               @Query("value") Integer value);

    @GET("/api/v1/reservation/{id}/liveSpends")
    Call<List<LiveSpendTO>> getLiveSpendList(@Path("id") Long id,
                                             @Query("api_key") String apiKey);
}
