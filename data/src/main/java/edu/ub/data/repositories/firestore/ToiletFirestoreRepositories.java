package edu.ub.data.repositories.firestore;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;


import edu.ub.data.dtos.firestore.ToiletFirestoreDto;
import edu.ub.data.mappers.DTOToDomainMapper;
import edu.ub.domain.model.entities.Toilet;
import edu.ub.features.fe.repositories.ToiletRepository;

public class ToiletFirestoreRepositories implements ToiletRepository {

    private static final String CLIENTS_COLLECTION_NAME = "lavabos";

    private final FirebaseFirestore db;
    private final DTOToDomainMapper DTOtoDomainMapper;

    public ToiletFirestoreRepositories() {
        db = FirebaseFirestore.getInstance();
        DTOtoDomainMapper = new DTOToDomainMapper();
    }

    /* Constants */
    /* Attributes */
    /* Empty constructor */
    //DONE
    //PROGRAMACIÓ REACTIVA
    @Override
    public Observable<Toilet> getToiletById(String uid) {
        return Observable.create(emitter -> {
            db.collection("lavabos")
                    .whereEqualTo("uid", uid)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            ToiletFirestoreDto dto = documentSnapshot.toObject(ToiletFirestoreDto.class);
                            Toilet t = DTOToDomainMapper.mapObject(dto, Toilet.class);
                            emitter.onNext(t);
                            emitter.onComplete();
                        }else{
                            emitter.onError(new Exception("No toilet found with uid: " + uid));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Observable<List<Toilet>> getAllToilet() {
        return Observable.create(emitter -> {
            db.collection(CLIENTS_COLLECTION_NAME)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            List<Toilet> products = new ArrayList<>();
                            for (ToiletFirestoreDto toiletDto : querySnapshot.toObjects(ToiletFirestoreDto.class)) {
                                Toilet t = DTOtoDomainMapper.map(toiletDto, Toilet.class);
                                products.add(t);
                                //TODO - emitter.onNext fora del bucle
                                emitter.onNext(products);
                            }

                            emitter.onComplete();
                        }else{
                            emitter.onError(new Exception("No toilets found"));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    //TODO - ADD
    @Override
    public Completable add(Toilet toilet) {
        return Completable.create(emitter -> {
            ToiletFirestoreDto productDto = DTOtoDomainMapper.map(toilet, ToiletFirestoreDto.class);
            db.collection(CLIENTS_COLLECTION_NAME)
                    .document(toilet.getUid().getUid())
                    .set(productDto)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Error adding product", e)));
        });
    }

}

