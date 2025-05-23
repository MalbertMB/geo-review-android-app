package edu.ub.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ToiletUid implements Serializable {

    private String uid;

    private ToiletUid(String uid) {
        if(uid == null || uid.isBlank()) {
            throw new IllegalArgumentException("UID no pot ser nul o buit");
        }
        this.uid = uid;
    }

    /*Unused*/
    public ToiletUid() {}

    //Convertir string a UID
    public static ToiletUid fromString(String uid){
        return new ToiletUid(uid);
    }

    //Creador del UID
    public static ToiletUid createUID(){
        return new ToiletUid(UUID.randomUUID().toString());
    }

    public String getUid() {
        return this.uid;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ToiletUid productId = (ToiletUid) obj;
        return Objects.equals(uid, productId.uid); // Use Objects.equals for null safety
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid); // Use Objects.hash to generate hash code based on id
    }

    @Override
    public String toString() {
        return uid.toString();
    }

    //Molt necessari quan creem el labavo l'hem de creat amb l'ID buida i quan la bd li assigna li fiquem
    //a l'objecte UId

    public void setUid(String uid) {
        this.uid = uid;
    }
}
