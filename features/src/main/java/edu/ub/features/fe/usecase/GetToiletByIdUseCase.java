package edu.ub.features.fe.usecase;

import edu.ub.domain.model.entities.Toilet;
import io.reactivex.rxjava3.core.Observable;

public interface GetToiletByIdUseCase {

    //PROGRAMACIÓ REACTIVA
    Observable<Toilet> execute(String id);
}
