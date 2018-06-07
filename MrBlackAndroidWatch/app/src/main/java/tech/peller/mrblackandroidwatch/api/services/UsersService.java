package tech.peller.mrblackandroidwatch.api.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import tech.peller.mrblackandroidwatch.api.response.ResponseMessage;
import tech.peller.mrblackandroidwatch.models.user.ApiKey;
import tech.peller.mrblackandroidwatch.models.user.UserInfo;

public interface UsersService {
    @GET("/api/v1/user")
    Call<UserInfo> getCurrentUser(@Query("api_key") String apiKey);

    @PUT("/api/v1/user/token")
    Call<ResponseMessage> addTokenToUserNoId(@Query("api_key") String apiKey,
                                             @Query("token") String token, @Query("token_type") String tokenType,
                                             @Query("test") Boolean isTest);

    @GET("/api/v1/user/code")
    Call<ResponseMessage> getSecurityCode(@Query("phoneNumber") String phone);

    @GET("/api/v1/user/api_key")
    Call<ApiKey> getApiKey(@Query("phoneNumber") String phoneNumber,
                           @Query("securityCode") String securityCode);
}
