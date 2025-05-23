package edu.ub.features.fe.repositories;

import edu.ub.domain.model.entities.Client;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface ClientRepository {
  Completable add(Client client);
  Observable<Client> getClientById(String id);
  Completable updateClient(Client client);
}
