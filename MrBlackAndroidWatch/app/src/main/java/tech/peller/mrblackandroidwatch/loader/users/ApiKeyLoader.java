package tech.peller.mrblackandroidwatch.loader.users;


import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;

import retrofit2.Response;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.services.UsersService;
import tech.peller.mrblackandroidwatch.controller.ApplicationContext;
import tech.peller.mrblackandroidwatch.helpers.ApiKeyHelper;
import tech.peller.mrblackandroidwatch.models.user.ApiKey;

public class ApiKeyLoader extends BaseLoader {
    @Inject
    private ApiKeyHelper apiKeyHelper;

    @Inject
    private ApplicationContext applicationContext;
    private String phone;
    private String code;
    private Context context;

    public ApiKeyLoader(Context context, String phone, String code) {
        super(context);
        this.phone = phone;
        this.code = code;
        this.context = context;
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        UsersService service = this.applicationContext.getUsersService();
        Response<ApiKey> messageResponse = service.getApiKey(phone, code).execute();
        return BaseResponse.fromResponse(messageResponse);
    }
    @Override
    public void save(Object object){
        ApiKey apiKey = (ApiKey) object;
        if (apiKey != null) {
            apiKeyHelper.saveApiKey(apiKey, context);
        }
    }
}
