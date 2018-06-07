package tech.peller.mrblackandroidwatch.helpers;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.inject.Singleton;

import java.lang.reflect.Field;

import io.realm.Realm;
import io.realm.RealmResults;
import tech.peller.mrblackandroidwatch.models.venue.Venue;

/**
 * Created by nikitakulagin on 15.05.16.
 */
@Singleton
public class VenueHelper extends BaseHelper {

    public void saveCurrentVenue(Venue venue) {
        Realm realm = getRealm();
        realm.beginTransaction();
        realm.where(Venue.class).findAll().deleteAllFromRealm();
        realm.copyToRealm(venue);
        realm.commitTransaction();
    }

    public void updateCurrentVenue(Venue venue) {
        Realm realm = getRealm();
        realm.beginTransaction();
        Venue currentVenue = realm.where(Venue.class).findAll().last();
        if (currentVenue == null) {
            realm.copyToRealm(venue);
        } else try {
            for (Field field : Venue.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object newFieldValue = field.get(venue);
                if (field.getType().isPrimitive())
                    throw new IllegalArgumentException(String.format("Field `%s` from class `Venue` must be not primitive type for update", field.getName()));
                if (newFieldValue == null || field.equals(Parcelable.Creator.class))
                    continue;
                field.set(currentVenue, newFieldValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        realm.commitTransaction();
    }

    public void clearVenue() {
        Realm realm = getRealm();
        realm.beginTransaction();
        realm.where(Venue.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    @NonNull
    public Venue getVenue() {
        return getVenue(false);
    }

    @NonNull
    public Venue getVenue(boolean isCopyFromRealm) {
        Realm realm = getRealm();
        RealmResults<Venue> results = realm.where(Venue.class).findAll();
        if (!results.isEmpty()) {
            return isCopyFromRealm ? realm.copyFromRealm(results.last()) : results.last();
        } else {
            return new Venue();
        }
    }
}
