package edu.ub.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ClientId implements Serializable {
  private String id;

  public ClientId(String id) {
    this.id = id;
  }

  @SuppressWarnings("unused")
  public ClientId() {}

  public String getId() {
    return id;
  }

  public static ClientId fromString(String uid){
    return new ClientId(uid);
  }

  //Creador del UID
  public static ClientId createUID(){
    return new ClientId(UUID.randomUUID().toString());
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ClientId clientId = (ClientId) obj;
    return Objects.equals(id, clientId.id); // Use Objects.equals for null safety
  }

  @Override
  public int hashCode() {
    return Objects.hash(id); // Use Objects.hash to generate hash code based on id
  }

  @Override
  public String toString() {
    return id;
  }
}
