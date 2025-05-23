package edu.ub.features.fe.usecase.implementation;

import edu.ub.domain.model.entities.Toilet;
import edu.ub.features.fe.repositories.ToiletRepository;
import edu.ub.features.fe.usecase.GetAllToiletsUseCase;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public class GetAllToiletsUseCaseImpl implements GetAllToiletsUseCase {

    private final ToiletRepository toiletRepository;

    public GetAllToiletsUseCaseImpl(ToiletRepository toiletRepository) {
        this.toiletRepository = toiletRepository;
    }

    public Observable<List<Toilet>> execute(){
        return toiletRepository.getAllToilet();

    }

}