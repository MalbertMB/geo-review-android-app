package edu.ub.domain.model.entities;
import java.io.Serializable;
import java.util.List;


import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.ToiletUid;
import edu.ub.domain.valueobjects.ValorationUid;

public class Toilet implements Serializable {

    private ToiletUid uid;

    private ClientId clientId;
    private String name;
    private String description;
    private String coord;
    private String img_url;

    private float ratingAverage;

    private int nValoration;
    private Boolean noLegs;
    private Boolean free;
    private Boolean baby;
    private Boolean men;
    private Boolean women;
    private Boolean unisex;
    private List<ValorationUid> valorationUIDs;

    public Toilet() {}

    //Constructor amb valoració inicial
    public Toilet(ToiletUid uid, ClientId clientId, String name, String description, String coord, String img_url, float ratingAverage, int nValoration, boolean men, boolean women, boolean unisex, boolean handicap, boolean free, boolean baby, List<ValorationUid> valorationUIDs){
        this.uid = uid;
        this.clientId = clientId;
        this.name = name;
        this.description = description;
        this.coord = coord;
        this.noLegs = handicap;
        this.free = free;
        this.baby = baby;
        this.men = men;
        this.women = women;
        this.unisex = unisex;
        this.ratingAverage = ratingAverage;
        this.img_url = img_url;
        this.nValoration = nValoration;
        this.valorationUIDs = valorationUIDs;
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
    public double getLatitude() {
        return Double.parseDouble(coord.split(",")[0].trim());
    }

    public double getLongitude() {
        return Double.parseDouble(coord.split(",")[1].trim());
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

    public ToiletUid getUid() {
        return uid;
    }

    public void setUid(ToiletUid uid) {
        this.uid = uid;
    }

    public List<ValorationUid> getValorationUid() {
        return valorationUIDs;
    }

    public void setValorationUid(List<ValorationUid> valorationUIDs) {
        this.valorationUIDs = valorationUIDs;
    }

    public void addValorationUid(ValorationUid uid){
        valorationUIDs.add(uid);
    }

    public Boolean getNoLegs() {
        return noLegs;
    }

    public void setNoLegs(Boolean noLegs) {
        this.noLegs = noLegs;
    }

    public Boolean getMen(){return men;}

    public Boolean getWomen(){return women;}

    public Boolean getUnisex(){return unisex;}

    public Boolean getBaby() {
        return baby;
    }

    public void setBaby(Boolean baby) {
        this.baby = baby;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public float getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(float ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public void setValorationUIDs(List<ValorationUid> valorationUIDs) {
        this.valorationUIDs = valorationUIDs;
    }

    public int getnValoration() {
        return nValoration;
    }

    public void setnValoration(int nValoration) {
        this.nValoration = nValoration;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Toilet product = (Toilet) obj;
        return uid.equals(product.uid);
    }

    public List<ValorationUid> getValorationUIDs() {
        return valorationUIDs;
    }

    public void calculateAverageValoration(int new_val) {
        float newValoration = (ratingAverage*nValoration + new_val)/(nValoration+1);
        setRatingAverage(newValoration);
        this.nValoration++;
    }

}
