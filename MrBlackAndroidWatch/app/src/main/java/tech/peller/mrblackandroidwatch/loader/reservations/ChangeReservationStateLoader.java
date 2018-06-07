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
 * Created by Sam (samir@peller.tech) on 11.07.2016.
 */
public class ChangeReservationStateLoader extends BaseLoader {
    @Inject
    private ApplicationContext applicationContext;

    private long reservationId;
    private String newState;
    private String cancellationMessage;

    public ChangeReservationStateLoader(Context context, long reservationId, String newState,
                                        String cancellationMessage) {
        super(context);

        this.reservationId = reservationId;
        this.newState = newState;
        this.cancellationMessage = cancellationMessage;
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        ReservationsService service = applicationContext.getResService();
        Response<ResponseMessage> messageResponse = service.changeReservationState(reservationId,
                getApiKey(), newState, cancellationMessage).execute();
        return BaseResponse.fromResponse(messageResponse);
    }
}
