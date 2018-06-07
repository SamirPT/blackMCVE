package tech.peller.mrblackandroidwatch.api.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tech.peller.mrblackandroidwatch.models.venue.NotificationTO;
import tech.peller.mrblackandroidwatch.models.venue.Venue;

public interface VenuesService {
    //Venues list of approved and requested venues for current user
    @GET("api/v1/venues/my2")
    Call<List<Venue>> venuesListOfAppReqForCurUser(@Query("api_key") String apiKey);

    @GET("/api/v1/venue/{id}/feed")
    Call<List<NotificationTO>> getNotificationsMessages(@Path("id") Long id,
                                                        @Query("api_key") String apiKey,
                                                        @Query("pageIndex") Integer pageIndex,
                                                        @Query("pageSize") Integer pageSize);
}
