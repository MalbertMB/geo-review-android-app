package edu.ub.domain.model.entities;

import java.time.LocalDateTime;

import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.Comment;
import edu.ub.domain.valueobjects.Rating;
import edu.ub.domain.valueobjects.ToiletUid;
import edu.ub.domain.valueobjects.ValorationUid;

public class Valoration {

    private ValorationUid uid;

    private ToiletUid toiletUid;

    private ClientId clientId;

    private Rating rating;

    private Comment comment;

    private String imatgeUrl;

    private LocalDateTime date;

    public Valoration(ValorationUid uid, ToiletUid toiletUid, ClientId clientId, Rating rating, Comment comment) {
        this.uid = uid;
        this.toiletUid = toiletUid;
        this.clientId = clientId;
        this.rating = rating;
        this.comment = comment;
        this.date = LocalDateTime.now();
        this.imatgeUrl = "default";
    }

    public Valoration(ValorationUid uid, ToiletUid toiletUid, ClientId clientId, Rating rating, Comment comment, String imatgeUrl) {
        this.uid = uid;
        this.toiletUid = toiletUid;
        this.clientId = clientId;
        this.rating = rating;
        this.comment = comment;
        this.date = LocalDateTime.now();
        this.imatgeUrl = imatgeUrl;
    }

    public Valoration(ValorationUid uid, ToiletUid toiletUid, ClientId clientId, Rating rating, Comment comment, String date, String imatgeUrl) {
        this.uid = uid;
        this.toiletUid = toiletUid;
        this.clientId = clientId;
        this.rating = rating;
        this.comment = comment;
        this.date = LocalDateTime.parse(date);
        this.imatgeUrl = imatgeUrl;
    }

    public Valoration(){
        this.uid = null;
        this.toiletUid = null;
        this.clientId = null;
        this.rating = null;
        this.comment = null;
        this.date = null;
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

    public Rating getRating() {
        return rating;
    }

    public Comment getComment() {
        return comment;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setUid(ValorationUid uid) {
        this.uid = uid;
    }

    public void setToiletUid(ToiletUid toiletUid) {
        this.toiletUid = toiletUid;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getImatgeUrl() {
        return imatgeUrl;
    }

    public void setImatgeUrl(String imatgeUrl) {
        this.imatgeUrl = imatgeUrl;
    }
}
