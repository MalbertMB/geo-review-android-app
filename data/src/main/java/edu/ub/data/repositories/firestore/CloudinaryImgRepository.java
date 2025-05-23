package edu.ub.data.repositories.firestore;

import android.net.Uri;

import edu.ub.features.fe.repositories.CloudinaryRepository;
import io.reactivex.rxjava3.core.Single;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class CloudinaryImgRepository implements CloudinaryRepository {

    @Override
    public Single<String> uploadImage(Object imageSource) {
        return Single.create(emitter -> {
            if (!(imageSource instanceof Uri)) {
                emitter.onError(new IllegalArgumentException("Invalid image source"));
                return;
            }

            Uri imageUri = (Uri) imageSource;

            MediaManager.get().upload(imageUri)
                    .unsigned("cagaUB")
                    .option("upload_preset", "cagaUB")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {}

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {}

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String imageUrl = (String) resultData.get("secure_url");
                            emitter.onSuccess(imageUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            emitter.onError(new Exception("Upload error: " + error.getDescription()));
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            emitter.onError(new Exception("Upload rescheduled: " + error.getDescription()));
                        }
                    }).dispatch();
        });
    }
}

