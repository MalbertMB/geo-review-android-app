package edu.ub.presentation.pos;

import android.os.Parcel;
import android.os.Parcelable;

import edu.ub.domain.valueobjects.ClientId;


/**
 * Data class holding the information of a client
 * outside the application layer.
 */
public class ClientPO implements Parcelable {
  /* Attributes */
  private ClientId id;
  private String email;
  private String photoUrl;
  private String password;

  /* Constructors */
  public ClientPO(ClientId id, String email, String password, String photoUrl) {
    this.id = id;
    this.email = email;
    this.photoUrl = photoUrl;
    this.password = password;
  }

  @SuppressWarnings("unused")
  public ClientPO() {
  }

  /* Setters */
  public ClientId getId() { return id; }
  public String getEmail() { return email; }
  public String getPhotoUrl() { return photoUrl; }

  public String getPassword() { return password; }

  public void setEmail(String nouMail) { email = nouMail; }
  public void setPhotoUrl(String nouUrl) { photoUrl = nouUrl; }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeSerializable(this.id);
    dest.writeString(this.email);
    dest.writeString(this.password);
    dest.writeString(this.photoUrl);
  }

  public void readFromParcel(Parcel source) {
    this.id = (ClientId) source.readSerializable();
    this.email = source.readString();
    this.password = source.readString();
    this.photoUrl = source.readString();
  }

  protected ClientPO(Parcel in) {
    this.id = (ClientId) in.readSerializable();
    this.email = in.readString();
    this.password = in.readString();
    this.photoUrl = in.readString();
  }

  public static final Creator<ClientPO> CREATOR = new Creator<ClientPO>() {
    @Override
    public ClientPO createFromParcel(Parcel source) {
      return new ClientPO(source);
    }

    @Override
    public ClientPO[] newArray(int size) {
      return new ClientPO[size];
    }
  };
}
