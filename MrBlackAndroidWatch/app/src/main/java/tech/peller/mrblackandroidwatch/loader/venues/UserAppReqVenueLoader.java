package tech.peller.mrblackandroidwatch.loader.venues;


import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.services.VenuesService;
import tech.peller.mrblackandroidwatch.controller.ApplicationContext;
import tech.peller.mrblackandroidwatch.loader.users.BaseLoader;
import tech.peller.mrblackandroidwatch.models.venue.Venue;

/**
 * Created by Sam (samir@peller.tech) on 24.01.2017
 */

public class UserAppReqVenueLoader extends BaseLoader {

    @Inject
    private ApplicationContext applicationContext;


    public UserAppReqVenueLoader(Context context) {
        super(context);
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        VenuesService service = applicationContext.getVenuesService();
        Response<List<Venue>> messageResponse = service.venuesListOfAppReqForCurUser(apiKey).execute();
        return BaseResponse.fromResponse(messageResponse);
    }
}
