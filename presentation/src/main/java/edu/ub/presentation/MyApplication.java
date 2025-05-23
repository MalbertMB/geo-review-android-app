package edu.ub.presentation;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {

    public AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();

         /*
        Hem de comprovar que n s'haig inicialitzat ja el MediManager
        En cas que es vulgui inicialitzar per segona vegada l'aplicació surtira
         */
        appContainer = new AppContainer(this);
        try {
            MediaManager.get();
        } catch (IllegalStateException e) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
            config.put("api_key", BuildConfig.CLOUDINARY_API_KEY);
            config.put("api_secret", BuildConfig.CLOUDINARY_API_SECRET);
            MediaManager.init(this, config);
        }
    }

    @SuppressWarnings("unused")
    public AppContainer getAppContainer() {
        return appContainer;
    }


    @SuppressWarnings("unused")
    public AppContainer.ViewModelFactory getViewModelFactory() {
        return appContainer.viewModelFactory;
    }

}
