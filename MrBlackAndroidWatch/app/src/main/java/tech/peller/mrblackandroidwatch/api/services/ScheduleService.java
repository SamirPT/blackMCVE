package tech.peller.mrblackandroidwatch.api.services;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tech.peller.mrblackandroidwatch.models.reservation.ScheduleInfo;

public interface ScheduleService {
    @GET("/api/v1/schedule")
    Call<ScheduleInfo> getUserSchedule(@Query("api_key") String apiKey, @Query("venueId") Long venueId);
}
