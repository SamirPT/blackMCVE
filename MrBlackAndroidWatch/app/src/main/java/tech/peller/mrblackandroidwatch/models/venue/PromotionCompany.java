package tech.peller.mrblackandroidwatch.models.venue;


import io.realm.RealmObject;

public class PromotionCompany extends RealmObject {

    private int id;
    private String name;

    public PromotionCompany() {
    }

    public PromotionCompany(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
