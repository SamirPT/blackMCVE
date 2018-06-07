package tech.peller.mrblackandroidwatch.api.response;

/**
 * Created by crashkin aka Boris T.
 * on 13.04.16 9:11
 */
public class ErrorResponse extends BaseResponse<ErrorMessage> {

    protected ErrorResponse(RequestStatus status, ErrorMessage obj) {
        super(status, obj);
    }

}