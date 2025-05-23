package edu.ub.presentation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ub.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.data.repositories.firestore.CloudinaryImgRepository;
import edu.ub.data.repositories.firestore.ToiletFirestoreRepositories;
import edu.ub.data.repositories.firestore.ValorationFirestoreRepositories;
import edu.ub.features.fe.repositories.ClientRepository;
import edu.ub.features.fe.repositories.CloudinaryRepository;
import edu.ub.features.fe.repositories.ToiletRepository;
import edu.ub.features.fe.repositories.ValorationRepository;
import edu.ub.features.fe.usecase.AddToiletUseCase;
import edu.ub.features.fe.usecase.FindLavabosPropersUseCase;
import edu.ub.features.fe.usecase.GetToiletByIdUseCase;
import edu.ub.features.fe.usecase.LogInUseCase;
import edu.ub.features.fe.usecase.RateToiletUseCase;
import edu.ub.features.fe.usecase.SignUpUseCase;
import edu.ub.features.fe.usecase.implementation.AddToiletUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.FindLavabosPropersUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.GetClientByIdUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.GetToiletByIdUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.GetValorationsByClientIdUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.LogInUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.RateToiletUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.SignUpUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.GetAllToiletsUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.GetValorationsByValorationUidUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.UpdateClientUseCaseImpl;
import edu.ub.features.fe.usecase.implementation.UploadImgToCloudinaryImpl;
import edu.ub.presentation.viewmodel.AddToiletViewModel;
import edu.ub.presentation.viewmodel.AddValorationViewModel;
import edu.ub.presentation.viewmodel.ConfigFragmentViewModel;
import edu.ub.presentation.viewmodel.LogInViewModel;
import edu.ub.presentation.viewmodel.MapsFragmentViewModel;
import edu.ub.presentation.viewmodel.PerfilFragmentViewModel;
import edu.ub.presentation.viewmodel.PropersFragmentViewModel;
import edu.ub.presentation.viewmodel.SignUpViewModel;
import edu.ub.presentation.viewmodel.ToiletInfoViewModel;

public class AppContainer {

    private Application application;

    //REPOSITORIES
    private final ToiletRepository toiletRepository = new ToiletFirestoreRepositories();

    private final ClientRepository clientRepository = new ClientFirestoreRepository();

    private final ValorationRepository valorationRepository = new ValorationFirestoreRepositories();

    private final CloudinaryRepository cloudinaryRepository = new CloudinaryImgRepository();

    //USE CASE
    private final AddToiletUseCase addToiletUseCase = new AddToiletUseCaseImpl(toiletRepository, valorationRepository);

    private final LogInUseCase logInUseCase = new LogInUseCaseImpl(clientRepository);

    private final SignUpUseCase signUpUseCase = new SignUpUseCaseImpl(clientRepository);

    private final UploadImgToCloudinaryImpl uploadImgToCloudinary = new UploadImgToCloudinaryImpl(cloudinaryRepository);

    private final GetAllToiletsUseCaseImpl getAllToiletsUseCase = new GetAllToiletsUseCaseImpl(toiletRepository);

    private final GetToiletByIdUseCase getToiletByIdUseCase = new GetToiletByIdUseCaseImpl(toiletRepository);

    private final RateToiletUseCase rateToiletUseCase = new RateToiletUseCaseImpl(toiletRepository, valorationRepository, cloudinaryRepository);

    private final GetValorationsByValorationUidUseCaseImpl getValorationsByToiletUidUseCase = new GetValorationsByValorationUidUseCaseImpl(valorationRepository);

    private final GetClientByIdUseCaseImpl getClientByIdUseCase = new GetClientByIdUseCaseImpl(clientRepository);

    private final FindLavabosPropersUseCase findLavabosPropersUseCase = new FindLavabosPropersUseCaseImpl(toiletRepository);
    private final GetValorationsByClientIdUseCaseImpl getValorationsByClientIdUseCaseImpl = new GetValorationsByClientIdUseCaseImpl(valorationRepository);

    private final UpdateClientUseCaseImpl updateClientUseCase = new UpdateClientUseCaseImpl(clientRepository);
    private static final double latitude = -1;
    private static final double longitude = -1;
    //VIEWMODEL
    public ViewModelFactory viewModelFactory = new ViewModelFactory(this);

    public static class ViewModelFactory implements ViewModelProvider.Factory {

        private final AppContainer appContainer;

        public ViewModelFactory(AppContainer appContainer){
            this.appContainer = appContainer;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(MapsFragmentViewModel.class)) {
                return (T) new MapsFragmentViewModel(appContainer.getAllToiletsUseCase);
            } else if (modelClass.isAssignableFrom(AddToiletViewModel.class)) {
                return (T) new AddToiletViewModel(appContainer.addToiletUseCase, appContainer.uploadImgToCloudinary);
            } else if (modelClass.isAssignableFrom(LogInViewModel.class)) {
                return (T) new LogInViewModel(appContainer.logInUseCase);
            } else if (modelClass.isAssignableFrom(SignUpViewModel.class)) {
                return (T) new SignUpViewModel(appContainer.signUpUseCase, appContainer.uploadImgToCloudinary);
            } else if (modelClass.isAssignableFrom(ToiletInfoViewModel.class)) {
                return (T) new ToiletInfoViewModel(appContainer.getToiletByIdUseCase, appContainer.rateToiletUseCase, appContainer.getValorationsByToiletUidUseCase, appContainer.getClientByIdUseCase);
            } else if (modelClass.isAssignableFrom(AddValorationViewModel.class)) {
                return (T) new AddValorationViewModel(appContainer.rateToiletUseCase, appContainer.getToiletByIdUseCase, appContainer.uploadImgToCloudinary);
            } else if (modelClass.isAssignableFrom(PropersFragmentViewModel.class)) {
                return (T)  new PropersFragmentViewModel(appContainer.findLavabosPropersUseCase, latitude, longitude);
            } else if (modelClass.isAssignableFrom(PerfilFragmentViewModel.class)) {
                return (T) new PerfilFragmentViewModel(appContainer.getValorationsByClientIdUseCaseImpl);
            } else if (modelClass.isAssignableFrom(ConfigFragmentViewModel.class)) {
                return (T) new ConfigFragmentViewModel(appContainer.updateClientUseCase, appContainer.uploadImgToCloudinary);
            }
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }

    public AppContainer(Application application){
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }


}
