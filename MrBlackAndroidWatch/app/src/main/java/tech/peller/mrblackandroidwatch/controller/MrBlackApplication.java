package tech.peller.mrblackandroidwatch.controller;

import android.content.Context;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by emil on 25.04.16.
 */
public class MrBlackApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        setupRealm();
        setupPicasso();
    }

    private void setupRealm() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    private void setupPicasso() {
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .followRedirects(true)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .cache(new Cache(getCacheDir(), 30000000))
                .build();

        Picasso.setSingletonInstance(
                new Picasso.Builder(this)
                        .downloader(new OkHttp3Downloader(client))
                        .listener((Picasso picasso, Uri uri, Exception exception) ->
                                Log.d("TAG", "setupPicasso: " + exception.getMessage()))
                        .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
