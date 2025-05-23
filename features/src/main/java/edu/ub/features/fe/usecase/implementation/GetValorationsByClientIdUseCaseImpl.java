package edu.ub.features.fe.usecase.implementation;

import java.util.List;

import edu.ub.domain.model.entities.Valoration;
import edu.ub.features.fe.repositories.ValorationRepository;
import edu.ub.features.fe.usecase.GetValorationByClientIdUseCase;
import io.reactivex.rxjava3.core.Observable;

public class GetValorationsByClientIdUseCaseImpl implements GetValorationByClientIdUseCase {

    private final ValorationRepository valorationRepository;

    public GetValorationsByClientIdUseCaseImpl(ValorationRepository valorationRepository) {
        this.valorationRepository = valorationRepository;
    }
    @Override
    public Observable<List<Valoration>> execute(String id) {
        return valorationRepository.findByClient(id);
    }
}
