package edu.ub.data.dtos.firestore;

public class ValorationFirestoreDto {

    private String uid;
    private String toiletUid;

    private String clientId;

    private int rating;

    private String comment;

    private String date;

    private String imatgeUrl;

    @SuppressWarnings("unused")
    public ValorationFirestoreDto() {}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToiletUid() {
        return toiletUid;
    }

    public void setToiletUid(String toiletUid) {
        this.toiletUid = toiletUid;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientUID(String clientUID) {
        this.clientId = clientUID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImatgeUrl() {
        return imatgeUrl;
    }

    public void setImatgeUrl(String imatgeUrl) {
        this.imatgeUrl = imatgeUrl;
    }
}
