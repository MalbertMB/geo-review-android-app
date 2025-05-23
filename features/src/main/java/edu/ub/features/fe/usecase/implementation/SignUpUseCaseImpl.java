package edu.ub.features.fe.usecase.implementation;


import edu.ub.domain.model.entities.Client;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.features.fe.repositories.ClientRepository;
import edu.ub.features.fe.usecase.SignUpUseCase;
import io.reactivex.rxjava3.core.Completable;

/**
 * Authentication service using a ClientRepository.
 */
public class SignUpUseCaseImpl implements SignUpUseCase {
  /* Attributes */
  private final ClientRepository clientRepository;

  /**
   * Empty constructor
   */
  @SuppressWarnings("unused")
  public SignUpUseCaseImpl(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  /**
   * Sign up a new client.
   *
   * @param username             the username
   * @param password             the password
   * @param passwordConfirmation the password confirmation
   */
  @Override
  public Completable execute(String username, String email, String password, String passwordConfirmation, String imageUrl) {
    // Validacions
    if (username.isEmpty()) {
      return Completable.error(new Throwable("Nom d'usuari no pot estar buit"));
    } else if (email.isEmpty()) {
      return Completable.error(new Throwable("E-Mail no pot estar buit"));
    } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
      return Completable.error(new Throwable("E-Mail no vàlid"));
    } else if (password.isEmpty()) {
      return Completable.error(new Throwable("Contrassenya no pot estar buida"));
    } else if (passwordConfirmation.isEmpty()) {
      return Completable.error(new Throwable("La confirmació de contrassenya no pot estar buida"));
    } else if (!password.equals(passwordConfirmation)) {
      return Completable.error(new Throwable("Contrassenyes no coincideixen"));
    }

    //RX
    return clientRepository.getClientById(username)
            .flatMapCompletable(existingClient ->
                    Completable.error(new Throwable("Usuari ja existent"))
            )
            .onErrorResumeNext(throwable -> {
              // Si el client no existeix, fem la creació
              if ("NotFound".equals(throwable.getMessage())) {
                Client client = new Client(new ClientId(username), email, password, imageUrl);
                return clientRepository.add(client);
              } else {
                return Completable.error(throwable);
              }
            });
  }

}