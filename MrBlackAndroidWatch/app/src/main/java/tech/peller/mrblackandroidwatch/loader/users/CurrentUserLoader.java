package tech.peller.mrblackandroidwatch.loader.users;


import android.content.Context;

import com.google.inject.Inject;

import java.io.IOException;

import retrofit2.Response;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.services.UsersService;
import tech.peller.mrblackandroidwatch.helpers.UserInfoHelper;
import tech.peller.mrblackandroidwatch.controller.ApplicationContext;
import tech.peller.mrblackandroidwatch.models.user.UserInfo;

public class CurrentUserLoader extends BaseLoader {
    @Inject
    private UserInfoHelper userInfoHelper;
    @Inject
    private ApplicationContext applicationContext;

    public CurrentUserLoader(Context context) {
        super(context);
    }

    @Override
    protected BaseResponse apiCall(String apiKey) throws IOException {
        UsersService service = applicationContext.getUsersService();

//        apiKey = "bd9de3fc-9cbb-4974-aff4-ffbb40efad11";
//        apiKey = "df5f9a08-e52e-4918-9a20-8d7eb89f21d3";

        Response<UserInfo> messageResponse = service.getCurrentUser(apiKey).execute();
        return BaseResponse.fromResponse(messageResponse);
    }
    @Override
    public void save(Object object) {
        UserInfo userInfo = (UserInfo) object;
        if (userInfo != null) {
            userInfoHelper.saveUserInfoContent(userInfo);
        }
    }
}
