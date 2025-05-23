package edu.ub.presentation.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.domain.model.entities.Client;
import edu.ub.domain.session.SessionManager;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.features.fe.usecase.UploadImgToCloudinaryUseCase;
import edu.ub.features.fe.usecase.implementation.UpdateClientUseCaseImpl;
import edu.ub.presentation.pos.ClientPO;
import edu.ub.presentation.pos.mappers.DomainToPOMapper;
import edu.ub.presentation.pos.mappers.POToDomainMapper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
public class ConfigFragmentViewModel extends ViewModel {
    private final MutableLiveData<ClientPO> currentClient = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Throwable> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> uploadingImage = new MutableLiveData<>(false);

    private final MutableLiveData<String> uploadedImageUrl = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final UploadImgToCloudinaryUseCase uploadImgToCloudinaryUseCase;
    private final UpdateClientUseCaseImpl updateClientUseCase;


    public ConfigFragmentViewModel(UpdateClientUseCaseImpl updateClientUseCase, UploadImgToCloudinaryUseCase uploadImgToCloudinaryUseCase) {
        this.updateClientUseCase = updateClientUseCase;
        this.uploadImgToCloudinaryUseCase = uploadImgToCloudinaryUseCase;
        loadCurrentClient();
    }
    public LiveData<ClientPO> getCurrentClient() {
        return currentClient;
    }

    public LiveData<Boolean> getUploadingImage() {
        return uploadingImage;
    }

    public MutableLiveData<String> getUploadedImageUrl() {
        return uploadedImageUrl;
    }

    public MutableLiveData<Throwable> getErrorLiveData() {
        return errorLiveData;
    }

    private void loadCurrentClient() {
        if (SessionManager.isLoggedIn()) {
            Client client = SessionManager.getCurrentClient();
            currentClient.setValue(DomainToPOMapper.mapObject(client, ClientPO.class));
        } else {
            currentClient.setValue(null);
        }
    }

    public void updateUserEmail(String newEmail) {
        ClientPO clientPO = currentClient.getValue();
        if (clientPO == null || newEmail == null) return;

        clientPO.setEmail(newEmail);
        updateClient(clientPO);
    }

    public void updateUserPhotoUrl(String newPhotoUrl) {
        ClientPO clientPO = currentClient.getValue();
        if (clientPO == null || newPhotoUrl == null) return;

        clientPO.setPhotoUrl(newPhotoUrl);
        updateClient(clientPO);
    }

    public void updateClient(ClientPO clientPO) {
        ClientPO updatedClientPO = currentClient.getValue();
        if (updatedClientPO == null) return;

        disposables.add(
                updateClientUseCase.execute(POToDomainMapper.mapObject(updatedClientPO, Client.class))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    updateSuccess.setValue(true);
                                    Client updated = POToDomainMapper.mapObject(updatedClientPO, Client.class);
                                    SessionManager.setCurrentClient(updated);
                                    currentClient.setValue(updatedClientPO);
                                },
                                errorLiveData::setValue
                        )
        );
    }

    public void uploadImage(Uri uri) {
        uploadingImage.postValue(true);
        disposables.add(
                uploadImgToCloudinaryUseCase.execute(uri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> uploadingImage.postValue(false))
                        .subscribe(
                                uploadedImageUrl::postValue,
                                errorLiveData::postValue
                        )
        );
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
