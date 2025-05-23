package edu.ub.features.fe.usecase;

import edu.ub.domain.model.entities.Client;
import io.reactivex.rxjava3.core.Observable;

public interface LogInUseCase {
  Observable<Client> execute(String username, String password);
}

