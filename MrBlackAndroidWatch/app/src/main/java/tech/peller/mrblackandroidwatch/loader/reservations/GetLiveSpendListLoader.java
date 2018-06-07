package tech.peller.mrblackandroidwatch.loader.reservations;

import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.services.ReservationsService;
import tech.peller.mrblackandroidwatch.controller.ApplicationContext;
import tech.peller.mrblackandroidwatch.loader.users.BaseLoader;

/**
 * Created by Sam (salyasov@gmail.com) on 07.01.2018
 */

public class GetLiveSpendListLoader extends BaseLoader {
    @Inject
    ApplicationContext applicationContext;

    private Long reservationId;

    public GetLiveSpendListLoader(Context context, long reservationId) {
        super(context);
        this.reservationId = reservationId;
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        ReservationsService service = applicationContext.getResService();
        Response<List<LiveSpendTO>> messageResponse = service.getLiveSpendList(reservationId,
                apiKey).execute();
        return BaseResponse.fromResponse(messageResponse);
    }
}
