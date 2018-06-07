package tech.peller.mrblackandroidwatch.api.response;

/**
 * Created by crashkin aka Boris T.
 * on 13.04.16 9:15
 */
public class SuccessResponse<T> extends BaseResponse<T> {

    protected SuccessResponse(RequestStatus status, T obj) {
        super(status, obj);
    }

}
