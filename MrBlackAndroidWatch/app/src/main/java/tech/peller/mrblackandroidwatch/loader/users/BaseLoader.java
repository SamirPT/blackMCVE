package tech.peller.mrblackandroidwatch.loader.users;


import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.response.RequestStatus;
import tech.peller.mrblackandroidwatch.helpers.ApiKeyHelper;
import tech.peller.mrblackandroidwatch.controller.RoboAsyncTaskLoader;
import tech.peller.mrblackandroidwatch.models.user.ApiKey;

public abstract class BaseLoader extends RoboAsyncTaskLoader<BaseResponse> {

    @Inject
    private ApiKeyHelper apiKeyHelper;
    private ApiKey apiKey;

    public BaseLoader(Context context) {
        super(context);
        String simpleName = this.getClass().getSimpleName();
        apiKey = apiKeyHelper.getApiKey(context);
    }

    protected String getApiKey() {
        return StringUtils.defaultString(apiKey.getApiKey());
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public BaseResponse loadInBackground() {
        try {
            BaseResponse baseResponse = apiCall(getApiKey());
            if (baseResponse.getRequestResult() == RequestStatus.SUCCESS) {
                save(baseResponse.getObj());
                onSuccess();
            } else {
                onError();
            }
            return baseResponse;
        } catch (Throwable t) {
            return BaseResponse.fromThrowable(t);
        }
    }

    protected void onSuccess() {
        Log.i("BaseLoader", "responseCursor non empty");
    }

    protected void onError() {
        Log.e("BaseLoader", "responseCursor is empty");
    }

    protected abstract BaseResponse apiCall(String apiKey) throws IOException;

    protected void save(Object object) {

    }
}