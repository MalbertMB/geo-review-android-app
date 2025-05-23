package edu.ub.features.fe.usecase.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.ub.domain.model.entities.Toilet;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.Comment;
import edu.ub.domain.valueobjects.Rating;
import edu.ub.domain.valueobjects.ToiletUid;
import edu.ub.domain.model.entities.Valoration;
import edu.ub.domain.valueobjects.ValorationUid;
import edu.ub.features.fe.repositories.ToiletRepository;
import edu.ub.features.fe.repositories.ValorationRepository;
import edu.ub.features.fe.usecase.AddToiletUseCase;
import io.reactivex.rxjava3.core.Completable;

public class AddToiletUseCaseImpl implements AddToiletUseCase {
    private final ToiletRepository toiletRepository;

    private final ValorationRepository valorationRepository;

    public AddToiletUseCaseImpl(ToiletRepository toiletRepository, ValorationRepository valorationRepository) {
        this.toiletRepository = toiletRepository;
        this.valorationRepository = valorationRepository;

        System.out.println("Instanciando AddToToiletUseCaseImpl:");
        System.out.println("Class: " + this.getClass());
        System.out.println("Fields: " + Arrays.toString(this.getClass().getDeclaredFields()));
    }
    /*
    Instanciem el ToiletRepository i el valorationRepository
     */
    @Override
    public Completable execute(String name, String clientId, String description, String coord, int ratingValue, String ratingText,
                               String imgUrl, float ratingAverage, int nValoration,
                               boolean men, boolean women, boolean unisex, boolean handicap, boolean free, boolean baby) {

        ToiletUid toiletUid = ToiletUid.createUID();
        List<ValorationUid> valorationUids = new ArrayList<>();

        // Creem Toilet (amb o sense valoració)
        Toilet toilet;
        Completable saveValorationCompletable = Completable.complete(); // Per defecte buit

        if (ratingValue > 0) {
            Rating rating = new Rating(ratingValue);
            Comment comment = new Comment(ratingText);
            ValorationUid valorationUid = ValorationUid.createUID();

            Valoration valoration = new Valoration(valorationUid, toiletUid, ClientId.fromString(clientId), rating, comment);
            valorationUids.add(valorationUid);

            // Guardar valoración
            saveValorationCompletable = valorationRepository.save(valoration);
            toilet = new Toilet(toiletUid, ClientId.fromString(clientId), name, description, coord, imgUrl, ratingAverage, 1,
                    men, women, unisex, handicap, free, baby, valorationUids);
        }else {
            toilet = new Toilet(toiletUid, ClientId.fromString(clientId), name, description, coord, imgUrl, ratingAverage, nValoration,
                    men, women, unisex, handicap, free, baby, valorationUids);
        }

        // Emmagatzemar toilet
        Completable saveToiletCompletable = toiletRepository.add(toilet);

        // Combinar operaciones si hi ha valoració
        return saveValorationCompletable
                .andThen(saveToiletCompletable);
    }
}
