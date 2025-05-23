package edu.ub.features.fe.usecase.implementation;


import edu.ub.domain.model.entities.Client;
import edu.ub.domain.model.entities.Toilet;
import edu.ub.features.fe.repositories.ClientRepository;
import edu.ub.features.fe.usecase.GetClientByIdUseCase;
import io.reactivex.rxjava3.core.Observable;

public class GetClientByIdUseCaseImpl implements GetClientByIdUseCase {

    private final ClientRepository clientRepository;

    public GetClientByIdUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    //Prog Reactiva
    public Observable<Client> execute(String id){
        return clientRepository.getClientById(id);

    }



}
