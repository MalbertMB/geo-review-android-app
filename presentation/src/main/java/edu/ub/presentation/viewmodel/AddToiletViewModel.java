package edu.ub.presentation.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.domain.model.entities.Client;
import edu.ub.domain.session.SessionManager;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.features.fe.usecase.AddToiletUseCase;
import edu.ub.features.fe.usecase.UploadImgToCloudinaryUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddToiletViewModel extends ViewModel {
    private AddToiletUseCase addToiletUseCase;
    private UploadImgToCloudinaryUseCase uploadImgToCloudinaryUseCase;
    private final MutableLiveData<Boolean> addSucces = new MutableLiveData<>();
    private final MutableLiveData<Throwable> addError = new MutableLiveData<>();
    private final MutableLiveData<String> usuariActual = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Boolean> uploadingImage = new MutableLiveData<>(false);
    private final MutableLiveData<String> uploadedImageUrl = new MutableLiveData<>();


    public AddToiletViewModel(AddToiletUseCase addToiletUseCase, UploadImgToCloudinaryUseCase uploadImgToCloudinaryUseCase){
        this.addToiletUseCase = addToiletUseCase;
        this.uploadImgToCloudinaryUseCase = uploadImgToCloudinaryUseCase;
    }

    public LiveData<String> getUsuariActual() {
        return usuariActual;
    }

    public LiveData<String> getUploadedImageUrl() {
        return uploadedImageUrl;
    }

    public LiveData<Boolean> getUploadingImage() {
        return uploadingImage;
    }

    public LiveData<Boolean> getAddSucces(){
        return this.addSucces;
    }

    public LiveData<Throwable> getAddError(){
        return this.addError;
    }

    public void checkLoggedInUser() {
        if (SessionManager.isLoggedIn()) {
            Client user = SessionManager.getCurrentClient();
            ClientId userId = user.getId();
            usuariActual.setValue(String.valueOf(userId));
        } else {
            usuariActual.setValue(null);
        }
    }

    public void addToilet(String name, String clientId, String description, String coord, int ratingValue, String ratingText,
                          String imgUrl, int nValoration, boolean men, boolean women, boolean unisex,
                          boolean handicap, boolean free, boolean baby) {

        // Validar rating
        int rating = Math.round(ratingValue);
        if (rating < 0 || rating > 5) {
            addError.postValue(new IllegalArgumentException("La valoració ha de ser entre 1 y 5 estrellas"));
            return;
        }

        // Sumar valoración inicial

        disposables.add(
                addToiletUseCase.execute(
                                name, clientId, description, coord,
                                rating, ratingText, imgUrl,
                                rating, nValoration,
                                men, women, unisex, handicap, free, baby
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> addSucces.postValue(true),
                                addError::postValue
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


    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
