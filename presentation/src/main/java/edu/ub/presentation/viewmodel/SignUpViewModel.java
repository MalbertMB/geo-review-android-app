package edu.ub.presentation.viewmodel;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.features.fe.usecase.SignUpUseCase;
import edu.ub.features.fe.usecase.UpdateClientUseCase;
import edu.ub.features.fe.usecase.UploadImgToCloudinaryUseCase;
import edu.ub.presentation.viewmodel.livedata.StateData;
import edu.ub.presentation.viewmodel.livedata.StateLiveData;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignUpViewModel extends ViewModel {
  /* Use cases */
  private final SignUpUseCase signUpUseCase;
  private final UploadImgToCloudinaryUseCase uploadImgToCloudinaryUseCase;
  /* MutableLiveData */
  private final MutableLiveData<Boolean> uploadingImage = new MutableLiveData<>(false);
  private final MutableLiveData<String> uploadedImageUrl = new MutableLiveData<>();
  /* LiveData */
  private final StateLiveData<Void> signUpState;
  private final CompositeDisposable disposables = new CompositeDisposable();

  /* Constructor */
  public SignUpViewModel(SignUpUseCase signUpUseCase, UploadImgToCloudinaryUseCase uploadImgToCloudinaryUseCase) {
    super();
    this.uploadImgToCloudinaryUseCase = uploadImgToCloudinaryUseCase;
    this.signUpUseCase = signUpUseCase;
    signUpState = new StateLiveData<>();
  }
  /**
   * Returns the state of the sign-up
   * @return the state of the sign-up
   */
  public StateLiveData<Void> getSignUpState() {
    return signUpState;
  }

  public MutableLiveData<String> getUploadedImageUrl() {
      return uploadedImageUrl;
  }

  public MutableLiveData<Boolean> getUploadingImage() {
      return uploadingImage;
  }

    public int fotoAleatoria() {
        return (int) (Math.random() * 7); // 0 a 6 incluidos
  }
    /**
   * Signs up the user
   * @param username the username
   * @param password the password
   * @param passwordConfirmation the password confirmation
   */
    public void signUp(String username, String email, String password, String passwordConfirmation, Uri imageUri) {
        Single<String> imageUploadSingle;

        if (imageUri != null) {
            imageUploadSingle = uploadImgToCloudinaryUseCase.execute(imageUri);
        } else {
            imageUploadSingle = Single.fromCallable(() -> {
                switch (fotoAleatoria()) {
                    case 0: return "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605757/predef_kyle_d2y0ae.png";
                    case 1: return "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_kenny_ceuz7d.png";
                    case 2: return "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_jesus_lfonnn.png";
                    case 3: return "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_caca_ep0yxc.png";
                    case 4: return "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_stan_lm4h8b.png";
                    case 5: return "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_towelie_ygsqc0.png";
                    case 6: return "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_cartman_fr5cwb.png";
                    default: return "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_cartman_fr5cwb.png";
                }
            });
        }

        Disposable disposable = imageUploadSingle
                .flatMapCompletable(imageUrl ->
                        signUpUseCase.execute(username, email, password, passwordConfirmation, imageUrl)
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> signUpState.postSuccess(null),
                        signUpState::postError
                );

        disposables.add(disposable);
    }

    public void uploadImage(Uri uri) {
        uploadingImage.postValue(true);
        disposables.add(
                uploadImgToCloudinaryUseCase.execute(uri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> uploadingImage.postValue(false))
                        .subscribe(
                                uploadedImageUrl::postValue
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear(); // clean de suscripcions
    }
}
