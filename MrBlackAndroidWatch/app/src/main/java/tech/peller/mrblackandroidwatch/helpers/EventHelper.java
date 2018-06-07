package tech.peller.mrblackandroidwatch.helpers;

import android.support.annotation.NonNull;

import com.google.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import tech.peller.mrblackandroidwatch.models.event.EventInfo;

/**
 * Created by s.karleev on 15.05.16.
 */
@Singleton
public class EventHelper extends BaseHelper {

    public void saveCurrentEvent(EventInfo event) {
        if (event.getCurrentDate() == null || event.getCurrentDate().isEmpty())
            throw new IllegalArgumentException("before save EventInfo must have non-empty field currentDate");
        Realm realm = getRealm();
        realm.beginTransaction();
        realm.where(EventInfo.class).findAll().deleteAllFromRealm();
        realm.copyToRealm(event);
        realm.commitTransaction();
    }

    public void clearEvent() {
        Realm realm = getRealm();
        realm.beginTransaction();
        realm.where(EventInfo.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    @NonNull
    public EventInfo getCurrentEvent() {
        return getCurrentEvent(false);
    }

    @NonNull
    public EventInfo getCurrentEvent(boolean isCopyFromRealm) {
        Realm realm = getRealm();
        RealmResults<EventInfo> results = realm.where(EventInfo.class).findAll();
        if (!results.isEmpty()) {
            return isCopyFromRealm ? realm.copyFromRealm(results.last()) : results.last();
        } else {
            return new EventInfo();
        }
    }
}
