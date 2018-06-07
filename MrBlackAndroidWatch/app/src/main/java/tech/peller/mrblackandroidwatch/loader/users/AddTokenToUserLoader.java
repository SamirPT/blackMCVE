package tech.peller.mrblackandroidwatch.loader.users;

import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;

import retrofit2.Response;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.response.ResponseMessage;
import tech.peller.mrblackandroidwatch.api.services.UsersService;
import tech.peller.mrblackandroidwatch.controller.ApplicationContext;

public class AddTokenToUserLoader extends BaseLoader {
    @Inject
    ApplicationContext applicationContext;

    private String token;
    private Boolean isTest;

    public AddTokenToUserLoader(Context context, String token, Boolean isTest) {
        super(context);
        this.token = token;
        this.isTest = isTest;
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        UsersService service = applicationContext.getUsersService();
        String tokenType = "ANDROID_WATCH";
        String constantApiKey = "61125926-f8bb-4b5e-9155-b6c4a42c04f1";
        Response<ResponseMessage> messageResponse = service.addTokenToUserNoId(constantApiKey, token,
                tokenType, isTest).execute();
        return BaseResponse.fromResponse(messageResponse);
    }
}