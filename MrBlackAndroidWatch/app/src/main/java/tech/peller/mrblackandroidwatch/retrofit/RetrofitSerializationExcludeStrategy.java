package tech.peller.mrblackandroidwatch.retrofit;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Created by Sergey Karleev on 13.04.2017.
 */

public class RetrofitSerializationExcludeStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        RetrofitExclude annotation = f.getAnnotation(RetrofitExclude.class);
        return annotation != null && annotation.Serialization();
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
