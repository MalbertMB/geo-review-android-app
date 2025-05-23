package edu.ub.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public class ValorationUid {

    private String UID;
    public ValorationUid(String UID) {
        this.UID = UID;
    }

    // Obligatorio para Firestore
    @SuppressWarnings("unused")
    public ValorationUid() {}
    //Factory method per crear nous UIDs
    public static ValorationUid createUID() {
        return new ValorationUid(UUID.randomUUID().toString());
    }

    //Factory method per reconstrucció desde persistència
    public static ValorationUid fromString(String UID) {
        return new ValorationUid(UID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValorationUid)) return false;
        return Objects.equals(UID, ((ValorationUid) o).UID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UID);
    }

    public String getUID() {
        return UID;
    }
}
