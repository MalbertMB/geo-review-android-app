package edu.ub.features.fe.usecase;

import io.reactivex.rxjava3.core.Single;

public interface UploadImgToCloudinaryUseCase {
    Single<String> execute(Object imageSource);
}
