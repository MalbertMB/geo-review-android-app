package edu.ub.presentation.viewmodel;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.domain.model.entities.Client;
import edu.ub.domain.model.entities.Toilet;
import edu.ub.domain.session.SessionManager;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.ToiletUid;
import edu.ub.domain.model.entities.Valoration;
import edu.ub.features.fe.usecase.GetToiletByIdUseCase;
import edu.ub.features.fe.usecase.RateToiletUseCase;
import edu.ub.features.fe.usecase.UploadImgToCloudinaryUseCase;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.pos.mappers.DomainToPOMapper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddValorationViewModel extends ViewModel {

    private final GetToiletByIdUseCase getToiletByIdUseCase;

    private final RateToiletUseCase rateToiletUseCase;
    private final UploadImgToCloudinaryUseCase uploadImgToCloudinaryUseCase;
    private final MutableLiveData<Boolean> addSuccess = new MutableLiveData<>();
    private final MutableLiveData<Throwable> addError = new MutableLiveData<>();
    private final MutableLiveData<String> uploadedImageUrl = new MutableLiveData<>();
    private final MutableLiveData<Boolean> uploadingImage = new MutableLiveData<>(false);
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<ToiletPO> toiletLiveData = new MutableLiveData<ToiletPO>();
    private final MutableLiveData<Throwable> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> clientActual = new MutableLiveData<>();

    public AddValorationViewModel(RateToiletUseCase rateToiletUseCase, GetToiletByIdUseCase getToiletByIdUseCase, UploadImgToCloudinaryUseCase uploadImgToCloudinaryUseCase) {
        this.rateToiletUseCase = rateToiletUseCase;
        this.getToiletByIdUseCase = getToiletByIdUseCase;
        this.uploadImgToCloudinaryUseCase = uploadImgToCloudinaryUseCase;
        checkLoggedInClient();
    }

    public MutableLiveData<Boolean> getAddSuccess() {
        return addSuccess;
    }

    public MutableLiveData<Throwable> getAddError() {
        return addError;
    }

    public LiveData<ToiletPO> getToiletLiveData() {
        return toiletLiveData;
    }

    public LiveData<String> getUploadedImageUrl() {
        return uploadedImageUrl;
    }

    public LiveData<Boolean> getUploadingImage() {
        return uploadingImage;
    }

    public void addValoration(ToiletPO toiletPO, int rating, String comment) {
        String clientId = clientActual.getValue();
        Toilet domainToilet = DomainToPOMapper.mapObject(toiletPO, Toilet.class);

        if (clientId == null) {
            addError.postValue(new IllegalStateException("Client not logged in"));
            return;
        }

        //Si l'imatge ha estat pujada previament, l'utilitzem. Si no "default"
        String imageUrl = uploadedImageUrl.getValue();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "default";
        }

        Completable addValorationFlow = rateToiletUseCase.execute(
                domainToilet,
                domainToilet.getUid(),
                ClientId.fromString(clientId),
                rating,
                comment,
                imageUrl
        );

        disposables.add(
                addValorationFlow
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> addSuccess.postValue(true), addError::postValue)
        );
    }

    public void loadToiletInfo(String toiletId) {
        disposables.add(
                getToiletByIdUseCase.execute(toiletId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                toilet -> {
                                    ToiletPO toiletPO = DomainToPOMapper.mapObject(toilet, ToiletPO.class);
                                    toiletLiveData.setValue(toiletPO);
                                },
                                throwable -> {
                                    Log.d("ErrorView", "Error: " + throwable.getMessage());
                                    errorLiveData.setValue(throwable);
                                }
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
                                addError::postValue
                        )
        );
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

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}

