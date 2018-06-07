package tech.peller.mrblackandroidwatch.models.venue;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Sam (samir@peller.tech) on 10.04.2017
 */

public class ImageWithTitle extends RealmObject implements Serializable {
    private String id;
    private String title;
    private String url;
    private boolean selected;

    public ImageWithTitle() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
