package edu.ub.data.repositories.firestore;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ub.data.dtos.firestore.ValorationFirestoreDto;
import edu.ub.data.mappers.DTOToDomainMapper;
import edu.ub.domain.model.entities.Toilet;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.model.entities.Valoration;
import edu.ub.features.fe.repositories.ValorationRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class ValorationFirestoreRepositories implements ValorationRepository {

    private static final String VALORATION_COLLECTION_NAME = "valoracions";

    private final FirebaseFirestore db;

    private final DTOToDomainMapper DTOtoDomainMapper;

    public ValorationFirestoreRepositories() {
        this.db = FirebaseFirestore.getInstance();
        DTOtoDomainMapper = new DTOToDomainMapper();
    }

    //DONE

    //Programació Reactiva

    @Override
    public Observable<Valoration> getValorationByUid(String id) {
        return Observable.create(emitter -> {
            Log.d("FirestoreQuery", "Buscando valoración con ID: " + id);

            // Corrección clave: usar document() en lugar de whereEqualTo()
            db.collection("valoracions")
                    .document(id)  // Busca directamente por ID de documento
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            try {
                                ValorationFirestoreDto dto = documentSnapshot.toObject(ValorationFirestoreDto.class);
                                if (dto != null) {
                                    Valoration valoration = DTOToDomainMapper.mapObject(dto, Valoration.class);
                                    Log.d("FirestoreSuccess", "Valoración encontrada: " + valoration.getUid());
                                    emitter.onNext(valoration);
                                } else {
                                    Log.e("FirestoreError", "Error al convertir documento a DTO");
                                    emitter.onError(new Exception("Error mapping document to DTO"));
                                }
                            } catch (Exception e) {
                                Log.e("MappingError", "Error en mapeo", e);
                                emitter.onError(e);
                            }
                        } else {
                            Log.d("FirestoreNotFound", "No existe valoración con ID: " + id);
                            emitter.onError(new Exception("No valoration found with uid: " + id));
                        }
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreFailure", "Error Firestore", e);
                        emitter.onError(e);
                    });
        });
    }

    @Override
    public Completable save(Valoration valoration) {
        return Completable.create(emitter -> {
            Map<String, Object> valorationData = new HashMap<>();
            valorationData.put("uid", valoration.getUid().getUID());
            valorationData.put("toiletUid", valoration.getToiletUid().getUid());
            valorationData.put("clientId", valoration.getClientId().getId());
            valorationData.put("rating", valoration.getRating().getValue());
            valorationData.put("comment", valoration.getComment().getText());
            valorationData.put("date", valoration.getDate().toString());
            valorationData.put("imatgeUrl", valoration.getImatgeUrl());

            db.collection(VALORATION_COLLECTION_NAME)
                    .document(valoration.getUid().getUID())
                    .set(valorationData)
                    .addOnSuccessListener(aVoid -> {
                        emitter.onComplete(); // Operación completada
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error saving valoration", e);
                        emitter.onError(new Throwable("Error adding valoration: " + e.getMessage()));
                    });
        });
    }


    @Override
    public Completable updateValoration(Toilet toilet, Valoration valoration) {
        return Completable.create(emitter -> {
            DocumentReference toiletDocRef = db.collection("lavabos").document(toilet.getUid().getUid());

            // Combinamos ambas operaciones de actualización
            Task<Void> updateUidsTask = toiletDocRef.update("valorationUids",
                    FieldValue.arrayUnion(valoration.getUid().getUID()));

            Task<Void> updateRatingTask = toiletDocRef.update("ratingAverage",
                    toilet.getRatingAverage());

            // Usamos Tasks.whenAllSuccess para esperar que ambas completen
            Tasks.whenAllComplete(updateUidsTask, updateRatingTask)
                    .addOnSuccessListener(tasks -> {
                        // Verificamos si alguna falló
                        for (Task<?> task : tasks) {
                            if (!task.isSuccessful()) {
                                emitter.onError(task.getException());
                                return;
                            }
                        }
                        emitter.onComplete();
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Observable<List<Valoration>> findByClient(String clientUid) {
        return Observable.create(emitter -> {
            db.collection(VALORATION_COLLECTION_NAME)
                    .whereEqualTo("clientId", clientUid)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            List<Valoration> valorations = new ArrayList<>();
                            for (ValorationFirestoreDto dto : querySnapshot.toObjects(ValorationFirestoreDto.class)) {
                                Valoration valoration = DTOtoDomainMapper.map(dto, Valoration.class);
                                valorations.add(valoration);
                            }
                            emitter.onNext(valorations);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Exception("No valorations found for client: " + clientUid));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

}
