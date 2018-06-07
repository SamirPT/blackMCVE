package tech.peller.mrblackandroidwatch.models;

import io.realm.RealmObject;

public class RealmString extends RealmObject {

    private String string;

    public RealmString(String string) {
        this.string = string;
    }

    public RealmString() {
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}