package tech.peller.mrblackandroidwatch.models.user.facebook;


import io.realm.RealmObject;

public class FbPictureData extends RealmObject {
    private String url;
    private Boolean silhouette;
    private Boolean is_silhouette;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getSilhouette() {
        return silhouette;
    }

    public void setSilhouette(Boolean silhouette) {
        this.silhouette = silhouette;
    }

    public Boolean getIs_silhouette() {
        return is_silhouette;
    }

    public void setIs_silhouette(Boolean is_silhouette) {
        this.is_silhouette = is_silhouette;
    }
}
