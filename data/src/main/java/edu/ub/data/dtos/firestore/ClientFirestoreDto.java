package edu.ub.data.dtos.firestore;

import com.google.firebase.firestore.DocumentId;

/**
 * Domain entity holding the data and the behavior of a client.
 */
public class ClientFirestoreDto {
  /* Attributes */
  @DocumentId
  private String id;
  private String email;
  private String password;
  private String photoUrl;

  /**
   * Empty constructor.
   */
  @SuppressWarnings("unused")
  public ClientFirestoreDto() { }

  /**
   * Gets the id of the client.
   * @return The id of the client.
   */
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the username of the client.
   * @return The username of the client.
   */
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the password of the client.
   * @return The password of the client.
   */
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }
  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

}
