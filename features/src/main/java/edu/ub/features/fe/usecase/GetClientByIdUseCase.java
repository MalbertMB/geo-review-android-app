package edu.ub.features.fe.usecase;

import edu.ub.domain.model.entities.Client;
import edu.ub.domain.model.entities.Toilet;
import io.reactivex.rxjava3.core.Observable;

public interface GetClientByIdUseCase {

    //PROGRAMACIÓ REACTIVA
    Observable<Client> execute(String id);

}
