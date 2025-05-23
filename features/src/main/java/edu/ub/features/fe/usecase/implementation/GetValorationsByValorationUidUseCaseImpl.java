package edu.ub.features.fe.usecase.implementation;

import edu.ub.domain.model.entities.Valoration;
import edu.ub.features.fe.repositories.ValorationRepository;
import edu.ub.features.fe.usecase.GetValorationsByValorationUidUseCase;
import io.reactivex.rxjava3.core.Observable;

public class GetValorationsByValorationUidUseCaseImpl implements GetValorationsByValorationUidUseCase {

    private final ValorationRepository valorationRepository;

    public GetValorationsByValorationUidUseCaseImpl(ValorationRepository valorationRepository) {
        this.valorationRepository = valorationRepository;
    }

    public Observable<Valoration> execute(String id){
        return valorationRepository.getValorationByUid(id);

    }
}