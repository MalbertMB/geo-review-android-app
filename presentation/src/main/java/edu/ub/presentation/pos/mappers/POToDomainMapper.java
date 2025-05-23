package edu.ub.presentation.pos.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.stream.Collectors;

import edu.ub.domain.model.entities.Client;
import edu.ub.domain.model.entities.Valoration;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.Rating;
import edu.ub.presentation.pos.ClientPO;
import edu.ub.presentation.pos.ValorationPO;

/*
 * This class maps PO (Presentation Object) to Domain entities.
 */
public class POToDomainMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        // ClientPO -> Client
        modelMapper.addConverter(new AbstractConverter<ClientPO, Client>() {
            @Override
            protected Client convert(ClientPO source) {
                return new Client(
                        new ClientId(source.getId().getId()),
                        source.getEmail(),
                        source.getPassword(),
                        source.getPhotoUrl()
                );
            }
        });

        // ValorationPO -> Valoration
        modelMapper.addConverter(new AbstractConverter<ValorationPO, Valoration>() {
            @Override
            protected Valoration convert(ValorationPO source) {
                return new Valoration(
                        source.getUid(),
                        source.getToiletUid(),
                        source.getClientId(),
                        new Rating((int) source.getRating()),
                        source.getComment(),
                        source.getImg_Url(),
                        source.getDate().toString()
                );
            }
        });
    }

    public static <S, T> T mapObject(S source, Class<T> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
