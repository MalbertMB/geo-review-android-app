package edu.ub.features.fe.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ub.domain.model.entities.Toilet;
import io.reactivex.rxjava3.core.Observable;

public interface FindLavabosPropersUseCase {
    Observable<List<Toilet>> execute(double userLat, double userLong, double radiusKm) ;

}
