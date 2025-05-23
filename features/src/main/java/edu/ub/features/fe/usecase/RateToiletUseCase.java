package edu.ub.features.fe.usecase;

import edu.ub.domain.model.entities.Toilet;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.ToiletUid;
import io.reactivex.rxjava3.core.Completable;

public interface RateToiletUseCase {

  Completable execute(Toilet toilet, ToiletUid toiletUid, ClientId clientId, int ratingValue, String commentString, String imatgeUrl);
}
