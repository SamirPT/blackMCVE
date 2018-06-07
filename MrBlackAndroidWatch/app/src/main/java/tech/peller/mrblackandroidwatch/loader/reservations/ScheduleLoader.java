package tech.peller.mrblackandroidwatch.loader.reservations;

import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;

import retrofit2.Response;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.services.ScheduleService;
import tech.peller.mrblackandroidwatch.controller.ApplicationContext;
import tech.peller.mrblackandroidwatch.loader.users.BaseLoader;
import tech.peller.mrblackandroidwatch.models.reservation.ScheduleInfo;

public class ScheduleLoader extends BaseLoader {

    @Inject
    private ApplicationContext applicationContext;

    private Long venueId;

    public ScheduleLoader(Context context, Long venueId) {
        super(context);
        this.venueId = venueId;
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        ScheduleService service = applicationContext.getScheduleService();
        Response<ScheduleInfo> messageResponse = service.getUserSchedule(apiKey, venueId).execute();
        return BaseResponse.fromResponse(messageResponse);
    }
}
