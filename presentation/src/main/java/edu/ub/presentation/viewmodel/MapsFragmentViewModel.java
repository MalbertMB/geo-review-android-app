package edu.ub.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import edu.ub.domain.model.entities.Client;
import edu.ub.domain.model.entities.Toilet;
import edu.ub.domain.session.SessionManager;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.features.fe.usecase.GetAllToiletsUseCase;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.pos.mappers.DomainToPOMapper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsFragmentViewModel extends ViewModel {

    private final MutableLiveData<List<ToiletPO>> toiletsLiveData = new MutableLiveData<>();
    private final GetAllToiletsUseCase getAllToiletsUseCase;
    private final MutableLiveData<Throwable> AddError = new MutableLiveData<>();
    public final MutableLiveData<Boolean> addMarker = new MutableLiveData<>(false);
    private final CompositeDisposable disposables = new CompositeDisposable();
    public LiveData<Boolean> getAddMarker() {
        return addMarker;
    }
    private final MutableLiveData<String> clientActual = new MutableLiveData<>();

    public MapsFragmentViewModel(GetAllToiletsUseCase getAllToiletsUseCase) {
        this.getAllToiletsUseCase = getAllToiletsUseCase;
        loadToiletCoordinates();
    }

    public LiveData<List<ToiletPO>> getToilets() {
        return toiletsLiveData;
    }

    public void addMarker() {
        addMarker.setValue(!Boolean.TRUE.equals(addMarker.getValue()));
    }

    //PROGRAMACIÓ REACTIVA
    public void loadToiletCoordinates() {
        Disposable d = getAllToiletsUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(toilets -> {
                    List<ToiletPO> toiletPOs = new ArrayList<>();
                    for (Toilet toilet : toilets) {
                        toiletPOs.add(DomainToPOMapper.mapObject(toilet, ToiletPO.class));
                    }
                    return toiletPOs;
                })
                .subscribe(
                        toiletsLiveData::setValue,
                        throwable -> {
                            Log.e("MapsViewModel", "Error loading toilets", throwable);
                            AddError.postValue(throwable);
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