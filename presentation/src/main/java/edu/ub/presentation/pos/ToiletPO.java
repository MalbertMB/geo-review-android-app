package edu.ub.presentation.pos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import edu.ub.domain.valueobjects.ToiletUid;
import edu.ub.domain.valueobjects.ValorationUid;


/**
 * Data class holding the information of a client
 * outside the application layer.
 */
public class ToiletPO implements Parcelable {

    ToiletUid toiletUid;

    private String clientId;

    private String name;

    private String description;
    private String coord;

    private String img_url;

    private float ratingAverage;

    private int nValoration;

    private Boolean handicap;

    private Boolean free;

    private Boolean baby;

    private Boolean men;

    private Boolean women;

    private Boolean unisex;

    private List<ValorationUid> valorationUidList;

    public ToiletPO() {
        // Default constructor required by ModelMapper
    }

    public ToiletPO(ToiletUid toiletUid, String clientId, String name, String description, String coord, String img_url,float ratingAverage, int nValoration,Boolean handicap, Boolean free, Boolean baby, Boolean men, Boolean women, Boolean unisex, List<ValorationUid> valorationUidList) {
        this.toiletUid = toiletUid;
        this.clientId = clientId;
        this.name = name;
        this.description = description;
        this.coord = coord;
        this.img_url = img_url;
        this.ratingAverage = ratingAverage;
        this.nValoration = nValoration;
        this.handicap = handicap;
        this.free = free;
        this.baby = baby;
        this.men = men;
        this.women = women;
        this.unisex = unisex;
        this.valorationUidList = valorationUidList;
    }

    protected ToiletPO(Parcel in) {
        this.toiletUid = (ToiletUid) in.readSerializable();
        this.clientId = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.coord = in.readString();
        this.img_url = in.readString();
        this.ratingAverage = in.readFloat();
        this.handicap = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.free = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.baby = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.men = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.women = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.unisex = (Boolean) in.readValue(Boolean.class.getClassLoader());

        // Leer lista de ValorationUid (debe ser serializable)
        this.valorationUidList = new ArrayList<>();
        in.readList(this.valorationUidList, ValorationUid.class.getClassLoader());
    }

    public static final Creator<ToiletPO> CREATOR = new Creator<ToiletPO>() {
        @Override
        public ToiletPO createFromParcel(Parcel in) {
            return new ToiletPO(in);
        }

        @Override
        public ToiletPO[] newArray(int size) {
            return new ToiletPO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeSerializable(this.toiletUid);
        dest.writeString(this.clientId);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.coord);
        dest.writeString(this.img_url);
        dest.writeFloat(this.ratingAverage);
        dest.writeInt(this.nValoration);
        dest.writeValue(this.handicap);
        dest.writeValue(this.free);
        dest.writeValue(this.baby);
        dest.writeValue(this.men);
        dest.writeValue(this.women);
        dest.writeValue(this.unisex);

        // Escriure llista
        dest.writeList(this.valorationUidList);
    }

    public ToiletUid getToiletUid() {
        return toiletUid;
    }

    public int getnValoration() {
        return nValoration;
    }

    public void setnValoration(int nValoration) {
        this.nValoration = nValoration;
    }

    public void setToiletUid(ToiletUid toiletUid) {
        this.toiletUid = toiletUid;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoord() {
        return coord;
    }

    public void setCoord(String coord) {
        this.coord = coord;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public float getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(float ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public Boolean getHandicap() {
        return handicap;
    }

    public void setHandicap(Boolean handicap) {
        this.handicap = handicap;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Boolean getBaby() {
        return baby;
    }

    public void setBaby(Boolean baby) {
        this.baby = baby;
    }

    public Boolean getMen() {
        return men;
    }

    public void setMen(Boolean men) {
        this.men = men;
    }

    public Boolean getWomen() {
        return women;
    }

    public void setWomen(Boolean women) {
        this.women = women;
    }

    public Boolean getUnisex() {
        return unisex;
    }

    public void setUnisex(Boolean unisex) {
        this.unisex = unisex;
    }

    public List<ValorationUid> getValorationUidList() {
        return valorationUidList;
    }

    public void setValorationUidList(List<ValorationUid> valorationUidList) {
        this.valorationUidList = valorationUidList;
    }

    public double getLatitude() {
        return Double.parseDouble(coord.split(",")[0].trim());
    }

    public double getLongitude() {
        return Double.parseDouble(coord.split(",")[1].trim());
    }

}
