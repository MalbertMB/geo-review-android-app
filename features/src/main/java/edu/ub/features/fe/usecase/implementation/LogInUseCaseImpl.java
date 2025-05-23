package edu.ub.features.fe.usecase.implementation;


import edu.ub.domain.model.entities.Client;
import edu.ub.domain.session.SessionManager;
import edu.ub.features.fe.repositories.ClientRepository;
import edu.ub.features.fe.usecase.LogInUseCase;
import io.reactivex.rxjava3.core.Observable;
/**
 * Authentication service using a ClientRepository.
 */
public class LogInUseCaseImpl implements LogInUseCase {
  /* Attributes */
  private final ClientRepository clientRepository;

  /**
   * Empty constructor
   */
  @SuppressWarnings("unused")
  public LogInUseCaseImpl(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  /**
   * Log in client with username and password.
   * @param username the username
   * @param password the password
   *
   */

  public Observable<Client> execute(String username, String password) {
    return clientRepository.getClientById(username)
            .flatMap(client -> {
      if (client.getPassword().equals(password)) {
        SessionManager.login(client);
        return Observable.just(client);
      } else {
        return Observable.error(new Exception("Wrong password"));
      }

  });
  }

}
