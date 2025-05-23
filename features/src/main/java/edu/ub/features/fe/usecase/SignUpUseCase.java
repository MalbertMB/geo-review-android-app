package edu.ub.features.fe.usecase;

import io.reactivex.rxjava3.core.Completable;

public interface SignUpUseCase {

  Completable execute(String user, String email, String password, String passwordConfirmation, String imageUrl);
}
