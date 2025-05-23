package edu.ub.features.fe.usecase;

import edu.ub.domain.model.entities.Client;
import io.reactivex.rxjava3.core.Completable;

public interface UpdateClientUseCase {
    Completable execute(Client client);
}
