package tech.peller.mrblackandroidwatch.helpers;


import android.support.annotation.NonNull;

import com.google.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import tech.peller.mrblackandroidwatch.models.user.UserInfo;

@Singleton
public class UserInfoHelper extends BaseHelper {

    public void saveUserInfoContent(UserInfo response) {
        Realm realm = getRealm();
        realm.beginTransaction();
        realm.where(UserInfo.class).findAll().deleteAllFromRealm();

        realm.copyToRealm(response);
        realm.commitTransaction();
    }

    @NonNull
    public UserInfo getUserInfo() {
        return getUserInfo(false);
    }

    @NonNull
    public UserInfo getUserInfo(boolean isCopyFromRealm) {
        Realm realm = getRealm();
        RealmResults<UserInfo> results = realm.where(UserInfo.class).findAll();
        if (!results.isEmpty()) {
            return isCopyFromRealm ? realm.copyFromRealm(results.last()) : results.last();
        } else {
            return new UserInfo();
        }
    }

    public void executeRealmTransaction(Realm.Transaction transaction) {
        Realm realm = getRealm();
        realm.executeTransaction(transaction);
    }
}
