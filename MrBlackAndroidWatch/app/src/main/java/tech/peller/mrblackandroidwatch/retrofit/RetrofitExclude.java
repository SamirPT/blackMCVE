package tech.peller.mrblackandroidwatch.retrofit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Sergey Karleev on 13.04.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RetrofitExclude {
    boolean Serialization() default true;

    boolean Deserialization() default true;
}
