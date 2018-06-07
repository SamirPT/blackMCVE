package tech.peller.mrblackandroidwatch.api.response;


public enum RequestStatus {
    SUCCESS,
    UNAUTHENTICATED,
    CLIENT_ERROR,
    SERVER_ERROR,
    NETWORK_ERROR,
    UNEXPECTED_ERROR;

    public static RequestStatus fromCode(int code) {
        if (code >= 200 && code < 300) {
            return RequestStatus.SUCCESS;
        }
        if (code == 401) {
            return RequestStatus.UNAUTHENTICATED;
        }
        if (code >= 400 && code < 500) {
            return RequestStatus.CLIENT_ERROR;
        }
        //if not return, use general return
        return RequestStatus.SERVER_ERROR;
    }
}
