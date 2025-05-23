package edu.ub.features.fe.usecase;

import edu.ub.domain.model.entities.Toilet;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public interface GetAllToiletsUseCase {
  Observable<List<Toilet>> execute();

}