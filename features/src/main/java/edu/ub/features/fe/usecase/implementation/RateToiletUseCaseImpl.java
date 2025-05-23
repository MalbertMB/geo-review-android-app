package edu.ub.features.fe.usecase.implementation;

import edu.ub.domain.model.entities.Toilet;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.Comment;
import edu.ub.domain.valueobjects.Rating;
import edu.ub.domain.valueobjects.ToiletUid;
import edu.ub.domain.model.entities.Valoration;
import edu.ub.domain.valueobjects.ValorationUid;
import edu.ub.features.fe.repositories.CloudinaryRepository;
import edu.ub.features.fe.repositories.ToiletRepository;
import edu.ub.features.fe.repositories.ValorationRepository;
import edu.ub.features.fe.usecase.RateToiletUseCase;
import io.reactivex.rxjava3.core.Completable;

public class RateToiletUseCaseImpl implements RateToiletUseCase {
    private final ToiletRepository toiletRepository;
    private final ValorationRepository valorationRepository;

    public RateToiletUseCaseImpl(ToiletRepository toiletRepository, ValorationRepository valorationRepository, CloudinaryRepository cloudinaryRepository) {
        this.toiletRepository = toiletRepository;
        this.valorationRepository = valorationRepository;
    }

    @Override
    public Completable execute(Toilet toilet, ToiletUid toiletUid, ClientId clientId, int ratingValue, String commentString, String imatgeUrl) {

        Rating rating = new Rating(ratingValue);
        Comment comment = new Comment(commentString);
        ValorationUid valorationUid = ValorationUid.createUID();
        Valoration valoration = new Valoration(valorationUid, toiletUid, clientId, rating, comment, imatgeUrl);

        return valorationRepository.save(valoration)
                .andThen(Completable.defer(() -> {
                    // Actualizamos el valor medio del inodoro solo después de guardar correctamente
                    toilet.calculateAverageValoration(ratingValue);
                    return valorationRepository.updateValoration(toilet, valoration);
                }));
    }
}