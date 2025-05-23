package edu.ub.features.fe.repositories;

import java.util.List;

import edu.ub.domain.model.entities.Toilet;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

/* Interficie per poder actualitzar o agafar lavabos de la base de dades */

public interface ToiletRepository {
    Completable add(Toilet toilet);
    Observable<Toilet> getToiletById(String uid);
    Observable<List<Toilet>> getAllToilet();
}
