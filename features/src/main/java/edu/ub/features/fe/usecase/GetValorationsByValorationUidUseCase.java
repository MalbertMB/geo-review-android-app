package edu.ub.features.fe.usecase;

import edu.ub.domain.model.entities.Valoration;
import io.reactivex.rxjava3.core.Observable;

public interface GetValorationsByValorationUidUseCase {
    Observable<Valoration> execute(String id);

}