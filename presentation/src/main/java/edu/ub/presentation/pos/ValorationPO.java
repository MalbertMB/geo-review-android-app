package edu.ub.presentation.pos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;

import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.Comment;
import edu.ub.domain.valueobjects.Rating;
import edu.ub.domain.valueobjects.ToiletUid;
import edu.ub.domain.valueobjects.ValorationUid;

public class ValorationPO implements Parcelable {

    private ValorationUid uid;

    private ToiletUid toiletUid;

    private ClientId clientId;

    private float rating;

    private Comment comment;

    private String img_Url;

    private LocalDateTime date;

    public ValorationPO() {
        // Default constructor required by ModelMapper
    }

    public ValorationPO(ValorationUid uid, ToiletUid toiletUid, ClientId clientId, float rating, Comment comment, String img_Url, LocalDateTime date) {
        this.uid = uid;
        this.toiletUid = toiletUid;
        this.clientId = clientId;
        this.rating = rating;
        this.comment = comment;
        this.img_Url = img_Url;
        this.date = date;
    }

    protected ValorationPO(Parcel in) {
        img_Url = in.readString();
    }

    public static final Creator<ValorationPO> CREATOR = new Creator<ValorationPO>() {
        @Override
        public ValorationPO createFromParcel(Parcel in) {
            return new ValorationPO(in);
        }

        @Override
        public ValorationPO[] newArray(int size) {
            return new ValorationPO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(img_Url);
    }

    public ValorationUid getUid() {
        return uid;
    }

    public ToiletUid getToiletUid() {
        return toiletUid;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public float getRating() {
        return rating;
    }

    public Comment getComment() {
        return comment;
    }

    public String getImg_Url() {
        return img_Url;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
