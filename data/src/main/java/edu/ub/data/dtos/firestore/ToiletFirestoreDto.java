package edu.ub.data.dtos.firestore;

import java.util.List;

public class ToiletFirestoreDto {

    private String uid;

    private String clientId;
    private String name;
    private String description;
    private String coord;
    private String img_url;

    private float ratingAverage;

    private int nValoration;

    private List<String> valorationUids;

    private boolean men;
    private boolean women;
    private boolean unisex;
    private boolean handicap;
    private boolean free;
    private boolean baby;


     /* Empty constructor required for Firestore.*/

    @SuppressWarnings("unused")
    public ToiletFirestoreDto() { }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public boolean isMen() {
        return men;
    }

    public void setMen(boolean men) {
        this.men = men;
    }

    public boolean isWomen() {
        return women;
    }

    public void setWomen(boolean women) {
        this.women = women;
    }

    public boolean isUnisex() {
        return unisex;
    }

    public void setUnisex(boolean unisex) {
        this.unisex = unisex;
    }

    public boolean isHandicap() {
        return handicap;
    }

    public void setHandicap(boolean handicap) {
        this.handicap = handicap;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public boolean isBaby() {
        return baby;
    }

    public void setBaby(boolean baby) {
        this.baby = baby;
    }

    public List<String> getValorationUids() {
        return valorationUids;
    }

    public void setValorationUids(List<String> valorationUids) {
        this.valorationUids = valorationUids;
    }

    public float getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(float ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public int getnValoration() {
        return nValoration;
    }

    public void setnValoration(int nValoration) {
        this.nValoration = nValoration;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

