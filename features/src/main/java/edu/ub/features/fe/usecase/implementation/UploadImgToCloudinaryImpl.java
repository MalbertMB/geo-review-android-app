package edu.ub.features.fe.usecase.implementation;

import edu.ub.features.fe.repositories.CloudinaryRepository;
import edu.ub.features.fe.usecase.UploadImgToCloudinaryUseCase;
import io.reactivex.rxjava3.core.Single;

public class UploadImgToCloudinaryImpl implements UploadImgToCloudinaryUseCase {
    private CloudinaryRepository imageRepository;

    public UploadImgToCloudinaryImpl(CloudinaryRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Single<String> execute(Object imageSource) {
        return imageRepository.uploadImage(imageSource);
    }
}
