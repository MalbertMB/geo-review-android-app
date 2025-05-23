package edu.ub.features.fe.usecase;

import java.util.List;

import edu.ub.domain.model.entities.Valoration;
import io.reactivex.rxjava3.core.Observable;

public interface GetValorationByClientIdUseCase {
    Observable<List<Valoration>> execute(String id);
}
