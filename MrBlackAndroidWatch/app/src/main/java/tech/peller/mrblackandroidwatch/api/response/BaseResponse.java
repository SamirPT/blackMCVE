package tech.peller.mrblackandroidwatch.api.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Response;

public abstract class BaseResponse<T> {

    @Nullable
    private final T obj;
    private final RequestStatus status;

    protected BaseResponse(RequestStatus status, T obj) {
        this.obj = obj;
        this.status = status;
    }

    public static BaseResponse fromResponse(Response r) throws IOException {
        RequestStatus status = RequestStatus.fromCode(r.code());
        if (status == RequestStatus.SUCCESS) {
            return new SuccessResponse(status, r.body());
        } else {
            String error = r.errorBody().string();
            ErrorMessage errorMessage = new Gson().fromJson(error, ErrorMessage.class);
            return new ErrorResponse(status, errorMessage);
        }
    }

    public static BaseResponse fromThrowable(Throwable t) {
        RequestStatus status = RequestStatus.UNEXPECTED_ERROR;
        if (t instanceof IOException) {
            status = RequestStatus.NETWORK_ERROR;
        }

        //кустарненько собираем мессагу
        ErrorMessage message = new ErrorMessage();
        message.setError(ExceptionUtil.causeList(t));

        return new ErrorResponse(status, message);
    }

    public boolean isSuccess() {
        return status == RequestStatus.SUCCESS;
    }

    public SuccessResponse asSuccess() {
        if (!isSuccess()) {
            throw new IllegalStateException("ErrorResponse can't cast to SuccessResponse");
        }
        return (SuccessResponse) this;

    }

    public ErrorResponse asError() {
        if (isSuccess()) {
            throw new IllegalStateException("SuccessResponse can't cast to ErrorResponse");
        }
        return (ErrorResponse) this;
    }

    @NonNull
    public RequestStatus getRequestResult() {
        return status;
    }

    @Nullable
    public T getObj() {
        return obj;
    }

    public RequestStatus getStatus() {
        return status;
    }


}