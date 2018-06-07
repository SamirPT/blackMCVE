package tech.peller.mrblackandroidwatch.api.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tech.peller.mrblackandroidwatch.models.event.EventsList;
import tech.peller.mrblackandroidwatch.models.log.LogTO;

/**
 * Created by Sam (salyasov@gmail.com) on 31.05.2018
 */
public interface LogService {
    @POST("/log")
    Call<EventsList> postLog(@Body LogTO logTO);
}
