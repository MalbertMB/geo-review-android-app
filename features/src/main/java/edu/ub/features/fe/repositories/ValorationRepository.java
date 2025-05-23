package edu.ub.features.fe.repositories;

import java.util.List;

import edu.ub.domain.model.entities.Toilet;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.model.entities.Valoration;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface ValorationRepository {
    Completable save(Valoration valuation);
    Completable updateValoration(Toilet toilet, Valoration valoration);
    Observable<Valoration> getValorationByUid(String id);
    Observable<List<Valoration>> findByClient(String clientUid);
}
