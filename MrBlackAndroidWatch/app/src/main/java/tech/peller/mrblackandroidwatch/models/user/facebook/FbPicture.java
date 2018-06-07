package tech.peller.mrblackandroidwatch.models.user.facebook;


import io.realm.RealmObject;

public class FbPicture extends RealmObject {
    private FbPictureData data;

    public FbPictureData getData() {
        return data;
    }

    public void setData(FbPictureData data) {
        this.data = data;
    }
}
