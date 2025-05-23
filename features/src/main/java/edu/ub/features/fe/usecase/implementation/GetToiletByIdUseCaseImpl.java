package edu.ub.features.fe.usecase.implementation;


import edu.ub.domain.model.entities.Toilet;
import edu.ub.features.fe.repositories.ToiletRepository;
import edu.ub.features.fe.usecase.GetToiletByIdUseCase;
import io.reactivex.rxjava3.core.Observable;

public class GetToiletByIdUseCaseImpl implements GetToiletByIdUseCase {

    private final ToiletRepository toiletRepository;

    public GetToiletByIdUseCaseImpl(ToiletRepository toiletRepository) {
        this.toiletRepository = toiletRepository;
    }

    public Observable<Toilet> execute(String id){
            return toiletRepository.getToiletById(id);

    }

}
