package tech.peller.mrblackandroidwatch.loader.reservations;

import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;

import retrofit2.Response;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.response.ResponseMessage;
import tech.peller.mrblackandroidwatch.api.services.ReservationsService;
import tech.peller.mrblackandroidwatch.controller.ApplicationContext;
import tech.peller.mrblackandroidwatch.loader.users.BaseLoader;

/**
 * Created by Sam (salyasov@gmail.com) on 07.01.2018
 */

public class UpdateLiveSpendLoader extends BaseLoader {
    @Inject
    ApplicationContext applicationContext;

    private Long reservationId;
    private Integer value;

    public UpdateLiveSpendLoader(Context context, long reservationId,
                                 Integer value) {
        super(context);
        this.reservationId = reservationId;
        this.value = value;
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        ReservationsService service = applicationContext.getResService();
        Response<ResponseMessage> messageResponse = service.updateLiveSpendValue(reservationId,
                apiKey, value).execute();
        return BaseResponse.fromResponse(messageResponse);
    }
}
