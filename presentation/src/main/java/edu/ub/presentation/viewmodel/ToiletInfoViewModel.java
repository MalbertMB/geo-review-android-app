package edu.ub.presentation.viewmodel;



import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.domain.model.entities.Client;
import edu.ub.domain.session.SessionManager;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.features.fe.usecase.GetToiletByIdUseCase;
import edu.ub.features.fe.usecase.RateToiletUseCase;
import edu.ub.features.fe.usecase.GetValorationsByValorationUidUseCase;
import edu.ub.features.fe.usecase.GetClientByIdUseCase;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.pos.ValorationPO;
import edu.ub.presentation.pos.mappers.DomainToPOMapper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ToiletInfoViewModel extends ViewModel {
    private final GetToiletByIdUseCase getToiletByIdUseCase;
    private final RateToiletUseCase rateToiletUseCase;
    private final GetValorationsByValorationUidUseCase getValorationsByValorationUidUseCase;
    private final GetClientByIdUseCase getClientByIdUseCase;
    private final MutableLiveData<ToiletPO> toiletLiveData = new MutableLiveData<>();
    private final MutableLiveData<Throwable> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Client> clientLiveData = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<String> clientActual = new MutableLiveData<>();
    public ToiletInfoViewModel(GetToiletByIdUseCase getToiletByIdUseCase, RateToiletUseCase rateToiletUseCase, GetValorationsByValorationUidUseCase getValorationsByValorationUidUseCase, GetClientByIdUseCase getClientByIdUseCase) {
        this.getToiletByIdUseCase = getToiletByIdUseCase;
        this.rateToiletUseCase = rateToiletUseCase;
        this.getValorationsByValorationUidUseCase = getValorationsByValorationUidUseCase;
        this.getClientByIdUseCase = getClientByIdUseCase;
    }
    public LiveData<ToiletPO> getToiletLiveData() {
        return toiletLiveData;
    }

    private final Map<String, MutableLiveData<Client>> clientLiveDataMap = new HashMap<>();

    public LiveData<Client> getClientLiveData(String clientId) {
        if (!clientLiveDataMap.containsKey(clientId)) {
            MutableLiveData<Client> liveData = new MutableLiveData<>();
            clientLiveDataMap.put(clientId, liveData);

            Disposable d = getClientByIdUseCase.execute(clientId)
                    .subscribe(
                            liveData::setValue,
                            throwable -> Log.d("ErrorView", "Error: " + throwable.getMessage())
                    );
            disposables.add(d);
        }
        return clientLiveDataMap.get(clientId);
    }

    //PROGRAMACIÓ REACTIVA
    public void loadToiletInfo(String toiletId) {
        disposables.add(getToiletByIdUseCase.execute(toiletId)
                .map(toilet -> DomainToPOMapper.mapObject(toilet, ToiletPO.class)) // Conversió
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        toiletLiveData::setValue,
                        throwable -> {
                            Log.e("ToiletInfoVM", "Error loading toilet", throwable);
                            errorLiveData.postValue(throwable);
                        }
                ));
    }

    public void fetchAllValorationsByUids(List<String> uids, OnValorationsFetched callback) {
        List<ValorationPO> valorationsList = new ArrayList<>();
        List<String> processedUids = new ArrayList<>();
        AtomicInteger processedCount = new AtomicInteger(0);

        for (String uid : uids) {
            Disposable d = getValorationsByValorationUidUseCase.execute(uid)
                    .map(valoration -> DomainToPOMapper.mapObject(valoration, ValorationPO.class))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            valoration -> {
                                if (valoration != null && !processedUids.contains(valoration.getUid().getUID())) {
                                    processedUids.add(valoration.getUid().getUID());
                                    valorationsList.add(valoration);
                                }
                                if (processedCount.incrementAndGet() == uids.size()) {
                                    callback.onFetched(valorationsList);
                                }
                            },
                            throwable -> {
                                Log.e("ValorationError", "Error cargando valoración UID: " + uid, throwable);
                                if (processedCount.incrementAndGet() == uids.size()) {
                                    callback.onFetched(valorationsList); // Llama igualmente, incluso si hay errores
                                }
                            }
                    );
            disposables.add(d);
        }
    }

    // Interfaz de callback
    public interface OnValorationsFetched {
        void onFetched(List<ValorationPO> valorations);
    }

    public void getClientById(String id) {
        Disposable d = getClientByIdUseCase.execute(id)
                .subscribe(
                        clientLiveData::setValue,
                        throwable -> {
                            Log.d("ErrorView", "Error: " + throwable.getMessage());
                            errorLiveData.postValue(throwable);
                        }
                );
        disposables.add(d);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }

    public LiveData<String> getClientActual() {
        return clientActual;
    }

    public void checkLoggedInClient() {
        if (SessionManager.isLoggedIn()) {
            Client user = SessionManager.getCurrentClient();
            ClientId userId = user.getId();
            clientActual.setValue(String.valueOf(userId));
        } else {
            clientActual.setValue(null);
        }
    }
}
