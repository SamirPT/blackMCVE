package tech.peller.mrblackandroidwatch.helpers;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.inject.Singleton;

import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.models.user.ApiKey;

@Singleton
public class ApiKeyHelper extends BaseHelper {
    public void saveApiKey(@NonNull ApiKey apiKey, @NonNull Context context) {
        if (!(context instanceof Activity))
            return;

        SharedPreferences sharedPref = ((Activity) context).getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.pref_api_key), apiKey.getApiKey());
        editor.apply();
    }

    public void clearApiKey(@NonNull Context context) {
        if (!(context instanceof Activity))
            return;

        SharedPreferences sharedPref = ((Activity) context).getPreferences(Context.MODE_PRIVATE);

        if (sharedPref.contains(context.getString(R.string.pref_api_key))) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(context.getString(R.string.pref_api_key));
            editor.apply();
        }
    }

    @NonNull
    public ApiKey getApiKey(Context context) {
        if (!(context instanceof Activity))
            return new ApiKey("");

        SharedPreferences sharedPref = ((Activity) context).getPreferences(Context.MODE_PRIVATE);

        return new ApiKey(sharedPref.getString(context.getString(R.string.pref_api_key), ""));
    }
}