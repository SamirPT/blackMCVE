package tech.peller.mrblackandroidwatch.controller;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import roboguice.inject.RoboApplicationProvider;
import tech.peller.mrblackandroidwatch.BuildConfig;
import tech.peller.mrblackandroidwatch.api.services.EventsService;
import tech.peller.mrblackandroidwatch.api.services.LogService;
import tech.peller.mrblackandroidwatch.api.services.ReservationsService;
import tech.peller.mrblackandroidwatch.api.services.ScheduleService;
import tech.peller.mrblackandroidwatch.api.services.UsersService;
import tech.peller.mrblackandroidwatch.api.services.VenuesService;
import tech.peller.mrblackandroidwatch.models.RealmString;
import tech.peller.mrblackandroidwatch.retrofit.RetrofitDeserializationExcludeStrategy;
import tech.peller.mrblackandroidwatch.retrofit.RetrofitSerializationExcludeStrategy;


public class ApplicationContext extends RoboApplicationProvider<MrBlackApplication> {
    private static final int TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 120;
    private static final int CONNECT_TIMEOUT = 30;

    @Singleton
    public Realm provideRealm() {
        return Realm.getDefaultInstance();
    }

    @Singleton
    private Retrofit getRetrofit() {
        OkHttpClient CLIENT = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();

        GsonBuilder gsonBuilder = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .addSerializationExclusionStrategy(new RetrofitSerializationExcludeStrategy())
                .addDeserializationExclusionStrategy(new RetrofitDeserializationExcludeStrategy())
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new DateTime(json.getAsJsonPrimitive().getAsLong(), DateTimeZone.getDefault()).toDate();
                    }
                }).registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                    @Override
                    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.getTime());
                    }
                }).registerTypeAdapter(new TypeToken<RealmList<RealmString>>() {
                        }.getType(),
                        new RelamStringRealmListConverter());

        /*TODO: раскомментировать, если требуется подключить валидатор полей
            if (BuildConfig.DEBUG)
            gsonBuilder.registerTypeAdapterFactory(new ValidatorAdapterFactory());*/

        Gson GSON_CLIENT = gsonBuilder
                .create();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_SERVER)
                .addConverterFactory(GsonConverterFactory.create(GSON_CLIENT))
                .client(CLIENT)
                .build();
    }

    @Singleton
    public UsersService getUsersService() {
        return getRetrofit().create(UsersService.class);
    }

    @Singleton
    public VenuesService getVenuesService() {
        return getRetrofit().create(VenuesService.class);
    }

    public EventsService getEventsService() {
        return getRetrofit().create(EventsService.class);
    }

    public ScheduleService getScheduleService() {
        return getRetrofit().create(ScheduleService.class);
    }

    public ReservationsService getResService() {
        return getRetrofit().create(ReservationsService.class);
    }

    public LogService getLogService() {
        return getRetrofit().create(LogService.class);
    }
}
