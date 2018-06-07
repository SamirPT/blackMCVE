package tech.peller.mrblackandroidwatch.models.venue;

import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.services.VenuesService;
import tech.peller.mrblackandroidwatch.controller.ApplicationContext;
import tech.peller.mrblackandroidwatch.loader.users.BaseLoader;

/**
 * Created by Sam (samir@peller.tech) on 16.02.2017
 */

public class GetNotificationsMessagesLoader extends BaseLoader {
    @Inject
    private ApplicationContext applicationContext;
    private Long venueId;
    private Integer pageIndex;
    private Integer pageSize;

    public GetNotificationsMessagesLoader(Context context, Long venueId, Integer pageIndex, Integer pageSize) {
        super(context);
        this.venueId = venueId;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        VenuesService service = applicationContext.getVenuesService();
        Response<List<NotificationTO>> messageResponse = service.getNotificationsMessages(venueId,
                apiKey, pageIndex, pageSize).execute();
        return BaseResponse.fromResponse(messageResponse);
    }
}
