package tech.peller.mrblackandroidwatch.helpers;

import io.realm.Realm;


public class BaseHelper {

    public Realm getRealm() {
        return Realm.getDefaultInstance();
    }
}
