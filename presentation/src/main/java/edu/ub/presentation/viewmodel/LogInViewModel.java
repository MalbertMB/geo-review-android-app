package edu.ub.presentation.viewmodel;

import static edu.ub.presentation.viewmodel.livedata.StateData.DataStatus.ERROR;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import edu.ub.domain.session.SessionManager;
import edu.ub.features.fe.usecase.LogInUseCase;
import edu.ub.presentation.pos.ClientPO;
import edu.ub.presentation.pos.mappers.DomainToPOMapper;
import edu.ub.presentation.viewmodel.livedata.StateLiveData;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class LogInViewModel extends ViewModel {
  /* Use cases */
  private final LogInUseCase logInUseCase;
  /* LiveData */
  private final StateLiveData<ClientPO> logInState;
  private final MutableLiveData<Throwable> errorLiveData;
  MutableLiveData<ClientPO> clientLiveData;
  private final CompositeDisposable disposables = new CompositeDisposable();

  /* Constructor */
  public LogInViewModel(LogInUseCase logInUseCase) {
    this.logInUseCase = logInUseCase;
    this.errorLiveData = new MutableLiveData<>();
    this.clientLiveData = new MutableLiveData<>();
    logInState = new StateLiveData<>();
  }
  /**
   * Returns the state of the log-in
   * @return the state of the log-in
   */
  public StateLiveData<ClientPO> getLogInState() {
    return logInState;
  }

  /**
   * Logs in the user
   * @param username the username
   * @param password the password
   */

  public void logIn(String username, String password) {
      Log.d("ErrorView", "Aqui");
      Disposable d = logInUseCase.execute(username, password)
              .map(client -> DomainToPOMapper.mapObject(client, ClientPO.class))
              .subscribe(
                      clientPO -> {
                          clientLiveData.postValue(clientPO);
                          logInState.postSuccess(clientPO);
                      },
                      throwable -> {
                          Log.d("ErrorView", "Error: " + throwable.getMessage());
                          logInState.postError(throwable);
                          errorLiveData.postValue(throwable);
                      }
              );

      disposables.add(d);
  }


}
