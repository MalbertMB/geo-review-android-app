package edu.ub.domain.model.entities;

import java.util.Map;

import edu.ub.domain.valueobjects.ClientId;

/**
 * Domain entity holding the data and the behavior of a client.
 */
public class Client {

  /* Attributes */
  private final ClientId id;
  private final String email;
  private final String password;
  private final String photoUrl;


  /**
   * Constructor.
   * @param id The id of the client.
   * @param email The email of the client.
   * @param password The password of the client.
   * @param photoUrl The photo URL of the client.
   */
  public Client(ClientId id, String email, String password, String photoUrl) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.photoUrl = photoUrl;
  }

  /**
   * Empty constructor.
   */
  @SuppressWarnings("unused")
  public Client(Client client) {
    this(client.id, client.email, client.password, client.photoUrl);
  }

  /**
   * Empty constructor.
   */
  @SuppressWarnings("unused")
  public Client() {
    this(null, null, null, null);
  }

  /**
   * Gets the id of the client.
   * @return The id of the client.
   */
  public ClientId getId() {
    return id;
  }

  /**
   * Gets the username of the client.
   * @return The username of the client.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Gets the password of the client.
   * @return The password of the client.
   */
  public String getPassword() {
    return password;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Client client = (Client) obj;
    return id.equals(client.id);
  }

  public String getImatgeUrl() {
    return photoUrl;
  }

}
