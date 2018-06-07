package tech.peller.mrblackandroidwatch.models.reservation;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

/**
 * Created by Sam (samir@peller.tech) on 02.08.2017
 */

public class SignatureTO extends RealmObject implements Parcelable {
    public static final Creator<SignatureTO> CREATOR = new Creator<SignatureTO>() {
        @Override
        public SignatureTO createFromParcel(Parcel in) {
            return new SignatureTO(in);
        }

        @Override
        public SignatureTO[] newArray(int size) {
            return new SignatureTO[size];
        }
    };
    private String url;
    private int bottleMin;
    private int minSpend;
    private String fullName;

    public SignatureTO() {
    }

    protected SignatureTO(Parcel in) {
        url = in.readString();
        bottleMin = in.readInt();
        minSpend = in.readInt();
        fullName = in.readString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getBottleMin() {
        return bottleMin;
    }

    public void setBottleMin(Integer bottleMin) {
        this.bottleMin = bottleMin;
    }

    public Integer getMinSpend() {
        return minSpend;
    }

    public void setMinSpend(Integer minSpend) {
        this.minSpend = minSpend;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(bottleMin);
        dest.writeInt(minSpend);
        dest.writeString(fullName);
    }
}
