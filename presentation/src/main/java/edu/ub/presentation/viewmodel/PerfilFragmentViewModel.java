package edu.ub.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import edu.ub.domain.model.entities.Client;
import edu.ub.domain.model.entities.Valoration;
import edu.ub.domain.session.SessionManager;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.features.fe.usecase.implementation.GetValorationsByClientIdUseCaseImpl;
import edu.ub.presentation.pos.ClientPO;
import edu.ub.presentation.pos.ValorationPO;
import edu.ub.presentation.pos.mappers.DomainToPOMapper;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class PerfilFragmentViewModel extends ViewModel {

    private final MutableLiveData<ClientPO> clientActual = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>();
    private final MutableLiveData<Throwable> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<ValorationPO>> valorationsByClient = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final GetValorationsByClientIdUseCaseImpl getValorationsByClientIdUseCaseImpl;

    public PerfilFragmentViewModel(GetValorationsByClientIdUseCaseImpl getValorationsByClientIdUseCaseImpl) {
        this.getValorationsByClientIdUseCaseImpl = getValorationsByClientIdUseCaseImpl;
    }

    public LiveData<Boolean> getLogoutSuccess() {
        return logoutSuccess;
    }
    public LiveData<ClientPO> getClientActual() {
        return clientActual;
    }
    public LiveData<List<ValorationPO>> getValorationsByClient() {
        return valorationsByClient;
    }


    public void checkLoggedInClient() {
        if (SessionManager.isLoggedIn()) {
            Client client = SessionManager.getCurrentClient();
            clientActual.setValue(DomainToPOMapper.mapObject(client, ClientPO.class));
        } else {
            clientActual.setValue(null);
        }
    }

    public void logOut() {
        SessionManager.logout();
        logoutSuccess.setValue(Boolean.TRUE);
    }

    public void loadValorationsForClient() {
        if (!SessionManager.isLoggedIn()) return;

        ClientId clientId = SessionManager.getCurrentClient().getId();

        disposables.add(
                getValorationsByClientIdUseCaseImpl.execute(clientId.toString())
                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                        .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                        .map(valorations -> {
                            List<ValorationPO> valorationPOs = new ArrayList<>();
                            for (Valoration valoration : valorations) {
                                valorationPOs.add(DomainToPOMapper.mapObject(valoration, ValorationPO.class));
                            }
                            return valorationPOs;
                        })
                        .subscribe(
                                valorations -> valorationsByClient.setValue(valorations),
                                throwable -> {
                                    throwable.printStackTrace();
                                    errorLiveData.postValue(throwable);
                                }
                        )
        );

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear(); // Allibera totes les subscripcions actives
    }

}

