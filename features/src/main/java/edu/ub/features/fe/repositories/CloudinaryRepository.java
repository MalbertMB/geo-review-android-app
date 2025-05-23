package edu.ub.features.fe.repositories;
import io.reactivex.rxjava3.core.Single;

public interface CloudinaryRepository {
    Single<String> uploadImage(Object imageSource);

}
