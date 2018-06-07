package tech.peller.mrblackandroidwatch.api.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tech.peller.mrblackandroidwatch.models.event.EventsList;

/**
 * Created by Sam (salyasov@gmail.com) on 13.04.2018
 */
public interface EventsService {
    @GET("/api/v1/events")
    Call<EventsList> getEvents(@Query("api_key") String apiKey,
                               @Query("date") String date,
                               @Query("venueId") Long venueId);
}
