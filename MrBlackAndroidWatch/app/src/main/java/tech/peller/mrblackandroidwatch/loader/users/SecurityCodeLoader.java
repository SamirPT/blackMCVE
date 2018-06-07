package tech.peller.mrblackandroidwatch.loader.users;

import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;

import retrofit2.Response;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.response.ResponseMessage;
import tech.peller.mrblackandroidwatch.api.services.UsersService;
import tech.peller.mrblackandroidwatch.controller.ApplicationContext;

public class SecurityCodeLoader extends BaseLoader {
    @Inject
    private ApplicationContext applicationContext;

    private String phoneNumber;
    private Response<ResponseMessage> messageResponse;

    public SecurityCodeLoader(Context context, String phone) {
        super(context);
        phoneNumber = phone;
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        if (messageResponse == null) {
            UsersService service = applicationContext.getUsersService();
            messageResponse = service.getSecurityCode(phoneNumber).execute();
        }
        return BaseResponse.fromResponse(messageResponse);
    }
}
