package edu.ub.data.repositories.firestore;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import edu.ub.data.dtos.firestore.ClientFirestoreDto;
import edu.ub.data.dtos.firestore.ToiletFirestoreDto;
import edu.ub.data.mappers.DTOToDomainMapper;
import edu.ub.domain.model.entities.Client;
import edu.ub.domain.model.entities.Toilet;
import edu.ub.features.fe.repositories.ClientRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

/**
 * Firebase repository for clients.
 */
public class ClientFirestoreRepository implements ClientRepository {
  /* Constants */
  private static final String CLIENTS_COLLECTION_NAME = "clients";
  /* Attributes */
  private final FirebaseFirestore db;

  /**
   * Empty constructor
   */
  public ClientFirestoreRepository() {
    db = FirebaseFirestore.getInstance();
  }


  /**
   * Get a client by id.
   *
   * @param id The client id.
   */
  public Observable<Client> getClientById(String id) {
    return Observable.create(emitter -> {
      db.collection(CLIENTS_COLLECTION_NAME)
              .document(id)
              .get()
              .addOnSuccessListener(ds -> {
                if (ds.exists()) {
                  ClientFirestoreDto clientDto = ds.toObject(ClientFirestoreDto.class);
                  Client client = DTOToDomainMapper.mapObject(clientDto, Client.class);
                  emitter.onNext(client);
                  emitter.onComplete();
                }else{
                  emitter.onError(new Exception("NotFound"));
                }
              })
              .addOnFailureListener(emitter::onError);
    });
  }

    /**
     * Add a client to the Firebase CloudFirestore.
     *
     * @param client The client to add.
     */
    @Override
    public Completable add(Client client) {
        ClientFirestoreDto dto = DTOToDomainMapper.mapObject(client, ClientFirestoreDto.class);
        return Completable.create(emitter -> {
            db.collection(CLIENTS_COLLECTION_NAME)
                    .document(client.getId().getId())
                    .set(dto)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable updateClient(Client client) {
        ClientFirestoreDto dto = DTOToDomainMapper.mapObject(client, ClientFirestoreDto.class);
        Map<String, Object> updateData = new HashMap<>();

        //Per simplificar encara que no s'hagin tocat seran iguals
        updateData.put("email", dto.getEmail());
        updateData.put("photoUrl", dto.getPhotoUrl());

        return Completable.create(emitter -> {
            db.collection(CLIENTS_COLLECTION_NAME)
                    .document(client.getId().getId())
                    .update(updateData)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}
