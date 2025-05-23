package edu.ub.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.ub.domain.model.entities.Toilet;
import edu.ub.features.fe.usecase.FindLavabosPropersUseCase;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.pos.mappers.DomainToPOMapper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PropersFragmentViewModel extends ViewModel {

    private final FindLavabosPropersUseCase findLavabosPropersUseCase;
    private final MutableLiveData<List<ToiletPO>> nearbyToilets = new MutableLiveData<>();
    private final MutableLiveData<Throwable> errorLiveData = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    double lat;
    double lg;

    public PropersFragmentViewModel( FindLavabosPropersUseCase findLavabosPropersUseCase, double lat, double lang ) {
        this.findLavabosPropersUseCase = findLavabosPropersUseCase;
        //No sé com passar-los per constructor
        this.lat = lat;
        this.lg = lang;
    }

    public LiveData<List<ToiletPO>> getNearbyToilets() {
        return nearbyToilets;
    }
    public void setCoords(double lat, double lng){
            this.lat=lat;
            this.lg = lng;
            loadNearbyToilets();

    }
    public LiveData<Throwable> getError() {
        return errorLiveData;
    }

    public void loadNearbyToilets() {
        disposables.add(findLavabosPropersUseCase.execute(lat, lg, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(toilets -> {
                    List<ToiletPO> toiletPOs = new ArrayList<>();
                    for (Toilet toilet : toilets) {
                        toiletPOs.add(DomainToPOMapper.mapObject(toilet,ToiletPO.class));
                    }
                    return toiletPOs;
                })
                .subscribe(
                        nearbyToilets::setValue,
                        throwable -> {
                            Log.e("PropersVM", "Error loading nearby toilets", throwable);
                            errorLiveData.postValue(throwable);
                        }
                ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}

