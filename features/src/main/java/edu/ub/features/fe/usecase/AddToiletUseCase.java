package edu.ub.features.fe.usecase;

import io.reactivex.rxjava3.core.Completable;

public interface AddToiletUseCase {
    Completable execute(String name, String clientId, String description, String coord, int ratingValue, String ratingText, String img_url , float ratingAverage, int nValoration, boolean men, boolean women, boolean unisex, boolean handicap, boolean free, boolean baby);

}
