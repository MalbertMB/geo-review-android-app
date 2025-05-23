package edu.ub.features.fe.usecase.implementation;

import edu.ub.domain.model.entities.Client;
import edu.ub.features.fe.repositories.ClientRepository;
import edu.ub.features.fe.usecase.UpdateClientUseCase;
import io.reactivex.rxjava3.core.Completable;

public class UpdateClientUseCaseImpl implements UpdateClientUseCase {

    private final ClientRepository clientRepository;

    public UpdateClientUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Completable execute(Client client) {
        return clientRepository.updateClient(client);
    }
}
